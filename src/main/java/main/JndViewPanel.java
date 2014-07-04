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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.terasology.math.delaunay.Voronoi;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2d;
import org.terasology.math.geom.Vector2d;

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
	
	private Voronoi delaunay;
	
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
		updateDelaunay();
		repaint();
	}

	private void updateRegions()
	{
		jndPoints.clear();
		
		int sampleRate = 100;
		
		for (int y = 1; y < sampleRate-1; y++)
		{
			float my = y / (float)(sampleRate - 1);
			for (int x = 1; x < sampleRate-1; x++)
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
	
	private void updateDelaunay()
	{
		List<Vector2d> pts = new ArrayList<>(jndPoints.size());
		for (Point2D pt : jndPoints.keySet())
		{
			pts.add(new Vector2d(pt.getX(), pt.getY()));
		}
		delaunay = new Voronoi(pts, Rect2d.createFromMinAndMax(0, 0, 1, 1));
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		drawColormap(g);
		drawVoronoi(g);
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
	
	/**
	 * Draws Voronoi diagram based on current triangulation
	 * A Voronoi diagram can be created from a Delaunay triangulation by
	 * connecting the circumcenters of neighboring triangles
	 */
	private void drawVoronoi(Graphics g)
	{
		List<LineSegment> segs = delaunay.voronoiDiagram();

		g.setColor(Color.BLACK);
		for (LineSegment seg : segs)
		{
			double x1 = mapXtoScreenX(seg.getP0().getX());
			double y1 = mapYtoScreenY(seg.getP0().getY());
			double x2 = mapXtoScreenX(seg.getP1().getX());
			double y2 = mapYtoScreenY(seg.getP1().getY());
			
			g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		}

		g.setColor(Color.WHITE);
		for (Vector2d pt : delaunay.siteCoords())
		{
			int r = 2;
			int wx = (int) mapXtoScreenX(pt.getX());
			int wy = (int) mapXtoScreenX(pt.getY());
			g.fillOval(wx - r, wy - r, 2 * r , 2 * r);
		}
	}

}
