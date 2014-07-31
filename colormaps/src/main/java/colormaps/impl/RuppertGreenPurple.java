package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

/**
 * 
 * @author jubernar
 * @deprecated Zwei mal Ruppert brauchen wir fürs Paper nicht, da die beiden
 *             colormaps nicht für unsere Tasks geeignet sind. die Orange Blue
 *             schneidet etwas besser ab, daher bleibt die drin.
 * 
 */
@Deprecated
public class RuppertGreenPurple extends AbstractColormap2D {

	FourCornersAnchorColorMapParameterizable anchorColorMapParameterizable = new FourCornersAnchorColorMapParameterizable(new Color(59, 197, 85), new Color(128, 128, 128), new Color(128, 128, 128), new Color(181, 75, 160));

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		return anchorColorMapParameterizable.getColor(x, y);
	}

	@Override
	public String getName() {
		return "Ruppert et al. G-P";
	}

	@Override
	public String getDescription() {
		return "2D RGB color lookup table by Ruppert et al. with constant Gray as 1st, and Green-Magenta as 2nd principal diagonal.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("Ruppert2014");
	}
}
