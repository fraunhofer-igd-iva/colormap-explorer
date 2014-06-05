package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class BaumOrangeCyan extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		// red
		float r = 0.547322350173516f + 0.692515727985112f * x
				+ 255.732690566668f * y;
		if (r > 255)
			System.out.println("Red: " + r);
		r = Math.max(0, Math.min(255, r));

		// green
		float g = -2.38928709411272f + 173.55305419884f * x + 86.4399107353958f
				* y;
		if (g > 255)
			System.out.println("Green: " + g);
		g = Math.max(0, Math.min(255, g));

		// blue
		float b = -0.0456529636621121f + 256.889116983918f * x
				+ 1.16866281852388f * y;
		if (b > 255)
			System.out.println("Blue: " + b);
		b = Math.max(0, Math.min(255, b));

		return new Color((int) r, (int) g, (int) b);
	}

	@Override
	public String getName() {
		return "Baum et al. 2006: Orange-Cyan";
	}

	@Override
	public String getDescription() {
		return "2D color lookup table by Baum et al. with Orange-Cyan as a first principal diagonal";
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
