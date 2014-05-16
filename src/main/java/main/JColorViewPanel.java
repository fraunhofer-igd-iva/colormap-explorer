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

import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.BLUE;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.GREEN;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.HUE;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.REAL;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.RED;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.SATURATION;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.TEST1;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.TEST2;
import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.VALUE;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import colormaps.Colormap2D;
import colormaps.impl.ConstantColormap2D;
import colormaps.transformed.SimpleFilteredColormap2D;
import colormaps.transformed.TransformedColormap2D;
import colormaps.transformed.SimpleFilteredColormap2D.ViewType;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import events.ColormapSelectionEvent;
import events.MyEventBus;

/**
 * The main display panel (right side)
 * @author Martin Steiger
 */
public class JColorViewPanel extends JPanel
{
	private static final long serialVersionUID = -222462739962919021L;

	private int rows = 3;
	private int cols = 3;
	
	private final JMiniHexPanel[][] panels = new JMiniHexPanel[rows][cols];
	private final List<ViewType> types = ImmutableList.of(REAL, TEST1, TEST2, RED, GREEN, BLUE, HUE, SATURATION, VALUE);

	/**
	 * Default constructor
	 */
	public JColorViewPanel()
	{
		setLayout(new GridLayout(rows, cols, 5, 5));
		
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				JMiniHexPanel mini = new JMiniHexPanel(new ConstantColormap2D(Color.WHITE));
				panels[row][col] = mini;
				add(mini);
			}
		}
		
		MyEventBus.getInstance().register(this);
	}
	
	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		int typeIdx = 0;
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				Colormap2D colormap = event.getSelection();
				TransformedColormap2D tColormap = new SimpleFilteredColormap2D(colormap, types.get(typeIdx++));
				panels[row][col].setColorMap(tColormap);
			}
		}
	}
	
}
