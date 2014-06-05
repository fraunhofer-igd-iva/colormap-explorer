package colormaps.impl;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class Baum2DLookupTable extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);

		return new Color(x, x, y);
	}

	@Override
	public String getName() {
		return "Baum et al. 2006";
	}

	@Override
	public String getDescription() {
		return "2D color lookup table by Baum et al. used for the fusion of multimodal images.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.RGB;
	}

	@Override
	public List<String> getReferences() {
		return Collections.singletonList("baum2006techniques");
	}
}
