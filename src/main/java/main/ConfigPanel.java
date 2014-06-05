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
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tiling.Tile;
import tiling.TileModel;
import colorSpaces.CIELAB;
import colorSpaces.CIELABLch;
import colormaps.Colormap2D;

import com.google.common.eventbus.Subscribe;

import events.ColormapSelectionEvent;
import events.MyEventBus;
import events.TileSelectionEvent;

/**
 * The config and info panel at the left
 * @author Martin Steiger
 */
public class ConfigPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigPanel.class);
	
	private static final long serialVersionUID = -3147864756762616815L;

	private final JPanel tileInfoPanel;

	/**
	 * @param colorMaps the (sorted) list of all available color maps
	 */
	public ConfigPanel(List<Colormap2D> colorMaps)
	{
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		final JComboBox<Colormap2D> mapsCombo = new JComboBox<Colormap2D>(colorMaps.toArray(new Colormap2D[0]));
		
		final JPanel cmPanel = new JPanel(new BorderLayout(5, 5));
		cmPanel.add(new JLabel("Colormap"), BorderLayout.NORTH);
		cmPanel.add(mapsCombo, BorderLayout.CENTER);
		
		final JLabel descLabel = new JLabel();
		descLabel.setBorder(BorderFactory.createTitledBorder("Description"));
		cmPanel.add(descLabel, BorderLayout.SOUTH);

		add(cmPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		
		tileInfoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		tileInfoPanel.setBorder(BorderFactory.createTitledBorder("Tile Info"));
		centerPanel.add(tileInfoPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);

		mapsCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Colormap2D colormap = (Colormap2D) mapsCombo.getSelectedItem();
				descLabel.setText("<html>" + colormap.getDescription() + "</html>");
				MyEventBus.getInstance().post(new ColormapSelectionEvent(colormap));
				logger.debug("Selected colormap " + colormap);
			}
		});
		mapsCombo.setSelectedIndex(0);

		MyEventBus.getInstance().register(this);
	}

	@Subscribe
	public void onSelect(TileSelectionEvent event)
	{
		tileInfoPanel.removeAll();

		Colormap2D colormap = event.getColormap();
		TileModel tileModel = event.getTileModel();
		
		Set<Tile> tiles = event.getSelection();

		for (Tile tile : tiles)
		{
			int x = tile.getMapX();
			int y = tile.getMapY();
			int worldX = tileModel.getWorldX(x, y);
			int worldY = tileModel.getWorldY(x, y);
			
			float mapX = (float)worldX / tileModel.getWorldWidth();
			float mapY = (float)worldY / tileModel.getWorldHeight();
			
			Color color = colormap.getColor(mapX, mapY);

			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();

			int lum = (int) (0.299f * red + 0.587f * green + 0.114f * blue + 0.5);
			float[] hsb = Color.RGBtoHSB(red, green, blue, null);
			double[] lch = new CIELABLch().fromColor(color);
			
			addInfo(tileInfoPanel, "Relative X", String.format("%.3f", mapX));
			addInfo(tileInfoPanel, "Relative Y", String.format("%.3f", mapY));

			addInfo(tileInfoPanel, "Red", String.valueOf(red));
			addInfo(tileInfoPanel, "Green", String.valueOf(green));
			addInfo(tileInfoPanel, "Blue", String.valueOf(blue));
			
			addInfo(tileInfoPanel, "Hue", String.valueOf((int)(hsb[0] * 360)) + " °");
			addInfo(tileInfoPanel, "Saturation", String.valueOf((int)(hsb[1] * 100)) + "%");
			addInfo(tileInfoPanel, "Value", String.valueOf((int)(hsb[2] * 100)) + "%");
			addInfo(tileInfoPanel, "Luminance", String.valueOf(lum));
			addInfo(tileInfoPanel, "Lightness", String.valueOf(lch[0]));
			addInfo(tileInfoPanel, "Chroma", String.valueOf(lch[1]));
			addInfo(tileInfoPanel, "Attention", String.valueOf(Math.sqrt(lch[0]*lch[0]+lch[1]*lch[1])/100.0));
		}
		
		tileInfoPanel.revalidate();
	}

	private void addInfo(Container c, String label, String value)
	{
		JTextField tfX = new JTextField(value);
		tfX.setEditable(false);
		tfX.setHorizontalAlignment(SwingConstants.RIGHT);
		tfX.setBackground(Color.WHITE);
		c.add(new JLabel(label));
		c.add(tfX);
	}
}
