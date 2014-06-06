package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class LespintasPurpleGreen extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		// red
		float r = 48.4101328012817f - 67.7679235599738f * x + 156.264587670182f
				* y;
//		if (r > 255)
//			System.out.println("Red: " + r);
		r = Math.max(0, Math.min(255, r));

		// green
		float g = 109.79014168161f - 129.178754101385f * x + 95.3694735810212f
				* y;
//		if (g > 255)
//			System.out.println("Green: " + g);
		g = Math.max(0, Math.min(255, g));

		// blue
		float b = 49.2428539301213f - 67.7029702970293f * x + 156.629280866351f
				* y;
//		if (b > 255)
//			System.out.println("Blue: " + b);
		b = Math.max(0, Math.min(255, b));

		return new Color((int) r, (int) g, (int) b);
	}

	@Override
	public String getName() {
		return "Lespintas et al. 2011: Green-Purple";
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
