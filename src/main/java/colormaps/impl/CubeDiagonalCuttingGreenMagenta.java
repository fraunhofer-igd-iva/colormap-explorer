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
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class CubeDiagonalCuttingGreenMagenta extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		return new Color(x, (1 - y), y);
	}

	@Override
	public String getName() {
		return "RGB Cube Diagonal Cutting 1";
	}

	@Override
	public String getDescription() {
		return "RGB cube diagonal cuting with the anchors Green, Yellow, Magenta, and Blue. X-axis: Red, y-axis: Blue vs. Green";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("himberg1998enhancing", "Vesanto98", "Vesanto99som", "khedairia2008", "Bremm2011");
	}
}
