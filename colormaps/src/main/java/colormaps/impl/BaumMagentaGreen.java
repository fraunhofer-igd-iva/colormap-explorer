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


/**
 * @deprecated Another magenta/green colormap. This one with black/white corners this is suboptimal compared
 * to the other colormaps in the paper
 * @author ??
 */
@Deprecated
public class BaumMagentaGreen extends AbstractColormap2D {

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		// red
		double r = 0.344695156761389f + 0.677707491191746f * x + 256.483701136566f * y;

		r = Math.max(0, Math.min(255, r));

		// green
		double g = -0.281391097754051f + 229.326675008791f * x + 30.974564200741f * y;

		g = Math.max(0, Math.min(255, g));

		// blue
		double b = 10.31667022f - 3.57053806720217f * x + 283.103063713476f * y;

		b = Math.max(0, Math.min(255, b));

		return new Color((float)(r / 255), (float)(g / 255), (float)(b / 255));
	}

	@Override
	public String getName() {
		return "Baum et al. M-G";
	}

	@Override
	public String getDescription() {
		return "2D CIELab color lookup table by Baum et al. with Magenta-Green as a first principal diagonal";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("baum2006genetic", "baum2007investigation");
	}
}
