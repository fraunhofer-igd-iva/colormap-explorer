// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package de.fhg.igd.iva.colormaps;

import java.awt.Color;

/**
 * @author Martin Steiger
 *
 */
public interface Colormap
{
	/**
	 * @param x the x coordinate in the range [0..1]
	 * @param y the x coordinate in the range [0..1]
	 * @return the color value at [mx, my]
	 */
	Color getColor(double x, double y);
}
