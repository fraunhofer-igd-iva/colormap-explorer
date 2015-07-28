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

import com.google.common.base.Preconditions;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.AbstractKnownColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

/**
 * A Colormap with nine anchor colors (3x3 grid) and linear interpolation in
 * between (RGB). The colors are assigned at run-time.
 *
 */
class NineCornersAnchorColorMapParameterizable extends AbstractColormap {

	private final Color[][] anchor;

	public NineCornersAnchorColorMapParameterizable(Color[][] anchor) {
	    Preconditions.checkArgument(anchor.length == 3);
	    Preconditions.checkArgument(anchor[0].length == 3);
	    Preconditions.checkArgument(anchor[1].length == 3);
	    Preconditions.checkArgument(anchor[2].length == 3);
	    this.anchor = anchor;
	}

	/**
	 * Create from four corners plus center.
	 * @param upperLeft
	 * @param upperRight
	 * @param lowerLeft
	 * @param lowerRight
	 * @param center
	 */
	public NineCornersAnchorColorMapParameterizable(Color upperLeft, Color upperRight, Color lowerLeft, Color lowerRight, Color center) {

		if (lowerLeft == null || lowerRight == null || upperLeft == null || upperRight == null || center == null)
			throw new IllegalArgumentException("one of the color objects was null!");

		anchor = new Color[3][3];
		this.anchor[0][0] = upperLeft;
		this.anchor[0][2] = upperRight;
		this.anchor[2][0] = lowerLeft;
		this.anchor[2][2] = lowerRight;
		this.anchor[1][1] = center;
		this.anchor[1][0] = interpolate(upperLeft, upperRight, lowerLeft, lowerRight, 0, 0.5); // center top
		this.anchor[1][2] = interpolate(upperLeft, upperRight, lowerLeft, lowerRight, 1, 0.5); // center bottom
		this.anchor[0][1] = interpolate(upperLeft, upperRight, lowerLeft, lowerRight, 0.5, 0); // center left
		this.anchor[2][1] = interpolate(upperLeft, upperRight, lowerLeft, lowerRight, 0.5, 1); // center right
	}

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		int ix = (x < 0.5) ? 0 : 1;
		int iy = (y < 0.5) ? 0 : 1;

		double ax = (x < 0.5) ? x * 2 : (x - 0.5) * 2;
		double ay = (y < 0.5) ? y * 2 : (y - 0.5) * 2;

		Color tl = anchor[iy][ix];
        Color tr = anchor[iy][ix + 1];
        Color bl = anchor[iy + 1][ix];
        Color br = anchor[iy + 1][ix + 1];

        return interpolate(tl, tr, bl, br, ax, ay);
	}
}
