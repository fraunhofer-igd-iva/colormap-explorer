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
import java.util.List;

import javax.swing.JPanel;

import colormaps.Colormap2D;
import colormaps.impl.ConstantColormap2D;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import events.ColormapSelectionEvent;
import events.MyEventBus;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class PointsExampleViewPanel extends JPanel
{
	private static final long serialVersionUID = 4842610449905121603L;

	private final List<Color> backgrounds = ImmutableList.of(Color.WHITE, Color.GRAY, Color.BLACK);
	private final List<Integer> alphas = ImmutableList.of(64, 128, 192, 255);

	private final List<PointsExamplePanel> panels = Lists.newArrayList();
	
	/**
	 * Default constructor
	 */
	public PointsExampleViewPanel()
	{
		int rows = backgrounds.size();
		int cols = alphas.size();
		
		setLayout(new GridLayout(rows, cols, 5, 5));
		
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				PointsExamplePanel mini = new PointsExamplePanel(new ConstantColormap2D(Color.WHITE));
				mini.setBackground(backgrounds.get(row));
				mini.setAlpha(alphas.get(col));
				panels.add(mini);
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
		Colormap2D colormap = event.getSelection();
		for (PointsExamplePanel panel : panels)
		{
			panel.setColormap(colormap);
		}
	}
}
