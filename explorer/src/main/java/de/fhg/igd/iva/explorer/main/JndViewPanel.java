/*
 * Copyright 2014 Fraunhofer IGD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package de.fhg.igd.iva.explorer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import progress.LoggingProgressListener;
import progress.SwingProgressListener;
import algorithms.JndRegionComputer;
import algorithms.sampling.CircularSampling;

import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.colormaps.CachedColormap;
import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.explorer.events.ColormapSelectionEvent;
import de.fhg.igd.iva.explorer.events.MyEventBus;

/**
 * Display points with a certain jnd distance
 * @author Martin Steiger
 */
public class JndViewPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(JndViewPanel.class);

	private static final long serialVersionUID = 5994307533367447487L;

	private CachedColormap colormap;
	private Colormap orgColormap;

	private final JCheckBox drawColormap;
	private final JCheckBox drawRegions;
	private final JComboBox<Double> jndDistanceCombo;
	private final JComboBox<RegionSampling> jndRegionCombo;

	private JndRegionComputer regionComputer;

	private final JFileChooser fileChooser = FileDialogs.createSaveImageDialog();

	private static class RegionSampling
	{
		private final int angles;
		private final double stepSize;

		public RegionSampling(int angles, double stepSize)
		{
			this.angles = angles;
			this.stepSize = stepSize;
		}

		public int getAngles()
		{
			return angles;
		}

		public double getStepSize()
		{
			return stepSize;
		}

		@Override
		public String toString()
		{
			return String.format("%d - %.4f", angles, stepSize);
		}
	}

	public JndViewPanel()
	{
		MyEventBus.getInstance().register(this);

		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		ActionListener repaintListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JndViewPanel.this.repaint();
			}
		};

		Component jndView = new JComponent()
		{
			private static final long serialVersionUID = 240761518096949199L;

			@Override
			protected void paintComponent(Graphics g)
			{
				drawJndView(g);
			}
		};


		drawColormap = new JCheckBox("Draw colormap", true);
		drawColormap.addActionListener(repaintListener);
		panel.add(drawColormap);

		drawRegions = new JCheckBox("Draw regions", true);
		drawRegions.addActionListener(repaintListener);
		panel.add(drawRegions);

		jndDistanceCombo = new JComboBox<Double>();
		jndDistanceCombo.addItem(1.0);
		jndDistanceCombo.addItem(2.0);
		jndDistanceCombo.addItem(3.0);
		jndDistanceCombo.addItem(4.0);
		jndDistanceCombo.addItem(5.0);
		jndDistanceCombo.setSelectedIndex(2);
		panel.add(new JLabel("JND Threshold"));
		panel.add(jndDistanceCombo);

		jndRegionCombo = new JComboBox<RegionSampling>();
		jndRegionCombo.addItem(new RegionSampling(32, 0.005));
		jndRegionCombo.addItem(new RegionSampling(64, 0.002));
		jndRegionCombo.addItem(new RegionSampling(128, 0.0005));
		jndDistanceCombo.setSelectedIndex(1);
		panel.add(new JLabel("Region Sampling"));
		panel.add(jndRegionCombo);

		ActionListener updateSamplingListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateSampling();
			}
		};
		jndRegionCombo.addActionListener(updateSamplingListener);
		jndDistanceCombo.addActionListener(updateSamplingListener);

		JButton saveImageBtn = new JButton("Save Image");
		saveImageBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				saveImageToFile();
			}
		});
		panel.add(saveImageBtn);

		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		add(panel, BorderLayout.NORTH);
		add(jndView, BorderLayout.CENTER);
	}

	private void saveImageToFile()
	{
		if (fileChooser.showSaveDialog(JndViewPanel.this) == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File file = fileChooser.getSelectedFile();
				int extIdx = file.getName().lastIndexOf('.');
				String ext = extIdx >= 0 ? file.getName().substring(extIdx + 1) : null;
				if (ext == null) {
					ext = "png";
					file = new File(file.getAbsolutePath() + "." + ext);
				}

				renderToImage(file, ext);
				Desktop dt = Desktop.getDesktop();
			    dt.open(file);
			}
			catch (IOException e)
			{
				logger.error("Could not save file", e);
			}
		}
	}

	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		colormap = new CachedColormap(event.getSelection(), 512, 512);

		int sampleRate = 200;
//		GridSampling sampling = new GridSampling(sampleRate);
		CircularSampling sampling = new CircularSampling(sampleRate);
		regionComputer = new JndRegionComputer(colormap, sampling);

		updateSampling();
	}

	private void updateSampling()
	{
		Double distanceThreshold = jndDistanceCombo.getItemAt(jndDistanceCombo.getSelectedIndex());
		RegionSampling regionSampling = jndRegionCombo.getItemAt(jndRegionCombo.getSelectedIndex());
		regionComputer.setJndThreshold(distanceThreshold.doubleValue());
		regionComputer.setRegionSampling(regionSampling.getAngles(), regionSampling.getStepSize());

		final SwingProgressListener progress1 = new SwingProgressListener(this, "Computing JND points")
		{
			private int oldPoints = 0;

			@Override
			public void step()
			{
				super.step();

				int points = regionComputer.getPoints().size();

				if (points != oldPoints)
				{
					oldPoints = points;
					JndViewPanel.this.repaint();
				}
			}
		};

		final SwingProgressListener progress2 = new SwingProgressListener(this, "Updating Regions")
		{
			private int oldRegions = 0;

			@Override
			public void step()
			{
				super.step();

				int regions = regionComputer.getRegionCount();

				if (regions != oldRegions)
				{
					oldRegions = regions;
					JndViewPanel.this.repaint();
				}
			}
		};

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				regionComputer.computePoints(progress1);
				regionComputer.computeJndRegions(progress2);

				return null;
			}
		};
		worker.execute();

		repaint();
	}

	@Override
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);

		if (aFlag)
		{
			ColormapSelectionEvent event = MyEventBus.getLast(ColormapSelectionEvent.class);
			if (event.getSelection() != orgColormap)
			{
				onSelect(event);
				orgColormap = event.getSelection();
			}
		}
	}


	protected void drawJndView(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;

		if (drawColormap.isSelected())
		{
			drawColormap(g);
		}

		drawJndPoints(g);

		if (drawRegions.isSelected())
		{
			drawJndRegions(g);
		}
		else
		{
			fillJndRegions(g);
		}

	}

	private void renderToImage(File file, String format)
	{
		BufferedImage bi = new BufferedImage(getScreenWidth(), getScreenHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D gi = bi.createGraphics();

		drawColormap(gi);
		drawJndPoints(gi);
		drawJndRegions(gi);

		gi.dispose();

		try {
			ImageIO.write(bi, format, file);
		}
		catch (IOException e) {
			logger.error("Could not save to file", e);
		}
	}

	private int getScreenWidth()
	{
		return Math.min(getWidth(), getHeight());
	}

	private int getScreenHeight()
	{
		return Math.min(getWidth(), getHeight());
	}

	private double mapXtoScreenX(double mx)
	{
		return mx * (getScreenWidth() - 1);
	}

	private double mapYtoScreenY(double my)
	{
		return my * (getScreenHeight() - 1);
	}

	private void drawColormap(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.drawImage(colormap.getImage(), 0, 0, getScreenWidth(), getScreenHeight(), null);
	}

	private void drawJndPoints(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		for (Point2D pt : regionComputer.getPoints())
		{
			int r = 2;
			int wx = (int) mapXtoScreenX(pt.getX());
			int wy = (int) mapYtoScreenY(pt.getY());
			g.fillOval(wx - r, wy - r, 2 * r , 2 * r);
		}
	}

	private void fillJndRegions(Graphics2D g)
	{
		for (Point2D jndPt : regionComputer.getPoints())
		{
			List<Point2D> pts = regionComputer.getRegion(jndPt);
			if (pts != null)	// the computation could have been cancelled and the region undefined
			{
				Polygon poly = createPolygon(pts);
				g.setColor(new Color(regionComputer.getPColor(jndPt).getARGB()));
				g.fill(poly);
			}
		}
	}

	private void drawJndRegions(Graphics2D g)
	{
		g.setColor(Color.BLACK);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (Point2D jndPt : regionComputer.getPoints())
		{
			List<Point2D> pts = regionComputer.getRegion(jndPt);
			if (pts != null)
			{
				Polygon poly = createPolygon(pts);
				g.draw(poly);
			}
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
	}

	private Polygon createPolygon(List<Point2D> pts)
	{
        int[] x = new int[pts.size()];
        int[] y = new int[pts.size()];

        for (int i = 0; i < pts.size(); i++)
        {
			int wx = (int) mapXtoScreenX(pts.get(i).getX());
			int wy = (int) mapYtoScreenY(pts.get(i).getY());
            x[i] = wx;
            y[i] = wy;
        }

        return new Polygon(x, y, pts.size());
    }


}

