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

import de.fhg.igd.iva.colormaps.AbstractColormap2D;
import de.fhg.igd.iva.colormaps.ColorSpace;

public class BaumGreenYellowRedBlack extends AbstractColormap2D {

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		// red
		double r = 0.789864892142907f + 255.89422028982f * x + 0.0184526315177488f * y;

		r = Math.max(0, Math.min(255, r));

		// green
		double g = 0.0676231280670265f + 0.620400537432935f * x + 255.376889232313f * y;

		g = Math.max(0, Math.min(255, g));

		// blue
		double b = 1.30667120293858f - 0.0487380129975751f * x + -0.186210117808519f * y;

		b = Math.max(0, Math.min(255, b));

		return new Color((float)(r / 255), (float)(g / 255), (float)(b / 255));
	}

	@Override
	public String getName() {
		return "Baum et al. G-Y-R-B";
	}

	@Override
	public String getDescription() {
		return "2D color lookup table by Baum et al. with the 4 color anchors Green, Yellow, Red, Black";
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
