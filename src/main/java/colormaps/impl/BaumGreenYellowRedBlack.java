package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class BaumGreenYellowRedBlack extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		// red
		float r = 0.789864892142907f + 255.89422028982f * x
				+ 0.0184526315177488f * y;
		if (r > 255)
			System.out.println("Red: " + r);
		r = Math.max(0, Math.min(255, r));

		// green
		float g = 0.0676231280670265f + 0.620400537432935f * x
				+ 255.376889232313f * y;
		if (g > 255)
			System.out.println("Green: " + g);
		g = Math.max(0, Math.min(255, g));

		// blue
		float b = 1.30667120293858f - 0.0487380129975751f * x
				+ -0.186210117808519f * y;
		if (b > 255)
			System.out.println("Blue: " + b);
		b = Math.max(0, Math.min(255, b));

		return new Color((int) r, (int) g, (int) b);
	}

	@Override
	public String getName() {
		return "Baum et al. 2006: Green, Yellow, Red, Black";
	}

	@Override
	public String getDescription() {
		return "2D color lookup table by Baum et al. with the 4 color anchors Green, Yellow, Red, Black";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.CIE_Lab;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("baum2006techniques", "baum2007investigation");
	}
}
