package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class Yeo1 extends AbstractColormap2D {

	FourCornersAnchorColorMapParameterizable anchorColorMapParameterizable = new FourCornersAnchorColorMapParameterizable(new Color(0, 128, 128), new Color(128, 0, 255), new Color(128,255,0), new Color(255, 128, 128));

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		return anchorColorMapParameterizable.getColor(x, y);
	}

	@Override
	public String getName() {
		return "Yeo et al.";
	}

	@Override
	public String getDescription() {
		return "Topological map in geometric RGB color space. Colors: Green, Dark Green, Pink, Purple";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("yeo2005colour");
	}
}
