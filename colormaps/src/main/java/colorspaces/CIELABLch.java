/*
 * Copyright (c) 2014, University of Konstanz
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
package colorspaces;

import java.awt.Color;

/**
 * @author Sebastian Mittelstaedt
 */
public class CIELABLch extends AbstractColorSpace {
	
	public static double[] rgb2lch(double[] rgb) {
		double[] lab = CIELAB.rgb2lab(rgb);
		return lab2lch(lab);
	}
	
	public static double[] lch2rgb(double[] lch) {
		double[] lab = lch2lab(lch);
		return CIELAB.lab2rgb(lab);
	}
	
	public static double[] lab2lch(double[] lab) {
		double l = lab[0];
		double a = lab[1];
		double b = lab[2];

		double c = Math.sqrt(a * a + b * b);
		double h = Math.atan2(b, a) / Math.PI * 180.0;

		return new double[] { l, c, h };
	}

	public static double[] lch2lab(double[] lch) {
		double l = lch[0];
		double c = lch[1];
		double h = lch[2] / 180.0 * Math.PI;

		double a = c * Math.cos(h);
		double b = c * Math.sin(h);

		return new double[] { l, a, b };
	}
	
	@Override
	public double[] toRGB(double[] v) {
		return lch2rgb(v);
	}

	@Override
	public Color toColor(double[] v, boolean returnBlackForUndefinedRGB) {
		return RGB.rgb2color(lch2rgb(v), returnBlackForUndefinedRGB);
	}

	@Override
	public double[] fromRGB(double[] rgb) {
		return rgb2lch(rgb);
	}

	@Override
	public double[] fromColor(Color c) {
		return rgb2lch(RGB.color2rgb(c));
	}
}
