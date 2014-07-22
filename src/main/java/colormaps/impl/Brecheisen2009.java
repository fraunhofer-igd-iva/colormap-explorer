package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class Brecheisen2009 extends AbstractColormap2D {

	FourCornersAnchorColorMapParameterizable anchorColorMapParameterizable = new FourCornersAnchorColorMapParameterizable(
			Color.MAGENTA, Color.WHITE, Color.BLACK, Color.GREEN);

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		return anchorColorMapParameterizable.getColor(x, y);
	}

	@Override
	public String getName() {
		return "Brecheisen et al.";
	}

	@Override
	public String getDescription() {
		return "2D RGB color lookup table by Brecheisen et al. with Black-White as 1st, and Magenta-Green as 2nd principal diagonal";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("Brecheisen2009");
	}
}
