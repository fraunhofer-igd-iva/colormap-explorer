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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.JPanel;

import colormaps.Colormap2D;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

import events.ColormapSelectionEvent;
import events.MyEventBus;

/**
 * Display points with a certain jnd distance
 * @author Martin Steiger
 */
public class JndViewPanel extends JPanel
{
	private static final long serialVersionUID = 5994307533367447487L;

	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);

	private Colormap2D colormap;
	
	private Map<Point2D, PColor> jndPoints = Maps.newHashMap();

	private double jndThreshold = 5.0;
	
	public JndViewPanel()
	{
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
		colormap = event.getSelection();
		updateRegions();
		repaint();
	}

	private void updateRegions()
	{
		jndPoints.clear();
		
		int sampleRate = 100;
		
		for (int y = 0; y < sampleRate; y++)
		{
			float my = y / (float)(sampleRate - 1);
			for (int x = 0; x < sampleRate; x++)
			{
				float mx = x / (float)(sampleRate - 1);
				boolean ok = true;
				
				Color color = colormap.getColor(mx, my);
				PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

				for (PColor expcolor : jndPoints.values())
				{
					double dist = ColorTools.distance(pcolor, expcolor, VIEW_ENV);
					if (dist < jndThreshold)
					{
						ok = false;
						break;
					}
				}
				
				if (ok)
				{
					jndPoints.put(new Point2D.Double(mx, my), pcolor);
				}
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		drawColormap(g);
	}

	private int getScreenWidth()
	{
		return Math.min(getWidth(), getHeight());
	}

	private int getScreenHeight()
	{
		return Math.min(getWidth(), getHeight());
	}

	private double mapXtoScreenX(double mx)
	{
		return mx * (getScreenWidth() - 1);
	}

	private double mapYtoScreenY(double my)
	{
		return my * (getScreenHeight() - 1);
	}
	
	private double screenXtoMapX(double sx)
	{
		return sx / (getScreenWidth() - 1);
	}

	private double screenYtoMapY(double sy)
	{
		return sy / (getScreenHeight() - 1);
	}

	private void drawColormap(Graphics2D g)
	{
		for (int y = 0; y < getScreenHeight(); y++)
		{
			double my = screenYtoMapY(y);
			for (int x = 0; x < getScreenWidth(); x++)
			{
				double mx = screenXtoMapX(x);
				g.setColor(colormap.getColor((float)mx, (float)my));
				g.drawLine(x, y, x, y);
			}
		}
	}
}
