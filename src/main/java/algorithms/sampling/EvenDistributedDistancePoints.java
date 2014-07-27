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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

/**
 * Randomly outputs points in [0..1, 0..1] whose sequential pairwise distance is evenly distributed
 * in the range given.
 * @author Simon Thum
 */
public class EvenDistributedDistancePoints implements SamplingStrategy
{

	private final List<Point2D> pts = Lists.newArrayList();
	
	private double lower = 0;
	private double upper = 1;
	
	public EvenDistributedDistancePoints(Random random, int number, double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
		populateList(random, number);
	}

	public EvenDistributedDistancePoints(Random random, int number)
	{
		populateList(random, number);
	}

	private void populateList(Random random, int number) {
		while (pts.size() < number)
		{
			double ax = random.nextDouble();
			double ay = random.nextDouble();

			double bx = 0;
			double by = 0;

			final double dist = lower + random.nextDouble() * (upper - lower);

			int tries = 20;
			while (tries >= 1)
			{
				double angle = random.nextDouble() * Math.PI * 2.0;

				bx = (ax + sin(angle) * dist);
				by = (ay + cos(angle) * dist);
				if (bx >= 0.0 && bx <= 1.0 && by >= 0.0 && by <= 1.0)
				{
					pts.add(new Point2D.Double(ax, ay));
					pts.add(new Point2D.Double(bx, by));
					break;
				}
				tries--;
			}
		}
	}

	@Override
	public Iterable<Point2D> getPoints()
	{
		return pts;
	}
}
