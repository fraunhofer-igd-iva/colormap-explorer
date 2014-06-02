package colormaps.impl;

import java.awt.Color;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class RGBFourAnchorColorMapDynamic extends AbstractColormap2D {

	private Color lowerLeft;
	private Color lowerRight;
	private Color upperRight;
	private Color upperLeft;

	public RGBFourAnchorColorMapDynamic(Color upperLeft, Color upperRight,
			Color lowerLeft, Color lowerRight) {

		if (lowerLeft == null || lowerRight == null || upperLeft == null
				|| upperRight == null)
			throw new IllegalArgumentException(
					"one of the color objects was null!");

		this.lowerLeft = lowerLeft;
		this.lowerRight = lowerRight;
		this.upperRight = upperRight;
		this.upperLeft = upperLeft;
	}

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		double r = interpolate(upperLeft.getRed(), upperRight.getRed(),
				lowerLeft.getRed(), lowerRight.getRed(), x, y);
		double g = interpolate(upperLeft.getGreen(), upperRight.getGreen(),
				lowerLeft.getGreen(), lowerRight.getGreen(), x, y);
		double b = interpolate(upperLeft.getBlue(), upperRight.getBlue(),
				lowerLeft.getBlue(), lowerRight.getBlue(), x, y);

		return new Color((int) r, (int) g, (int) b);
	}

	@Override
	public String getName() {
		return "RGBFourAnchorColorMapDynamic";
	}

	@Override
	public String getDescription() {
		return "Colormap2D with four anchor colors. the four colors are assigned at run-time.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.RGB;
	}

	private double interpolate(double start, double end, double position) {
		return start + (end - start) * position;
	}

	private double interpolate(double lo, double ro, double lu, double ru,
			double positionX, double positionY) {
		// TODO: needs testing!
		double o = interpolate(lo, ro, positionX);
		double u = interpolate(lu, ru, positionX);
		return interpolate(o, u, positionY);
	}

}
