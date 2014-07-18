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

import java.awt.geom.Point2D;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Defines a sampling strategy for a square [0..1] x [0..1] based on concentric circles
 * @author Martin Steiger
 */
public class CircularSampling implements SamplingStrategy
{
	private int resolution;

	public CircularSampling(int resolution)
	{
		this.resolution = resolution;
	}
	
	/**
	 * @return a order-preserving iterable for the sampling points
	 */
	@Override
	public Iterable<Point2D> getPoints()
	{
		// TODO: convert into AbstractIterator and compute next point on-the-fly
		
		List<Point2D> pts = Lists.newArrayList();
		
		double cx = 0.5;
		double cy = 0.5;
		
		double maxDist = 0.5 * Math.sqrt(2.0);
		double sampleDist = 1.0 / resolution;
		double dist = sampleDist;
		
		// add center point
		pts.add(new Point2D.Double(cx, cy));
		
		while (dist < maxDist)
		{
			int sampleRate = (int) (2.0 * Math.PI * dist / sampleDist); 
			for (int i = 0; i < sampleRate; i++)
			{
				double dx = Math.cos(i * 2.0 * Math.PI / sampleRate + dist);
				double dy = Math.sin(i * 2.0 * Math.PI / sampleRate + dist);

				double px = cx + dx * dist; 
				double py = cy + dy * dist;
				
				if (px < 0 || px > 1 || py < 0 || py > 1)
					continue;

				pts.add(new Point2D.Double(px, py));
			}
			
			dist += sampleDist;
		}
		
		return pts;
	}

}

