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
import java.util.Iterator;

import main.MismatchScatterplotPanel;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;

/**
 * Computes the variance of the color distance/map distance ratio.
 * @author Martin Steiger
 */
public final class ColorDivergenceVariance implements ColormapQuality
{
	private final SamplingStrategy strategy;

	public ColorDivergenceVariance(SamplingStrategy strategy)
	{
		this.strategy = strategy;
	}

	@Override
	public double getQuality(Colormap2D colormap)
	{
		DescriptiveStatistics stats = new DescriptiveStatistics();
		Iterator<Point2D> ptIt = strategy.getPoints().iterator();

		while (ptIt.hasNext())
		{
			Point2D p1 = ptIt.next();

			if (!ptIt.hasNext())
				break;

			Point2D p2 = ptIt.next();

			float dist = (float) p1.distance(p2);

			Color colorA = colormap.getColor(p1.getX(), p1.getY());
			Color colorB = colormap.getColor(p2.getX(), p2.getY());

			// roughly 0-100
			double cdist = MismatchScatterplotPanel.colorDiff(colorA, colorB);

			double ratio = cdist / dist;

			stats.addValue(ratio);
		}

		return 1.0 / stats.getVariance();	// "good" should be > "bad"
	}

	@Override
	public String getName()
	{
		return "ColorDistance Variance";
	}

	@Override
	public String getDescription()
	{
		return "Computes the variance of the color distance/map distance ratio";
	}
}
