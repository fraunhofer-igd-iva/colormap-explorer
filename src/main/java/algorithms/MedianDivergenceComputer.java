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

import main.MismatchScatterplotPanel;
import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;

import com.google.common.collect.Lists;

/**
 * Computes the median divergence of a color map and related quantiles.
 * 
 * @author Simon Thum
 */
public final class MedianDivergenceComputer {
	
	private Colormap2D colormap;
	
	private List<Point2D> points;

	private double[] ratios;
	
	private MedianDivergenceComputer(Colormap2D colormap, List<Point2D> points) {
		super();
		this.colormap = colormap;
		this.points = points;
	}

	public static MedianDivergenceComputer fromSamplingStrategy(Colormap2D colormap, SamplingStrategy strategy) {
		List<Point2D> points = Lists.newArrayList(strategy.getPoints());
		MedianDivergenceComputer that = new MedianDivergenceComputer(colormap, points);
		that.deriveMedianColormapToJNDRatio();
		return that;
	}
	
	public static MedianDivergenceComputer fromPoints(Colormap2D colormap, List<Point2D> points) {
		MedianDivergenceComputer that = new MedianDivergenceComputer(colormap, points);
		that.deriveMedianColormapToJNDRatio();
		return that;
	}
	
	public double getQuantile(double p) {
		return ratios[(int) ((ratios.length-1) * p)];
	}
	
	private void deriveMedianColormapToJNDRatio() {
		int len = points.size()/2;
		ratios = new double[len];
		Iterator<Point2D> ptIt = points.iterator();
		
		for (int i = 0; i < len; i++)
		{
			Point2D p1 = ptIt.next();
			Point2D p2 = ptIt.next();
	
			float dist = (float) p1.distance(p2);
			
			Color colorA = colormap.getColor(p1.getX(), p1.getY());
			Color colorB = colormap.getColor(p2.getX(), p2.getY());
			
			// roughly 0-100
			double cdist = MismatchScatterplotPanel.colorDiff(colorA, colorB);
			
			double ratio = cdist / dist;
			ratios[i] = ratio;
		}
		Arrays.sort(ratios);
	}
	
}
