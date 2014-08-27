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
public class XYZ extends AbstractColorSpace{

	public static final double[] D65 = {95.047 ,	100.000 ,	108.883};

	public static double[] rgb2xyz(double[] rgb) {

		double varR = rgb[0];
		double varG = rgb[1];
		double varB = rgb[2];

		if (varR > 0.04045) {
			varR = Math.pow(((varR + 0.055) / 1.055), 2.4);
		} else {
			varR = varR / 12.92;
		}

		if (varG > 0.04045) {
			varG = Math.pow(((varG + 0.055) / 1.055), 2.4);
		} else {
			varG = varG / 12.92;
		}

		if (varB > 0.04045) {
			varB = Math.pow(((varB + 0.055) / 1.055), 2.4);
		} else {
			varB = varB / 12.92;
		}

		varR = varR * 100.0;
		varG = varG * 100.0;
		varB = varB * 100.0;

		// sRGB, Illuminant = D65
		double x = (varR * 0.4124564) + (varG * 0.3575761)
				+ (varB * 0.1804375);
		double y = (varR * 0.2126729) + (varG * 0.7151522)
				+ (varB * 0.0721750);
		double z = (varR * 0.0193339) + (varG * 0.1191920)
				+ (varB * 0.9503041);

		return new double[] { x, y, z };
	}

	public static double[] xyz2rgb(double[] xyz) {

		double varX = xyz[0] / 100.0;
		double varY = xyz[1] / 100.0;
		double varZ = xyz[2] / 100.0;

		// sRGB, Illuminant = D65
		double varR = (varX * 3.2404542) + (varY * -1.5371385)
				+ (varZ * -0.4985314);
		double varG = (varX * -0.9692660) + (varY * 1.8760108)
				+ (varZ * 0.0415560);
		double varB = (varX * 0.0556434) + (varY * -0.2040259)
				+ (varZ * 1.0572252);

		if (varR > 0.0031308) {
			varR = 1.055 * (Math.pow(varR, (1.0 / 2.4))) - 0.055;
		} else {
			varR = 12.92 * varR;
		}
		if (varG > 0.0031308) {
			varG = 1.055 * (Math.pow(varG, (1.0 / 2.4))) - 0.055;
		} else {
			varG = 12.92 * varG;
		}
		if (varB > 0.0031308) {
			varB = 1.055 * (Math.pow(varB, (1.0 / 2.4))) - 0.055;
		} else {
			varB = 12.92 * varB;
		}

		double[] rgb = new double[] { varR, varG, varB };

		fixRoundingErrors(rgb);

		return rgb;

	}

	@Override
	public double[] toRGB(double[] v) {
		return xyz2rgb(v);
	}

	@Override
	public Color toColor(double[] v, boolean returnBlackForUndefinedRGB) {
		return RGB.rgb2color(xyz2rgb(v), returnBlackForUndefinedRGB);
	}

	@Override
	public double[] fromRGB(double[] rgb) {
		return rgb2xyz(rgb);
	}

	@Override
	public double[] fromColor(Color c) {
		return rgb2xyz(RGB.color2rgb(c));
	}
}
