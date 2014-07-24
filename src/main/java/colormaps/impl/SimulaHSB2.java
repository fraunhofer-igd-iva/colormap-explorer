package colormaps.impl;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;
import colorspaces.HSI;

/**
 * @deprecated There is no smooth transition at the left side (implementation bug?)
 * @author Simon Thum
 */
@Deprecated
public class SimulaHSB2 extends AbstractColormap2D {
	
	@Override
	public Color getColor(double x, double y) {
		// normalize to -1..1
		double nx = (float) (x * 2 - 1);
		double ny = (float) (y * 2 - 1);
		double dist = max(abs(ny), abs(nx));
		double ang = Math.atan2(ny, nx);
		double[] rgb = HSI.hsi2rgb(new double[] {ang, 1, dist});
		return new Color((float)rgb[0], (float)rgb[1], (float)rgb[2]);
	}

	@Override
	public String getName() {
		return "Circular Hue (HSI)";
	}

	@Override
	public String getDescription() {
		return "HSI-based map with black center and constant saturation.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.HSV;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("simula1999som", "himberg2001knowledgeengineering", "wallet2009latent", "royColormap2010", "matos2010seismic");
	}

	@Override
	protected Color getColor(float x, float y) {
		throw new IllegalStateException("this method shuld not be called");
	}

}
