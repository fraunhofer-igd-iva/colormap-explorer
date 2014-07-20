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

import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import algorithms.sampling.EvenDistributedDistancePoints;
import colormaps.Colormap2D;
import colormaps.ConstantColormap2D;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

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
	
	private Colormap2D oldColormap;
	
	double deriveMedianColormapToJNDRatio(Colormap2D colormap, Iterator<Point2D> points, int lines) {
		if (lines == 0)
			return Double.NaN;
		double[] ratios = new double[lines];
	
		for (int i = 0; i < lines; i++)
		{
			Point2D p1 = points.next();
			Point2D p2 = points.next();
	
			float dist = (float) p1.distance(p2);
			
			Color colorA = colormap.getColor(p1.getX(), p1.getY());
			Color colorB = colormap.getColor(p2.getX(), p2.getY());
			
			// roughly 0-100
			double cdist = MismatchScatterplotPanel.colorDiff(colorA, colorB);
			
			double ratio = cdist / dist;
			ratios[i] = ratio;
		}
		Arrays.sort(ratios);
		return ratios[lines/2];
	}

	/**
	 * Default constructor
	 */
	public AnalysisPanel()
	{
		int rows = 2;
		int cols = 1;
		
		setLayout(new GridLayout(rows, cols, 5, 5));
		
		MismatchScatterplotPanel mmPanelDirect = new MismatchScatterplotPanel(new ConstantColormap2D(Color.WHITE), false);
		MismatchScatterplotPanel mmPanelLog = new MismatchScatterplotPanel(new ConstantColormap2D(Color.WHITE), true);
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

		Colormap2D colormap = event.getSelection();
		List<Point2D> points = Lists.newArrayList(new EvenDistributedDistancePoints(new Random(123), lines).getPoints());
		double median = deriveMedianColormapToJNDRatio(colormap, points.iterator(), points.size()/2);
		for (MismatchScatterplotPanel panel : panels)
		{
			panel.setPointSource(points, points.size()/2, median);
			panel.setColormap(colormap);
		}
	}
}
