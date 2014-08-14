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

import static views.decomposed.SimpleColormapView.ViewType.ATT;
import static views.decomposed.SimpleColormapView.ViewType.BLUE;
import static views.decomposed.SimpleColormapView.ViewType.GREEN;
import static views.decomposed.SimpleColormapView.ViewType.HUE;
import static views.decomposed.SimpleColormapView.ViewType.LUM;
import static views.decomposed.SimpleColormapView.ViewType.REAL;
import static views.decomposed.SimpleColormapView.ViewType.RED;
import static views.decomposed.SimpleColormapView.ViewType.SAT;
import static views.decomposed.SimpleColormapView.ViewType.VAL;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import views.decomposed.SimpleColormapView;
import views.decomposed.SimpleColormapView.ViewType;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.ConstantColormap;
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
	private final List<ViewType> types = ImmutableList.of(REAL, LUM, ATT, RED, GREEN, BLUE, HUE, SAT, VAL);

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
				ConstantColormap colormap = new ConstantColormap(Color.WHITE);
				SimpleColormapView view = new SimpleColormapView(colormap, REAL);
				DecomposedPanel mini = new DecomposedPanel(view);
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
				Colormap colormap = event.getSelection();
				SimpleColormapView tColormap = new SimpleColormapView(colormap, types.get(typeIdx++));
				panels[row][col].setColorMapView(tColormap);
			}
		}
	}
	
}
