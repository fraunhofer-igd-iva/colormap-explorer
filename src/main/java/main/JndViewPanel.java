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


package main;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algorithms.JndRegionComputer;
import colormaps.CachedColormap2D;
import colormaps.Colormap2D;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

import events.ColormapSelectionEvent;
import events.MyEventBus;

/**
 * Display points with a certain jnd distance
 * @author Martin Steiger
 */
public class JndViewPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(JndViewPanel.class);
	
	private static final long serialVersionUID = 5994307533367447487L;

	private CachedColormap2D colormap;
	private Colormap2D orgColormap;

	private final JCheckBox drawColormap;
	private final JCheckBox drawRegions;
	
	private JndRegionComputer regionComputer;
	
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

		JButton saveImageBtn = new JButton("Save Image");
		saveImageBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					String safeFilename = colormap.getName().replaceAll("\\W+", "_");
					File tmpFile = Files.createTempFile("colormap_explorer_" + safeFilename + "_", ".png").toFile();
					renderToImage(tmpFile);
					Desktop dt = Desktop.getDesktop();
				    dt.open(tmpFile);
				}
				catch (IOException e)
				{
					logger.error("Could not save file", e);
				}
			}
		});
		panel.add(saveImageBtn);

		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		add(panel, BorderLayout.NORTH);
		add(jndView, BorderLayout.CENTER);
	}
	
	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		colormap = new CachedColormap2D(event.getSelection(), 512, 512);

		regionComputer = new JndRegionComputer(colormap, 3.0);

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

	private void renderToImage(File file)
	{
		BufferedImage bi = new BufferedImage(getScreenWidth(), getScreenHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D gi = bi.createGraphics();
		
		drawColormap(gi);
		drawJndPoints(gi);
		drawJndRegions(gi);
		
		gi.dispose();
		
		try {
			ImageIO.write(bi, "png", file);
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
	
	private double screenXtoMapX(double sx)
	{
		return sx / (getScreenWidth() - 1);
	}

	private double screenYtoMapY(double sy)
	{
		return sy / (getScreenHeight() - 1);
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
			Polygon poly = createPolygon(pts);
			g.setColor(new Color(regionComputer.getPColor(jndPt).getARGB()));
			g.fill(poly);
		}
	}
	
	private void drawJndRegions(Graphics2D g)
	{
		g.setColor(Color.BLACK);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (Point2D jndPt : regionComputer.getPoints())
		{
			List<Point2D> pts = regionComputer.getRegion(jndPt);
			Polygon poly = createPolygon(pts);
			g.draw(poly);
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

