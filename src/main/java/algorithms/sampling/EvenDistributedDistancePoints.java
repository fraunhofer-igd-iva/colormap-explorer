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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Randomly outputs points in [0..1, 0..1] whose sequential pairwise distance is evenly distributed.
 * @author Simon Thum
 */
public class EvenDistributedDistancePoints implements SamplingStrategy {

	private final Random random;
	
	private int countdown;
	
	public EvenDistributedDistancePoints(Random random, int number) {
		this.random = random;
		this.countdown = number;
	}

	@Override
	public Iterable<Point2D> getPoints() {
		return new Iterable<Point2D>() {
			@Override
			public Iterator<Point2D> iterator() {
				return new Iterator<Point2D>() {
					
					private List<Float> buffer;
					private boolean partial;

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
					
					
					@Override
					public Point2D next() {
						if (!hasNext())
							throw new IllegalStateException("iterator exhausted");

						countdown--;
						
						if (buffer == null) {
							partial = false;
							buffer = makePair();
						}
						
						if (!partial) {
							partial = true;
							return new Point2D.Float(buffer.get(0), buffer.get(1));
						} else {
							Point2D.Float p = new Point2D.Float(buffer.get(2), buffer.get(3));
							buffer = null;
							return p;
						}
					}
					
					@Override
					public boolean hasNext() {
						return countdown > 0;
					}
				};
			}
		};
	}
	
	// list of lines across which to compare as pairs of pairs of floats
	private List<Float> makePair() {
		List<Float> list = new ArrayList<>(4);
		
		float ax = random.nextFloat();
		float ay = random.nextFloat();
		
		float bx = 0;
		float by = 0;
		
		final float dist = random.nextFloat();
		
		int tries = 20;
		while (tries >= 1) {
			float angle = (float) (random.nextFloat() * Math.PI * 2.0);
			
			bx = (float) (ax + sin(angle) * dist); 
			by = (float) (ay + cos(angle) * dist);
			if (bx >= 0.0 && bx <= 1.0 && by >= 0.0 && by <= 1.0)
				break;
			tries --;
		}
		if (tries == 0)
			return makePair();  // exhausted, should not happen often, distorts scatterplot
		list.add(ax);
		list.add(ay);
		list.add(bx);
		list.add(by);
		return list;
	}

}
