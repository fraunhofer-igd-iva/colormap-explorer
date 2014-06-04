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

import static colormaps.transformed.SimpleFilteredColormap2D.ViewType.*;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import colormaps.Colormap2D;
import colormaps.ConstantColormap2D;
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
public class DecomposedViewPanel extends JPanel
{
	private static final long serialVersionUID = -222462739962919021L;

	private int rows = 3;
	private int cols = 3;
	
	private final DecomposedPanel[][] panels = new DecomposedPanel[rows][cols];
	private final List<ViewType> types = ImmutableList.of(REAL, LUMINANCE, TEST, RED, GREEN, BLUE, HUE, SATURATION, VALUE);

	/**
	 * Default constructor
	 */
	public DecomposedViewPanel()
	{
		setLayout(new GridLayout(rows, cols, 5, 5));
		
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				DecomposedPanel mini = new DecomposedPanel(new ConstantColormap2D(Color.WHITE));
				panels[row][col] = mini;
				add(mini);
			}
		}
		
		// get last selection event and trigger it manually to be up to date
		ColormapSelectionEvent selEvent = MyEventBus.getLast(ColormapSelectionEvent.class);
		if (selEvent != null)
		{
			onSelect(selEvent);
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
