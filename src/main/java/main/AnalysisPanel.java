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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

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
	
	// list of lines across which to compare as pairs of pairs of floats
	List<Float> makeLinePoints() {
		List<Float> list = new ArrayList<>(lines*4);
		Random r = new Random(123456);
		
		for (int i = 0; i < lines; i++)
		{
			float ax = r.nextFloat();
			float ay = r.nextFloat();
			
			float bx = 0, by = 0;
			
			final float dist = r.nextFloat();
			
			int tries = 10;
			while (tries >= 1) {
				float angle = (float) (r.nextFloat() * Math.PI * 2.0);
				
				bx = (float) (ax + sin(angle) * dist); 
				by = (float) (ay + cos(angle) * dist);
				if (bx >= 0.0 && bx <= 1.0 && by >= 0.0 && by <= 1.0)
					break;
				tries --;
			}
			if (tries == 0)
				continue;  // should not happen often, distorts scatterplot
			list.add(ax);
			list.add(ay);
			list.add(bx);
			list.add(by);
		}
		return list;
	}
	
	double deriveMedianColormapToJNDRatio(Colormap2D colormap, Iterator<Float> floats, int lines) {
		if (lines == 0)
			return Double.NaN;
		double[] ratios = new double[lines];
	
		for (int i = 0; i < lines; i++)
		{
			float ax = floats.next();
			float ay = floats.next();
			float bx = floats.next();
			float by = floats.next();
	
			float dist = (float) hypot(ax-bx, ay-by);
			
			Color colorA = colormap.getColor(ax, ay);
			Color colorB = colormap.getColor(bx, by);
			
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
		List<Float> linePoints = makeLinePoints();
		int actualLines = linePoints.size()/4;
		double median = deriveMedianColormapToJNDRatio(colormap, linePoints.iterator(), actualLines);
		for (MismatchScatterplotPanel panel : panels)
		{
			panel.setPointSource(linePoints, actualLines, median);
			panel.setColormap(colormap);
		}
	}
}
