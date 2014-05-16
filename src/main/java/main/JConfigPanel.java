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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tiling.Tile;
import colormaps.Colormap2D;

import com.google.common.eventbus.Subscribe;

import events.ColormapSelectionEvent;
import events.MyEventBus;
import events.TileSelectionEvent;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class JConfigPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(JConfigPanel.class);
	
	private static final long serialVersionUID = -3147864756762616815L;
	private JTextField tfX;
	private JTextField tfY;

	public JConfigPanel(List<Colormap2D> colorMaps)
	{
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		final JComboBox<Colormap2D> mapsCombo = new JComboBox<Colormap2D>(colorMaps.toArray(new Colormap2D[0]));
		mapsCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Colormap2D colormap = (Colormap2D) mapsCombo.getSelectedItem();
				MyEventBus.getInstance().post(new ColormapSelectionEvent(colormap));
				logger.debug("Selected colormap " + colormap);
			}
		});
		
		JLabel label = new JLabel("Colormap");
		label.setAlignmentX(Box.CENTER_ALIGNMENT);
		panel.add(label);
		panel.add(mapsCombo);
		
		panel.add(Box.createVerticalStrut(10));

		JPanel tileInfo = new JPanel();
		tileInfo.setLayout(new GridLayout(0, 2, 5, 5));
		tileInfo.setBorder(BorderFactory.createTitledBorder("Info"));
		tfX = new JTextField();
		tfY = new JTextField();
		tfX.setEditable(false);
		tfY.setEditable(false);
		tfX.setHorizontalAlignment(JTextField.RIGHT);
		tfY.setHorizontalAlignment(JTextField.RIGHT);
		tfX.setBackground(Color.WHITE);
		tfY.setBackground(Color.WHITE);
		tileInfo.add(new JLabel("X"));
		tileInfo.add(tfX);
		tileInfo.add(new JLabel("Y"));
		tileInfo.add(tfY);
		panel.add(tileInfo);
		
		MyEventBus.getInstance().register(this);
	}

	@Subscribe
	public void onSelect(TileSelectionEvent event)
	{
		Set<Tile> tiles = event.getSelection();
		
		if (tiles.isEmpty())
		{
			tfX.setText(null);
			tfY.setText(null);
		}
		else
		{
			// TODO: support more than one selected tile
			// TODO: use tile model and colormap to show more info
			Tile tile = tiles.iterator().next();
			tfX.setText("" + tile.getMapX());
			tfY.setText("" + tile.getMapY());
		}
	}
}
