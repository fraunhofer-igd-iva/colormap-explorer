package colormaps.impl;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class SimulaHSB1 extends AbstractColormap2D {

	@Override
	public Color getColor(double x, double y) {
		// normalize to -1..1
		double nx = (float) (x * 2 - 1);
		double ny = (float) (y * 2 - 1);
		double dist = max(abs(ny), abs(nx));
		double ang = Math.atan2(ny, nx)/(Math.PI*2);
		return Color.getHSBColor((float)ang, 1f, (float)dist);
	}

	@Override
	public String getName() {
		return "Simula 99 SOM Code HSB";
	}

	@Override
	public String getDescription() {
		return "HSB-based map with black center and constant saturation.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.HSV;
	}

	@Override
	public List<String> getReferences() {
		return Collections.singletonList("Simula99");
	}

	@Override
	protected Color getColor(float x, float y) {
		throw new IllegalStateException("this method shuld not be called");
	}

}
