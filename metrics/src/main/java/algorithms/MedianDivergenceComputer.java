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
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import algorithms.sampling.SamplingStrategy;

import com.google.common.collect.Lists;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Computes the median divergence of a color map and related quantiles.
 * 
 * @author Simon Thum
 */
public final class MedianDivergenceComputer {
	
	private static final ViewingConditions comparisonVc = ViewingConditions.sRGB_typical_envirnonment;

	private Colormap colormap;
	
	private List<Point2D> points;

	private double[] ratios;
	
	private MedianDivergenceComputer(Colormap colormap, List<Point2D> points) {
		super();
		this.colormap = colormap;
		this.points = points;
	}

	public static MedianDivergenceComputer fromSamplingStrategy(Colormap colormap, SamplingStrategy strategy) {
		List<Point2D> points = Lists.newArrayList(strategy.getPoints());
		MedianDivergenceComputer that = new MedianDivergenceComputer(colormap, points);
		that.deriveMedianColormapToJNDRatio();
		return that;
	}
	
	public static MedianDivergenceComputer fromPoints(Colormap colormap, List<Point2D> points) {
		MedianDivergenceComputer that = new MedianDivergenceComputer(colormap, points);
		that.deriveMedianColormapToJNDRatio();
		return that;
	}
	
	public double getQuantile(double p) {
		return ratios[(int) ((ratios.length-1) * p)];
	}
	
	// TODO: move elsewhere
	public static PColor convert(Color color) {
		return PColor.create(CS_sRGB.instance, color.getColorComponents(new float[3]));
	}
	
	// TODO: move elsewhere
	public static double colorDiff(Color c1, Color c2) {
		return ColorTools.distance(convert(c1), convert(c2), comparisonVc);
	}
	
	private void deriveMedianColormapToJNDRatio() {
		int len = points.size()/2;
		ratios = new double[len];
		Iterator<Point2D> ptIt = points.iterator();
		
		int i = 0;
		while (i < len && ptIt.hasNext())
		{
			Point2D p1 = ptIt.next();
			Point2D p2 = ptIt.next();
	
			double dist = p1.distance(p2);
			
			Color colorA = colormap.getColor(p1.getX(), p1.getY());
			Color colorB = colormap.getColor(p2.getX(), p2.getY());
			
			// color distance
			double cdist = colorDiff(colorA, colorB);
			
			// filter zero divisions, as long as the value distance is small
			// DON'T protect colormaps that contain duplicate colors
			if (cdist == 0 && dist < 0.05)
				continue;
			double ratio = cdist / dist;
			ratios[i] = ratio;
			i++;
		}
		Arrays.sort(ratios);
	}
	
}
