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
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import progress.ProgressListener;
import algorithms.sampling.SamplingStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Finds a set of points with a pair-wise perceptual distance of at least jndTheshold
 * and their corresponding regions
 * @author Martin Steiger
 */
public class JndRegionComputer
{
	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);

	private Map<Point2D, PColor> jndPoints = new MapMaker().concurrencyLevel(1).makeMap();
	private Map<Point2D, List<Point2D>> jndRegions = new MapMaker().concurrencyLevel(1).makeMap();

	private final SamplingStrategy sampling;
	private final Colormap colormap;

	private double jndThreshold = 3.0;
	private int angleSteps = 128;
	private double stepSize = 0.0005;

	/**
	 * @param colormap the colormap to use
	 * @param sampling the sampling strategy for finding the jnd points
	 * @param jndThreshold the jnd threshold for the set of distinguishable points
	 */
	public JndRegionComputer(Colormap colormap, SamplingStrategy sampling)
	{
		this.colormap = colormap;
		this.sampling = sampling;
	}

	public void setJndThreshold(double jndThreshold)
	{
		if (jndThreshold == this.jndThreshold)
			return;

		jndPoints.clear();
		jndRegions.clear();

		this.jndThreshold = jndThreshold;
	}

	public void computePoints(final ProgressListener progress)
	{
		if (!jndPoints.isEmpty())
			return;

		Deque<PColor> list = new LinkedList<PColor>();

		final Collection<Point2D> samples = sampling.getPoints();

		progress.start(samples.size());
		for (Point2D pt : samples)
		{
			Color color = colormap.getColor(pt.getX(), pt.getY());
			PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

			if (testColorDistance(pcolor, list))
			{
				jndPoints.put(pt, pcolor);
				list.addFirst(pcolor);
			}

			progress.step();

			if (progress.isCancelled())
				break;
		}
		progress.finish();
	}

	private boolean testColorDistance(PColor pcolor, Collection<? extends PColor> others)
	{
		for (PColor expcolor : others)
		{
			double dist = ColorTools.distance(pcolor, expcolor, VIEW_ENV);
			if (dist < jndThreshold)
			{
				return false;
			}
		}

		return true;
	}

	public void computeJndRegions(ProgressListener listener)
	{
		if (!jndRegions.isEmpty())
			return;

		int size = jndPoints.keySet().size();

		listener.start(size);
		for (Point2D pt : jndPoints.keySet())
		{
			List<Point2D> pts = computeJndRegion(pt.getX(), pt.getY());
			jndRegions.put(pt, pts);
			listener.step();

			if (listener.isCancelled())
				break;
		}
		listener.finish();
	}

	private List<Point2D> computeJndRegion(double mx, double my)
	{
		Color color = colormap.getColor(mx, my);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

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
				mapDist += stepSize;

				double px = mx + dx * mapDist;
				double py = my + dy * mapDist;

				if (px < 0 || px > 1 || py < 0 || py > 1)
					break;

				Color tcolor = colormap.getColor(px, py);
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
		return Collections.unmodifiableSet(jndPoints.keySet());
	}

	/**
	 * @param jndPt the point of interest
	 * @return a list of polygon corner points of the region or <code>null</code>
	 */
	public List<Point2D> getRegion(Point2D jndPt)
	{
		return jndRegions.get(jndPt);
	}

	/**
	 * @return the number of computed regions
	 */
	public int getRegionCount()
	{
		return jndRegions.size();
	}

	/**
	 * @param jndPt the point of interest
	 * @return the pcolor value of the point in the colormap
	 */
	public PColor getPColor(Point2D jndPt)
	{
		return jndPoints.get(jndPt);
	}

	/**
	 * @param angles the number of angles to test
	 * @param stepSize the step size to find the threshold
	 */
	public void setRegionSampling(int angles, double stepSize)
	{
		if (angles == angleSteps && stepSize == this.stepSize)
			return;

		jndRegions.clear();

		this.angleSteps = angles;
		this.stepSize = stepSize;
	}
}
