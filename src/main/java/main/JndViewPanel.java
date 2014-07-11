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
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import colormaps.CachedColormap2D;
import colormaps.Colormap2D;

import com.google.common.collect.Lists;
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

	private CachedColormap2D colormap;
	private Colormap2D orgColormap;
	
	private Map<Point2D, PColor> jndPoints = Maps.newHashMap();
	private Map<Point2D, Shape> jndRegions = Maps.newHashMap();

	private double jndThreshold = 2.0;
	
	public JndViewPanel()
	{
		MyEventBus.getInstance().register(this);
	}
	
	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		colormap = new CachedColormap2D(event.getSelection(), 256, 256);

		probeCircular();

		repaint();
	}

	@Override
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);
		
		if (aFlag)
		{
			ColormapSelectionEvent event = MyEventBus.getLast(ColormapSelectionEvent.class);
			if (event.getSelection() != orgColormap)
			{
				onSelect(event);
				orgColormap = event.getSelection();
			}
		}
	}
	
	private void probeRectGrid()
	{
		jndPoints.clear();
		
		int sampleRate = 50;
		
		for (int y = 1; y < sampleRate-1; y++)
		{
			float my = y / (float)(sampleRate - 1);
			for (int x = 1; x < sampleRate-1; x++)
			{
				float mx = x / (float)(sampleRate - 1);
				
				Color color = colormap.getColor(mx, my);
				PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

				if (testColorDistance(pcolor))
				{
					jndPoints.put(new Point2D.Double(mx, my), pcolor);
				}
			}
		}
	}
	
	private void probeCircular()
	{
		jndPoints.clear();
		
		double cx = 0.5;
		double cy = 0.5;
		
		double sampleDist = 0.01;
		double dist = sampleDist;

		// add center point
		Color color = colormap.getColor((float)cx, (float)cy);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
		jndPoints.put(new Point2D.Double(cx, cy), pcolor);
		
		while (dist < 0.5 * Math.sqrt(2.0))
		{
			int sampleRate = (int) (2.0 * Math.PI * dist / sampleDist); 
			for (int i = 0; i < sampleRate; i++)
			{
				double dx = Math.cos(i * 2.0 * Math.PI / sampleRate + dist);
				double dy = Math.sin(i * 2.0 * Math.PI / sampleRate + dist);

				double px = cx + dx * dist; 
				double py = cy + dy * dist;
				
				if (px < 0 || px > 1 || py < 0 || py > 1)
					continue;

				color = colormap.getColor((float)px, (float)py);
				pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

				if (testColorDistance(pcolor))
				{
					jndPoints.put(new Point2D.Double(px, py), pcolor);
				}
			}
			
			dist += sampleDist;
		}
	}
	

	private boolean testColorDistance(PColor pcolor)
	{
		for (PColor expcolor : jndPoints.values())
		{
			double dist = ColorTools.distance(pcolor, expcolor, VIEW_ENV);
			if (dist < jndThreshold)
			{
				return false;
			}
		}
		
		return true;
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		drawColormap(g);
		drawJndPoints(g);
		
		computeJndRegions();
		drawJndRegions(g);
	}
	
	private void computeJndRegions()
	{
		jndRegions.clear();
		for (Point2D pt : jndPoints.keySet())
		{
			Polygon poly = computeJndRegion(pt.getX(), pt.getY());
			jndRegions.put(pt, poly);
		}
	}
	
	private Polygon computeJndRegion(double mx, double my)
	{
		Color color = colormap.getColor((float)mx, (float)my);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
		
		int angleSteps = 64;
		double sampleRateDist = 0.002;
		
		List<Point2D> pts = Lists.newArrayList();
		
		for (int i = 0; i < angleSteps; i++)
		{
			double dx = Math.cos(i * 2.0 * Math.PI / angleSteps);
			double dy = Math.sin(i * 2.0 * Math.PI / angleSteps);
			
			double jndDist = 0;
			double mapDist = 0;
			Point2D best = new Point2D.Double(mx, my);
			
			while (true)
			{
				mapDist += sampleRateDist;
				
				double px = mx + dx * mapDist; 
				double py = my + dy * mapDist;
				
				if (px < 0 || px > 1 || py < 0 || py > 1)
					break;
				
				Color tcolor = colormap.getColor((float)px, (float)py);
				PColor ptcolor = PColor.create(COLOR_SPACE, tcolor.getColorComponents(new float[3]));

				jndDist = ColorTools.distance(pcolor, ptcolor, VIEW_ENV);
				
				if (jndDist < jndThreshold * 0.5)
					best = new Point2D.Double(px, py); 
				else 
					break;
			}
			
			pts.add(best);
		}
		
		Polygon poly = createPolygon(pts);
		return poly;
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
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		g.drawImage(colormap.getImage(), 0, 0, getScreenWidth(), getScreenHeight(), null);
	}
	
	private void drawJndPoints(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		for (Point2D pt : jndPoints.keySet())
		{
			int r = 2;
			int wx = (int) mapXtoScreenX(pt.getX());
			int wy = (int) mapYtoScreenY(pt.getY());
			g.fillOval(wx - r, wy - r, 2 * r , 2 * r);
		}
	}
	
	private void drawJndRegions(Graphics2D g)
	{
		for (Shape shape : jndRegions.values())
		{
			g.setColor(Color.BLACK);
			g.draw(shape);
		}
	}

	private Polygon createPolygon(List<Point2D> pts) 
	{
        int[] x = new int[pts.size()];
        int[] y = new int[pts.size()];
        
        for (int i = 0; i < pts.size(); i++) 
        {
			int wx = (int) mapXtoScreenX(pts.get(i).getX());
			int wy = (int) mapYtoScreenY(pts.get(i).getY());
            x[i] = wx;
            y[i] = wy;
        }
        
        return new Polygon(x, y, pts.size());
    }


}

