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
 * Do not re-use or distribute. Only for research and teaching purpose.
 * <p>
 * Copyright (c) 2014, University of Konstanz
 * </p>
 * 
 * @author Sebastian Mittelstaedt
 * 
 */

public class RGB extends AbstractColorSpace {
	public static double[] color2rgb(Color c) {

		double r = c.getRed() / 255.0;
		double g = c.getGreen() / 255.0;
		double b = c.getBlue() / 255.0;

		if (r == 0.0 && b == 0.0 && g == 0.0) {
			r = 1.0 / 255.0;
			g = 1.0 / 255.0;
			b = 1.0 / 255.0;
		}

		return new double[] { r, g, b };
	}

	public static Color rgb2color(double[] rgb,
			boolean returnBlackForUndefinedRGB) {

		for (int i = 0; i < rgb.length; i++) {
			if (rgb[i] < 0.0) {
				if (returnBlackForUndefinedRGB && rgb[i] < -0.03) {
					return Color.BLACK;
				} else {
					rgb[i] = 0.0;
				}

			} else if (rgb[i] > 1.0) {
				if (returnBlackForUndefinedRGB && rgb[i] > 1.03) {
					return Color.BLACK;
				} else {
					rgb[i] = 1.0;
				}
			}
		}

		int r = (int) (rgb[0] * 255.0 + 0.5);
		int g = (int) (rgb[1] * 255.0 + 0.5);
		int b = (int) (rgb[2] * 255.0 + 0.5);

		return new Color(r, g, b);
	}

	@Override
	public double[] toRGB(double[] v) {
		return v;
	}

	@Override
	public Color toColor(double[] v, boolean returnBlackForUndefinedRGB) {
		return rgb2color(v, returnBlackForUndefinedRGB);
	}

	@Override
	public double[] fromRGB(double[] rgb) {
		return rgb;
	}

	@Override
	public double[] fromColor(Color c) {
		return color2rgb(c);
	}
	
	// derive luma follwing the same whitepoint etc as in XYZ calculation
	public static double getLuma(Color c) {
		float[] rgb = c.getColorComponents(null);
		return (rgb[0] * 0.2126729) + (rgb[1] * 0.7151522)
				+ (rgb[2] * 0.0721750);
	}
	
	public static int getLumaByte(Color c) {
		return (int) (RGB.getLuma(c) * 255.0 + 0.5);
	}
}
