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
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import algorithms.MedianDivergenceComputer;
import algorithms.sampling.EvenDistributedDistancePoints;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.ConstantColormap;
import events.ColormapSelectionEvent;
import events.MyEventBus;

/**
 * A grid of Analysis panels
 * @author Simon Thum
 */
public class AnalysisPanel extends JPanel
{
	private static final long serialVersionUID = 4842610449905121603L;

	private final List<MismatchScatterplotPanel> panels = Lists.newArrayList();
	
	private int lines = 10000;
	
	private Colormap oldColormap;
	
	/**
	 * Default constructor
	 */
	public AnalysisPanel()
	{
		int rows = 2;
		int cols = 1;
		
		setLayout(new GridLayout(rows, cols, 5, 5));
		
		MismatchScatterplotPanel mmPanelDirect = new MismatchScatterplotPanel(new ConstantColormap(Color.WHITE), false);
		MismatchScatterplotPanel mmPanelLog = new MismatchScatterplotPanel(new ConstantColormap(Color.WHITE), true);
		mmPanelDirect.setBackground(Color.WHITE);
		mmPanelLog.setBackground(Color.WHITE);
		panels.add(mmPanelDirect);
		panels.add(mmPanelLog);
		add(mmPanelDirect);
		add(mmPanelLog);
		
		MyEventBus.getInstance().register(this);
	}

	@Override
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);
		
		if (aFlag)
		{
			ColormapSelectionEvent event = MyEventBus.getLast(ColormapSelectionEvent.class);
			if (event.getSelection() != oldColormap)
			{
				onSelect(event);
				oldColormap = event.getSelection();
			}
		}
	}

	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		Colormap colormap = event.getSelection();
		List<Point2D> points = Lists.newArrayList(new EvenDistributedDistancePoints(new Random(123), lines).getPoints());
		MedianDivergenceComputer median = MedianDivergenceComputer.fromPoints(colormap, points);
		for (MismatchScatterplotPanel panel : panels)
		{
			panel.setPointSource(points, points.size()/2, median);
			panel.setColormap(colormap);
		}
	}
}
