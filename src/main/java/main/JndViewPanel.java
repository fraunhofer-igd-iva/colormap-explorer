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
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.terasology.math.delaunay.Voronoi;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2d;
import org.terasology.math.geom.Vector2d;

import colormaps.CachedColormap2D;

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
	
	private Map<Point2D, PColor> jndPoints = Maps.newHashMap();

	private double jndThreshold = 5.0;
	
	private Voronoi delaunay;
	
	private Point curPos;

	public JndViewPanel()
	{
		// get last selection event and trigger it manually to be up to date
		ColormapSelectionEvent selEvent = MyEventBus.getLast(ColormapSelectionEvent.class);
		if (selEvent != null)
		{
			onSelect(selEvent);
		}
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				curPos = null;
			}
		});
		
		addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				curPos = new Point(e.getX(), e.getY());
				JndViewPanel.this.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				curPos = new Point(e.getX(), e.getY());
				JndViewPanel.this.repaint();
			}
		});

		MyEventBus.getInstance().register(this);
	}
	
	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		colormap = new CachedColormap2D(event.getSelection(), 512, 512);

		updateRegions();
//		updateDelaunay();

		repaint();
	}

	@Override
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);
		
		if (aFlag)
		{
			onSelect(MyEventBus.getLast(ColormapSelectionEvent.class));
		}
	}
	
	private void updateRegions()
	{
		jndPoints.clear();
		
		int sampleRate = 20;
		
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
		drawVoronoiOutline(g);
		drawJndRegion(g);
	}

	private void drawJndRegion(Graphics2D g)
	{
		if (curPos == null)
			return;
		
		double mx = screenXtoMapX(curPos.getX());
		double my = screenYtoMapY(curPos.getY());
		
		Color color = colormap.getColor((float)mx, (float)my);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
		
		int angleSteps = 64;
		double sampleRateDist = 0.005;
		
		List<Vector2d> pts = Lists.newArrayList();
		
		for (int i = 0; i < angleSteps; i++)
		{
			double dx = Math.cos(i * 2.0 * Math.PI / angleSteps);
			double dy = Math.sin(i * 2.0 * Math.PI / angleSteps);
			
			double jndDist = 0;
			double mapDist = 0;
			Vector2d best = new Vector2d(mx, my);
			
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
				
				if (jndDist < 2.0)
					best = new Vector2d(px, py); 
				else 
					break;
			}
			
			pts.add(best);
		}
		
		Polygon poly = createPolygon(pts);
		g.setColor(Color.BLACK);
		g.draw(poly);
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
	
	private void drawVoronoiOutline(Graphics2D g)
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
	}
	
	/**
	 * Draws Voronoi diagram based on current triangulation
	 * A Voronoi diagram can be created from a Delaunay triangulation by
	 * connecting the circumcenters of neighboring triangles
	 */
	private void drawVoronoiRegions(Graphics2D g)
	{
		for (final Vector2d center : delaunay.siteCoords())
		{
			List<Vector2d> corners = Lists.newArrayList(delaunay.region(center));
			
            Collections.sort(corners, new Comparator<Vector2d>() {

                @Override
                public int compare(Vector2d o0, Vector2d o1) {
                	Vector2d a = new Vector2d(o0).sub(center).normalize();
                    Vector2d b = new Vector2d(o1).sub(center).normalize();

                    if (a.y() > 0) { //a between 0 and 180
                        if (b.y() < 0) //b between 180 and 360
                            return -1;
                        return a.x() < b.x() ? 1 : -1;
                    } else { // a between 180 and 360
                        if (b.y() > 0) //b between 0 and 180
                            return 1;
                        return a.x() > b.x() ? 1 : -1;
                    }
                }
            });
            
            Color color = colormap.getColor((float)center.getX(), (float)center.getY());
            g.setColor(color);
            
            Polygon poly = createPolygon(corners);
            g.fill(poly);
        }

		g.setColor(Color.WHITE);
		for (Vector2d pt : delaunay.siteCoords())
		{
			int r = 1;
			int wx = (int) mapXtoScreenX(pt.getX());
			int wy = (int) mapYtoScreenY(pt.getY());
			g.fillOval(wx - r, wy - r, 2 * r , 2 * r);
		}
	}

	 private Polygon createPolygon(List<Vector2d> pts) 
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

