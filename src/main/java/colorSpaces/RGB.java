package colorSpaces;

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

		double r = (double) c.getRed() / 255.0;
		double g = (double) c.getGreen() / 255.0;
		double b = (double) c.getBlue() / 255.0;

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
}
