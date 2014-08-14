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

import java.awt.Color;
import java.awt.geom.Point2D;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.sampling.SamplingStrategy;
import de.fhg.igd.iva.colormaps.Colormap2D;
import de.fhg.igd.iva.colorspaces.CIELABLch;

/**
 * Computes the variance in attention steering
 * @author Martin Steiger
 */
public class AttentionQuality implements ColormapQuality
{
	private final SamplingStrategy sampling;

	/**
	 * @param sampling the sampling to use
	 */
	public AttentionQuality(SamplingStrategy sampling)
	{
		this.sampling = sampling;
	}
	
	@Override
	public double getQuality(Colormap2D colormap)
	{
		// max L + max c (which is the same as a or b)
		double normFac = Math.sqrt(100*100 + 150*150);

		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		for (Point2D pt : sampling.getPoints())
		{
			Color color = colormap.getColor(pt.getX(), pt.getY());
			double[] lch = new CIELABLch().fromColor(color);
			double attention = Math.sqrt(lch[0]*lch[0]+lch[1]*lch[1]) / normFac;
			
			stats.addValue(attention);
		}
		
		return stats.getStandardDeviation() * 100d;
	}
	
	@Override
	public boolean moreIsBetter()
	{
		return false;
	}

	@Override
	public String getName()
	{
		return "Attention Steering";
	}

	@Override
	public String getDescription()
	{
		return "The standard deviation of the attention steering (Lightness and Chroma) variable";
	}
}
