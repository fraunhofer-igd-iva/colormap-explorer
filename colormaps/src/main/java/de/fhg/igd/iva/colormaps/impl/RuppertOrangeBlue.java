package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

@Deprecated
public class RuppertOrangeBlue extends AbstractColormap {

	FourCornersAnchorColorMapParameterizable anchorColorMapParameterizable = new FourCornersAnchorColorMapParameterizable(new Color(255, 127, 2), new Color(128, 128, 128), new Color(128, 128, 128), new Color(19, 70, 237));

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		return anchorColorMapParameterizable.getColor(x, y);
	}

	@Override
	public String getName() {
		return "Ruppert et al. O-B";
	}

	@Override
	public String getDescription() {
		return "2D RGB color lookup table by Ruppert et al. with constant Gray as 1st, and Orange-Blue as 2nd principal diagonal.";
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
