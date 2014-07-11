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


package algorithms;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colormaps.Colormap2D;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class JndRegionComputer
{
	private static final Logger logger = LoggerFactory.getLogger(JndRegionComputer.class);

	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	
	private Map<Point2D, PColor> jndPoints;
	private Map<Point2D, List<Point2D>> jndRegions;

	private final double jndThreshold;
	private final Colormap2D colormap;

	/**
	 * @param colormap the colormap to use
	 * @param jndThreshold the jnd threshold for the set of distinguishable points
	 */
	public JndRegionComputer(Colormap2D colormap, double jndThreshold)
	{
		this.colormap = colormap;
		this.jndThreshold = jndThreshold;
	}

	private Map<Point2D, PColor> probeRectGrid()
	{
		Map<Point2D, PColor> result = Maps.newHashMap();
		
		int sampleRate = 50;
		
		for (int y = 0; y < sampleRate; y++)
		{
			float my = y / (float)(sampleRate - 1);
			for (int x = 0; x < sampleRate; x++)
			{
				float mx = x / (float)(sampleRate - 1);
				
				Color color = colormap.getColor(mx, my);
				PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

				if (testColorDistance(pcolor, result))
				{
					result.put(new Point2D.Double(mx, my), pcolor);
				}
			}
			
			logger.debug(String.format("Sampling: %3.0f%%", 100d * y / sampleRate));
		}
		
		logger.debug("Sampling: 100%");
		
		return result;
	}
	
	private Map<Point2D, PColor> probeCircular()
	{
		Map<Point2D, PColor> result = Maps.newHashMap();

		int idx = 0;
		
		double cx = 0.5;
		double cy = 0.5;
		
		double maxDist = 0.5 * Math.sqrt(2.0);
		double sampleDist = 0.01;
		double dist = sampleDist;
		
		int size = (int) (maxDist / dist);

		// add center point
		Color color = colormap.getColor((float)cx, (float)cy);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
		result.put(new Point2D.Double(cx, cy), pcolor);
		
		while (dist < maxDist)
		{
			if (idx % (size / 12) == 0)
			{
				logger.debug(String.format("Sampling: %3.0f%%", 100d * dist / maxDist));
			}
			
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

				if (testColorDistance(pcolor, result))
				{
					result.put(new Point2D.Double(px, py), pcolor);
				}
			}
			
			dist += sampleDist;
			idx++;
		}
		
		logger.debug("Sampling: 100%");
		
		return result;
	}

	private boolean testColorDistance(PColor pcolor, Map<Point2D, PColor> result)
	{
		for (PColor expcolor : result.values())
		{
			double dist = ColorTools.distance(pcolor, expcolor, VIEW_ENV);
			if (dist < jndThreshold)
			{
				return false;
			}
		}
		
		return true;
	}

	private Map<Point2D, List<Point2D>> computeJndRegions()
	{
		Map<Point2D, List<Point2D>> regions = Maps.newHashMap();
		
		int idx = 0;
		int size = jndPoints.keySet().size();
		
		for (Point2D pt : jndPoints.keySet())
		{
			if (idx % (size / 12) == 0)
			{
				logger.debug(String.format("Computing jnd regions: %3.0f%%", 100d * idx / size));
			}
			
			List<Point2D> pts = computeJndRegion(pt.getX(), pt.getY());
			regions.put(pt, pts);
			idx++;
		}
		
		logger.debug("Computing jnd regions: 100%");
		
		return regions;
	}
	
	private List<Point2D> computeJndRegion(double mx, double my)
	{
		Color color = colormap.getColor((float)mx, (float)my);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
		
		int angleSteps = 96;
		double sampleRateDist = 0.0005;
		
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
		
		return pts;
	}

	/**
	 * @return an unmodifiable set of points with a pairwise distance of at least jndThreshold
	 */
	public Set<Point2D> getPoints()
	{
		if (jndPoints == null)
		{
			jndPoints = probeCircular();
		}
		
		return Collections.unmodifiableSet(jndPoints.keySet());
	}

	/**
	 * @param jndPt the point of interest
	 * @return a list of polygon corner points of the region or <code>null</code>
	 */
	public List<Point2D> getRegion(Point2D jndPt)
	{
		if (jndRegions == null)
		{
			jndRegions = computeJndRegions();
		}

		return jndRegions.get(jndPt);
	}

	/**
	 * @param jndPt the point of interest
	 * @return the pcolor value of the point in the colormap
	 */
	public PColor getPColor(Point2D jndPt)
	{
		return jndPoints.get(jndPt);
	}	
}
