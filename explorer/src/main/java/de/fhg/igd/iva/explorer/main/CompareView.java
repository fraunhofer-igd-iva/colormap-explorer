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


package de.fhg.igd.iva.explorer.main;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

import algorithms.quality.AttentionQuality;
import algorithms.quality.ColorAppearanceDivergence;
import algorithms.quality.ColorDivergenceVariance;
import algorithms.quality.ColorDynamicDistBlack;
import algorithms.quality.ColorDynamicDistWhite;
import algorithms.quality.ColorExploitation;
import algorithms.quality.ColormapQuality;
import algorithms.sampling.CircularSampling;
import algorithms.sampling.EvenDistributedDistancePoints;
import algorithms.sampling.GridSampling;
import algorithms.sampling.SamplingStrategy;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.KnownColormap;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CompareView extends JPanel
{
	private static final long serialVersionUID = -2382155905934204715L;

	public CompareView(List<KnownColormap> colorMaps)
	{
		setLayout(new GridLayout(1, 0));

		List<ColormapQuality> metrics = getMetrics();

		Function<ColormapQuality, Range<Double>> ranges = computesRanges(colorMaps, metrics);

		add(new CompareViewPanel(colorMaps, metrics, ranges));
		add(new CompareViewPanel(colorMaps, metrics, ranges));
		add(new CompareViewPanel(colorMaps, metrics, ranges));
	}

	private List<ColormapQuality> getMetrics()
	{
        SamplingStrategy circSampling = new CircularSampling(30);
        SamplingStrategy rectSampling = new GridSampling(50);	// 50x50 = 2500 sample points
		EvenDistributedDistancePoints overallDistSampling = new EvenDistributedDistancePoints(new Random(12345), 2000);
//		EvenDistributedDistancePoints smallDistSampling = new EvenDistributedDistancePoints(new Random(12345), 2000, 0, 0.1);
//		EvenDistributedDistancePoints midDistSampling = new EvenDistributedDistancePoints(new Random(12345), 2000, 0.1, 0.3);
//		EvenDistributedDistancePoints largeDistSampling = new EvenDistributedDistancePoints(new Random(12345), 2000, 0.3, 1);

		// TODO: iterate over all available quality measures
		List<ColormapQuality> measures = Lists.newArrayList();

        measures.add(new ColorExploitation(circSampling, 3.0));
//        measures.add(new JndRegionSize(circSampling));
        measures.add(new AttentionQuality(rectSampling));
//        measures.add(new ColorDynamicBrightest(rectSampling));
//		measures.add(new ColorDynamicDarkest(rectSampling));
		measures.add(new ColorDynamicDistBlack(rectSampling));
		measures.add(new ColorDynamicDistWhite(rectSampling));
//		measures.add(new ColorDynamicWhiteContrast(rectSampling));
//		measures.add(new ColorDivergenceVariance(smallDistSampling));
//		measures.add(new ColorDivergenceVariance(midDistSampling));
//		measures.add(new ColorDivergenceVariance(largeDistSampling));
		measures.add(new ColorDivergenceVariance(overallDistSampling));
//		measures.add(new ColorAppearanceDivergence(0.05, 0.95, smallDistSampling));
//		measures.add(new ColorAppearanceDivergence(0.05, 0.95, midDistSampling));
//		measures.add(new ColorAppearanceDivergence(0.05, 0.95, largeDistSampling));
		measures.add(new ColorAppearanceDivergence(0.05, 0.95, overallDistSampling));

		//        measures.add(new ColorDivergenceQuantile(0.5));
//        measures.add(new ColorDivergenceQuantile(0.1));
//        measures.add(new ColorDivergenceQuantile(0.9));

		return measures;
	}

	private Function<ColormapQuality, Range<Double>> computesRanges(Collection<? extends Colormap> colorMaps, Collection<ColormapQuality> metrics)
	{
		Map<ColormapQuality, Range<Double>> result = Maps.newHashMap();

		for (ColormapQuality metric : metrics)
		{
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;

			for (Colormap cm : colorMaps)
			{
				double quality = metric.getQuality(cm);
				if (!Double.isInfinite(quality) && !Double.isNaN(quality))
				{
			    	if (quality > max)
			    		max = quality;

			    	if (quality < min)
			    		min = quality;
				}
			}

			result.put(metric, Range.closed(min, max));
		}

		return Functions.forMap(result);
	}


}
