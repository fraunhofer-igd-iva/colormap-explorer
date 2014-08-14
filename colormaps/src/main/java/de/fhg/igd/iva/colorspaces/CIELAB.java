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
package de.fhg.igd.iva.colorspaces;

import java.awt.Color;

/**
 * @author Sebastian Mittelstaedt
 */
public class CIELAB extends AbstractColorSpace {
	
	public static double[] rgb2lab(double[] rgb) {
		double[] xyz = XYZ.rgb2xyz(rgb);
		return xyz2lab(xyz);
	}
	
	public static double[] lab2rgb(double[] lab) {
		double[] xyz = lab2xyz(lab);
		return XYZ.xyz2rgb(xyz);
	}
	
	public static double[] xyz2lab(double[] xyz) {
		double[] white = XYZ.D65;

		double varX = xyz[0] / white[0];
		double varY = xyz[1] / white[1];
		double varZ = xyz[2] / white[2];

		if (varX > 0.008856) {
			varX = Math.pow(varX, (1.0 / 3.0));
		} else {
			varX = (7.787 * varX) + (16.0 / 116.0);
		}
		if (varY > 0.008856) {
			varY = Math.pow(varY, (1.0 / 3.0));
		} else {
			varY = (7.787 * varY) + (16.0 / 116.0);
		}
		if (varZ > 0.008856) {
			varZ = Math.pow(varZ, (1.0 / 3.0));
		} else {
			varZ = (7.787 * varZ) + (16.0 / 116.0);
		}

		double l = (116.0 * varY) - 16.0;
		double a = 500.0 * (varX - varY);
		double b = 200.0 * (varY - varZ);

		return new double[] { l, a, b };
	}

	public static double[] lab2xyz(double[] lab) {
		double varY = (lab[0] + 16.0) / 116.0;
		double varX = lab[1] / 500.0 + varY;
		double varZ = varY - lab[2] / 200.0;

		if (Math.pow(varY, 3.0) > 0.008856) {
			varY = Math.pow(varY, 3.0);
		} else {
			varY = (varY - 16.0 / 116.0) / 7.787;
		}
		if (Math.pow(varX, 3.0) > 0.008856) {
			varX = Math.pow(varX, 3.0);
		} else {
			varX = (varX - 16.0 / 116.0) / 7.787;
		}
		if (Math.pow(varZ, 3.0) > 0.008856) {
			varZ = Math.pow(varZ, 3.0);
		} else {
			varZ = (varZ - 16.0 / 116.0) / 7.787;
		}

		double[] white = XYZ.D65;

		double x = white[0] * varX; // ref_X = 95.047 Observer= 2°,
										// Illuminant= D65
		double y = white[1] * varY; // ref_Y = 100.000
		double z = white[2] * varZ; // ref_Z = 108.883
		return new double[] { x, y, z };
	}
	
	@Override
	public double[] toRGB(double[] v) {
		return lab2rgb(v);
	}

	@Override
	public Color toColor(double[] v, boolean returnBlackForUndefinedRGB) {
		return RGB.rgb2color(lab2rgb(v), returnBlackForUndefinedRGB);
	}

	@Override
	public double[] fromRGB(double[] rgb) {
		return rgb2lab(rgb);
	}

	@Override
	public double[] fromColor(Color c) {
		return rgb2lab(RGB.color2rgb(c));
	}
}
