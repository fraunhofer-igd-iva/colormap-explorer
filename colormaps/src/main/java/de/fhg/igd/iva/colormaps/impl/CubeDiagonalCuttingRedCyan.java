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
import java.util.Arrays;
import java.util.List;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

public class CubeDiagonalCuttingRedCyan extends AbstractColormap {

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		return new Color((float)(1 - y), (float)x, (float)y);
	}

	@Override
	public String getName() {
		return "Cube Diagonal Cut B-C-Y-R";
	}

	@Override
	public String getDescription() {
		return "RGB cube diagonal cuting with the anchors Yellow, Red, Blue and Cyan. X-axis: Green, y-axis: Blue vs. Red";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}
	
	@Override
	public List<String> getReferences() {
		return Arrays.asList("himberg2000som", "himberg2004insights", "Bremm2011", "andrienko2012visual", "bernard2012bcl");
	}
}
