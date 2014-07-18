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


package algorithms.sampling;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

import de.fhg.igd.pcolor.PColor;

/**
 * Defines a sampling strategy for a square [0..1] x [0..1]
 * @author Martin Steiger
 */
public class GridSampling implements SamplingStrategy
{
	private int sampleRate;

	public GridSampling(int resolution)
	{
		this.sampleRate = resolution;
	}
	
	/**
	 * @return a order-preserving iterable for the sampling points
	 */
	@Override
	public Iterable<Point2D> getPoints()
	{
		// TODO: convert into AbstractIterator and compute next point on-the-fly
		
		List<Point2D> pts = Lists.newArrayList();
		
		for (int y = 0; y < sampleRate; y++)
		{
			float my = y / (float)(sampleRate - 1);
			for (int x = 0; x < sampleRate; x++)
			{
				float mx = x / (float)(sampleRate - 1);
				pts.add(new Point2D.Double(mx, my));
			}
		}

		return pts;
	}

}
