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

package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

/**
 * Colormap with four anchor colors in the edges and linear interpolation in
 * between (RGB). The four colors are assigned at run-time.
 * 
 */
class FourCornersAnchorColorMapParameterizable extends AbstractColormap2D {

	private Color lowerLeft;
	private Color lowerRight;
	private Color upperRight;
	private Color upperLeft;

	public FourCornersAnchorColorMapParameterizable(Color upperLeft, Color upperRight,
			Color lowerLeft, Color lowerRight) {

		if (lowerLeft == null || lowerRight == null || upperLeft == null
				|| upperRight == null)
			throw new IllegalArgumentException(
					"one of the color objects was null!");

		this.lowerLeft = lowerLeft;
		this.lowerRight = lowerRight;
		this.upperRight = upperRight;
		this.upperLeft = upperLeft;
	}

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		double r = interpolate(upperLeft.getRed(), upperRight.getRed(),
				lowerLeft.getRed(), lowerRight.getRed(), x, y);
		double g = interpolate(upperLeft.getGreen(), upperRight.getGreen(),
				lowerLeft.getGreen(), lowerRight.getGreen(), x, y);
		double b = interpolate(upperLeft.getBlue(), upperRight.getBlue(),
				lowerLeft.getBlue(), lowerRight.getBlue(), x, y);

		return new Color((int) r, (int) g, (int) b);
	}

	@Override
	public String getName() {
		return "RGBFourAnchorColorMapDynamic";
	}

	@Override
	public String getDescription() {
		return "Colormap with four anchor colors in the edges and linear interpolation in between (RGB). The four colors are assigned at run-time.";
	}
	
	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.RGB;
	}

	private double interpolate(double start, double end, double position) {
		return start + (end - start) * position;
	}

	private double interpolate(double lo, double ro, double lu, double ru,
			double positionX, double positionY) {
		// TODO: needs testing!
		double o = interpolate(lo, ro, positionX);
		double u = interpolate(lu, ru, positionX);
		return interpolate(o, u, positionY);
	}

}
