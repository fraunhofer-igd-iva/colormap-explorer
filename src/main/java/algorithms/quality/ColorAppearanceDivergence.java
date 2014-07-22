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

import static java.lang.Math.log;

import java.util.Random;

import algorithms.MedianDivergenceComputer;
import algorithms.sampling.EvenDistributedDistancePoints;
import colormaps.Colormap2D;

/**
 * Represents the color appearance to value divergence ratio as a quality measure.
 * 
 * Note: Without caching, as is now, this is pretty expensive.
 * 
 * @author Simon Thum
 */
public class ColorAppearanceDivergence implements ColormapQuality {
	
	public ColorAppearanceDivergence() {
		super();
	}

	@Override
	public double getQuality(Colormap2D colormap2d) {
		MedianDivergenceComputer comp = MedianDivergenceComputer.fromSamplingStrategy(colormap2d, new EvenDistributedDistancePoints(new Random(123), 10000));
		double d = (log(comp.getQuantile(0.9))-log(comp.getQuantile(0.1))) / log(2);
		return 1.0 / d;		// "good" should be > "bad"
	}

	@Override
	public String getName() {
		return "ColorAppearanceDivergence";
	}

	@Override
	public String getDescription() {
		return "The color appearance to value divergence in powers of"
				+ " two between upper and lower bound; higher values are worse.";
	}

}
