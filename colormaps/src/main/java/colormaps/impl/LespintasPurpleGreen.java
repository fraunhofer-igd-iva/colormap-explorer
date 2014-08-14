package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

/**
 * @deprecated black-white diagonal.
 * @author JB
 */
@Deprecated
public class LespintasPurpleGreen extends AbstractColormap2D {

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		// red
		double r = 48.4101328012817f - 67.7679235599738f * x + 156.264587670182f * y;

		r = Math.max(0, Math.min(255, r));

		// green
		double g = 109.79014168161f - 129.178754101385f * x + 95.3694735810212f * y;

		g = Math.max(0, Math.min(255, g));

		// blue
		double b = 49.2428539301213f - 67.7029702970293f * x + 156.629280866351f * y;

		b = Math.max(0, Math.min(255, b));

		return new Color((float)(r / 255), (float)(g / 255), (float)(b / 255));
	}

	@Override
	public String getName() {
		return "Lespinats and Aupetit";
	}

	@Override
	public String getDescription() {
		return "2D CIELab color lookup table by Lespintas et al. with the 1st principal axis White-Black and the 2nd Purple-Green";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.CIE_Lab;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("lespinats2011checkviz");
	}
}
