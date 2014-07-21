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


package algorithms.quality;

import java.awt.geom.Point2D;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.JndRegionComputer;
import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;

/**
 * Computes the variance in jnd region size
 * @author Martin Steiger
 */
public class JndRegionSize implements ColormapQuality
{
	private final SamplingStrategy sampling;

	/**
	 * @param sampling the sampling to use
	 */
	public JndRegionSize(SamplingStrategy sampling)
	{
		this.sampling = sampling;
	}
	
	@Override
	public double getQuality(Colormap2D colormap)
	{
		JndRegionComputer computer = new JndRegionComputer(colormap, sampling, 3.0);

		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (Point2D center : computer.getPoints())
		{
			List<Point2D> poly = computer.getRegion(center);
			double area = computeArea(poly, center);
	        
	        stats.addValue(area);
		}
		
		// TODO: find a better scaling factor
		return stats.getVariance() * 10000000.d;
	}

	private double computeArea(List<Point2D> poly, Point2D center)
	{
		double sum = 0;
		for (int i = 0; i < poly.size(); i++)
		{
		    Point2D p0 = poly.get(i);
		    Point2D p1 = poly.get((i + 1) % poly.size());
		    
			sum = sum + p0.getX() * p1.getY() - p0.getY() * p1.getX();
		}

		double area = Math.abs(sum / 2);
		return area;
	}

	@Override
	public String getName()
	{
		return "Region Size";
	}

	@Override
	public String getDescription()
	{
		return "Compute the jnd regions values based on a sampling strategy and returns the variance of the jnd region size across the colormap";
	}
}
