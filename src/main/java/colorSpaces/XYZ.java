package colorSpaces;

import java.awt.Color;

/**
 * Do not re-use or distribute. Only for research and teaching purpose.
 * <p>
 * Copyright (c) 2014, University of Konstanz
 * </p>
 * @author Sebastian Mittelstaedt
 *
 */

public class XYZ extends AbstractColorSpace{
	
	public static double[] D65 = {95.047 ,	100.000 ,	108.883};
	
	public static double[] rgb2xyz(double[] rgb) {

		double var_R = rgb[0];
		double var_G = rgb[1];
		double var_B = rgb[2];

		if (var_R > 0.04045) {
			var_R = Math.pow(((var_R + 0.055) / 1.055), 2.4);
		} else {
			var_R = var_R / 12.92;
		}

		if (var_G > 0.04045) {
			var_G = Math.pow(((var_G + 0.055) / 1.055), 2.4);
		} else {
			var_G = var_G / 12.92;
		}

		if (var_B > 0.04045) {
			var_B = Math.pow(((var_B + 0.055) / 1.055), 2.4);
		} else {
			var_B = var_B / 12.92;
		}

		var_R = var_R * 100.0;
		var_G = var_G * 100.0;
		var_B = var_B * 100.0;

		// sRGB, Illuminant = D65
		double X = (var_R * 0.4124564) + (var_G * 0.3575761)
				+ (var_B * 0.1804375);
		double Y = (var_R * 0.2126729) + (var_G * 0.7151522)
				+ (var_B * 0.0721750);
		double Z = (var_R * 0.0193339) + (var_G * 0.1191920)
				+ (var_B * 0.9503041);

		return new double[] { X, Y, Z };
	}

	public static double[] xyz2rgb(double[] xyz) {

		double var_X = xyz[0] / 100.0;
		double var_Y = xyz[1] / 100.0;
		double var_Z = xyz[2] / 100.0;

		// sRGB, Illuminant = D65
		double var_R = (var_X * 3.2404542) + (var_Y * -1.5371385)
				+ (var_Z * -0.4985314);
		double var_G = (var_X * -0.9692660) + (var_Y * 1.8760108)
				+ (var_Z * 0.0415560);
		double var_B = (var_X * 0.0556434) + (var_Y * -0.2040259)
				+ (var_Z * 1.0572252);

		if (var_R > 0.0031308) {
			var_R = 1.055 * (Math.pow(var_R, (1.0 / 2.4))) - 0.055;
		} else {
			var_R = 12.92 * var_R;
		}
		if (var_G > 0.0031308) {
			var_G = 1.055 * (Math.pow(var_G, (1.0 / 2.4))) - 0.055;
		} else {
			var_G = 12.92 * var_G;
		}
		if (var_B > 0.0031308) {
			var_B = 1.055 * (Math.pow(var_B, (1.0 / 2.4))) - 0.055;
		} else {
			var_B = 12.92 * var_B;
		}

		return new double[] { var_R, var_G, var_B };

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
