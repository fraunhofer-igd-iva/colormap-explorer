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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.colormaps.CachedColormap;
import de.fhg.igd.iva.explorer.events.ColormapSelectionEvent;
import de.fhg.igd.iva.explorer.events.MyEventBus;

/**
 * A panel that allows for exporting color maps as images. 
 * @author Martin Steiger
 */
public class ExportView extends JPanel
{
	private static final long serialVersionUID = 6376448813807357178L;
	
	private static final Logger logger = LoggerFactory.getLogger(ExportView.class);
	

	private final JFileChooser fileChooser = FileDialogs.createSaveImageDialog();

	private final JSpinner widthSpinner;
	private final JSpinner heightSpinner;

	private CachedColormap colormap;

	private SpinnerNumberModel widthSpinnerModel;
	private SpinnerNumberModel heightSpinnerModel;
	
	public ExportView() 
	{
		MyEventBus.getInstance().register(this);
		
		setLayout(new BorderLayout(5, 5));
		
		JComponent cmView = new JComponent()
		{
			private static final long serialVersionUID = 240761518096949199L;

			@Override
			protected void paintComponent(Graphics g)
			{
				int size = Math.min(getWidth(), getHeight());
				g.drawImage(colormap.getImage(), 0, 0, size, size, null);
			}
		};

		JPanel panel = new JPanel();
//		panel.setPreferredSize(new Dimension(100, 120));
//		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JLabel dimLabel = new JLabel("Dimensions");
		dimLabel.setAlignmentX(0);
		widthSpinnerModel = new SpinnerNumberModel(512, 32, 2048, 32);
		heightSpinnerModel = new SpinnerNumberModel(512, 32, 2048, 32);
		widthSpinner = new JSpinner(widthSpinnerModel);
//		widthSpinner.setPreferredSize(new Dimension(60, 25));
		heightSpinner = new JSpinner(heightSpinnerModel);
//		heightSpinner.setPreferredSize(new Dimension(60, 25));
		
		panel.add(Box.createVerticalGlue());
		panel.add(dimLabel);
		panel.add(widthSpinner);
		panel.add(heightSpinner);
		panel.add(Box.createVerticalStrut(20));
//		panel.add(Box.createVerticalGlue());
		
		JButton exportButton = new JButton("Export");
		exportButton.setAlignmentX(0.5f);
		exportButton.setPreferredSize(new Dimension(120, 35));
		exportButton.setIcon(new ImageIcon(getClass().getResource("/icons/save_as.png")));
		exportButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveImageToFile();
			}
		});
		panel.add(exportButton);
		panel.add(Box.createVerticalStrut(20));

		add(cmView, BorderLayout.CENTER);
		
		JPanel wrap = new JPanel();
		wrap.setLayout(new FlowLayout());
		wrap.add(panel);
		add(wrap, BorderLayout.EAST);
	}
	
	@Override
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);

		if (aFlag)
		{
			ColormapSelectionEvent event = MyEventBus.getLast(ColormapSelectionEvent.class);
			if (colormap == null || event.getSelection() != colormap.getOriginal())
			{
				onSelect(event);
			}
		}
	}
	
	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		colormap = new CachedColormap(event.getSelection(), 512, 512);
	}
	
	private void saveImageToFile()
	{
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				File file = fileChooser.getSelectedFile();
				
				// TODO: this snippet also exists in the jnd view 
				
				// JFileChooser does not enforce an extension or set a default
				int extIdx = file.getName().lastIndexOf('.');
				String ext = extIdx >= 0 ? file.getName().substring(extIdx + 1) : null;
				if (ext == null) {
					ext = "png";
					file = new File(file.getAbsolutePath() + "." + ext);
				}

				int width = widthSpinnerModel.getNumber().intValue();
				int height = heightSpinnerModel.getNumber().intValue();
				renderToImage(file, ext, width, height);
				
				// open it with default (viewer) software
				Desktop dt = Desktop.getDesktop();
			    dt.open(file);
			}
			catch (IOException e)
			{
				logger.error("Could not save file", e);
			}
			finally
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	
	private void renderToImage(File file, String format, int width, int height) throws IOException
	{
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D gi = bi.createGraphics();

		float maxY = height - 1;
		float maxX = width - 1;

		WritableRaster raster = bi.getRaster();

		for (int y = 0; y < height; y++)
		{
			float my = y / maxY;
			for (int x = 0; x < width; x++)
			{
				float mx = x / maxX;
				Color color = colormap.getUncachedColor(mx, my);

				raster.setSample(x, y, 0, color.getRed());
				raster.setSample(x, y, 1, color.getGreen());
				raster.setSample(x, y, 2, color.getBlue());
			}
		}

		gi.dispose();

		ImageIO.write(bi, format, file);
	}
}

