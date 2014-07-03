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
import java.util.Set;

import javax.swing.JPanel;

import colormaps.Colormap2D;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

import events.ColormapSelectionEvent;
import events.MyEventBus;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class JndViewPanel extends JPanel
{
	private static final long serialVersionUID = 5994307533367447487L;

	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);

	private Colormap2D colormap;
	
	private Set<Point2D> jndPoints = Sets.newHashSet();
	
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

				for (Point2D pt : jndPoints)
				{
					Color exist = colormap.getColor((float)pt.getX(), (float)pt.getY());
					PColor expcolor = PColor.create(COLOR_SPACE, exist.getColorComponents(new float[3]));
					
					double dist = ColorTools.distance(pcolor, expcolor, VIEW_ENV);
					if (dist < 6.0)
					{
						ok = false;
						break;
					}
				}
				
				if (ok)
				{
					jndPoints.add(new Point2D.Double(mx, my));
				}
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		int width = Math.min(getWidth(), getHeight());
		int height = width;

		drawColormap(g, width, height);

		int dx = 3;
		int dy = 2;
		g.setColor(Color.BLACK);
		for (Point2D pt : jndPoints)
		{
			int x = (int) (pt.getX() * width + 0.5);
			int y = (int) (pt.getY() * height + 0.5);
			
			g.drawLine(x - dx, y - dy, x + dx, y + dy);
			g.drawLine(x - dx, y + dy, x + dx, y - dy);
		}
	}

	private void drawColormap(Graphics2D g, int width, int height)
	{
		for (int y = 0; y < height; y++)
		{
			float my = y / (height - 1f);
			for (int x = 0; x < width; x++)
			{
				float mx = x / (width - 1f);
				g.setColor(colormap.getColor(mx, my));
				g.drawLine(x, y, x, y);
			}
		}
	}
}
