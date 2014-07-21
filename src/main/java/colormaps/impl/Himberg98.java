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
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;


/**
 * @Deprecated identical to CubeDiagonalCutting1
 * @author jubernar
 *
 */
public class Himberg98 extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		return new Color(y, (1-x), x);
	}

	@Override
	public String getName() {
		return "Himberg 1998";
	}

	@Override
	public String getDescription() {
		return "[Identical with Diagonal Cutting 1] RGB Colormap with linear interpolated color chanels. X-axis: green vs. blue, Y-axis: red.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}
	
	@Override
	public List<String> getReferences()
	{
		return Collections.singletonList("himberg1998enhancing");
	}
}
