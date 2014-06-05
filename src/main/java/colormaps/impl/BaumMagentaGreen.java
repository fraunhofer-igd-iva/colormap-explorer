package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class BaumMagentaGreen extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		// red
		float r = 0.344695156761389f + 0.677707491191746f * x
				+ 256.483701136566f * y;
		if (r > 255)
			System.out.println("Red: " + r);
		r = Math.max(0, Math.min(255, r));

		// green
		float g = -0.281391097754051f + 229.326675008791f * x
				+ 30.974564200741f * y;
		if (g > 255)
			System.out.println("Green: " + g);
		g = Math.max(0, Math.min(255, g));

		// blue
		float b = 10.31667022f - 3.57053806720217f * x + 283.103063713476f * y;
		if (b > 255)
			System.out.println("Blue: " + b);
		b = Math.max(0, Math.min(255, b));

		return new Color((int) r, (int) g, (int) b);
	}

	@Override
	public String getName() {
		return "Baum et al. 2006: Magenta-Green";
	}

	@Override
	public String getDescription() {
		return "2D color lookup table by Baum et al. with Magenta-Green as a first principal diagonal";
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