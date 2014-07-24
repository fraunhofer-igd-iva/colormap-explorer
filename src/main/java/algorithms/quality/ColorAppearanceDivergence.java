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

import java.util.Random;

import algorithms.MedianDivergenceComputer;
import algorithms.sampling.EvenDistributedDistancePoints;
import colormaps.Colormap2D;

/**
 * Represents the color appearance to value divergence ratio as a quality measure.
 * 
 * @author Simon Thum
 */
public class ColorAppearanceDivergence implements ColormapQuality {
	
	private double _lower, _upper;
	
	/**
	 * @param lower lower quantile
	 * @param upper upper quantile
	 */
	public ColorAppearanceDivergence(double lower, double upper) {
		_lower = lower;
		_upper = upper;
	}

	@Override
	public double getQuality(Colormap2D colormap2d) {
		MedianDivergenceComputer comp = MedianDivergenceComputer.fromSamplingStrategy(colormap2d, new EvenDistributedDistancePoints(new Random(123), 10000));
		return comp.getQuantile(_upper) / comp.getQuantile(_lower);
	}
	
	@Override
	public boolean moreIsBetter()
	{
		return false;
	}


	@Override
	public String getName() {
		if (_lower == 0 && _upper == 1)
			return "Worst-case color appearance divergence";
		return "Color appearance divergence";
	}

	@Override
	public String getDescription() {
		return "The color appearance to value divergence between upper and lower bound";
	}

}
