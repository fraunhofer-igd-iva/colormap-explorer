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

		double var_X = xyz[0] / white[0];
		double var_Y = xyz[1] / white[1];
		double var_Z = xyz[2] / white[2];

		if (var_X > 0.008856) {
			var_X = Math.pow(var_X, (1.0 / 3.0));
		} else {
			var_X = (7.787 * var_X) + (16.0 / 116.0);
		}
		if (var_Y > 0.008856) {
			var_Y = Math.pow(var_Y, (1.0 / 3.0));
		} else {
			var_Y = (7.787 * var_Y) + (16.0 / 116.0);
		}
		if (var_Z > 0.008856) {
			var_Z = Math.pow(var_Z, (1.0 / 3.0));
		} else {
			var_Z = (7.787 * var_Z) + (16.0 / 116.0);
		}

		double l = (116.0 * var_Y) - 16.0;
		double a = 500.0 * (var_X - var_Y);
		double b = 200.0 * (var_Y - var_Z);

		return new double[] { l, a, b };
	}

	public static double[] lab2xyz(double[] lab) {
		double var_Y = (lab[0] + 16.0) / 116.0;
		double var_X = lab[1] / 500.0 + var_Y;
		double var_Z = var_Y - lab[2] / 200.0;

		if (Math.pow(var_Y, 3.0) > 0.008856) {
			var_Y = Math.pow(var_Y, 3.0);
		} else {
			var_Y = (var_Y - 16.0 / 116.0) / 7.787;
		}
		if (Math.pow(var_X, 3.0) > 0.008856) {
			var_X = Math.pow(var_X, 3.0);
		} else {
			var_X = (var_X - 16.0 / 116.0) / 7.787;
		}
		if (Math.pow(var_Z, 3.0) > 0.008856) {
			var_Z = Math.pow(var_Z, 3.0);
		} else {
			var_Z = (var_Z - 16.0 / 116.0) / 7.787;
		}

		double[] white = XYZ.D65;

		double X = white[0] * var_X; // ref_X = 95.047 Observer= 2°,
										// Illuminant= D65
		double Y = white[1] * var_Y; // ref_Y = 100.000
		double Z = white[2] * var_Z; // ref_Z = 108.883
		return new double[] { X, Y, Z };
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
