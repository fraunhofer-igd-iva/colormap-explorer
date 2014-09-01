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

package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.AbstractKnownColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

/**
 * Colormap with four anchor colors in the edges and linear interpolation in
 * between (RGB). The four colors are assigned at run-time.
 *
 */
class FourCornersAnchorColorMapParameterizable extends AbstractColormap {

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
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		double r = interpolate(upperLeft.getRed(), upperRight.getRed(),
				lowerLeft.getRed(), lowerRight.getRed(), x, y);
		double g = interpolate(upperLeft.getGreen(), upperRight.getGreen(),
				lowerLeft.getGreen(), lowerRight.getGreen(), x, y);
		double b = interpolate(upperLeft.getBlue(), upperRight.getBlue(),
				lowerLeft.getBlue(), lowerRight.getBlue(), x, y);

		return new Color((float)r / 255f, (float)g / 255f, (float)b / 255f);
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
