package colormaps.impl;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.hypot;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;
import colorspaces.HSL;

public class Ramirez2012 extends AbstractColormap2D {

	Logger _log = LoggerFactory.getLogger(getClass());
	
	@Override
	public Color getColor(double x, double y) {
		// normalize to -1..1
		double nx = (float) (x * 2 - 1);
		double ny = (float) (y * 2 - 1);
		double dist = hypot(nx, ny) / hypot(1, 1);
		double ang = (atan2(nx, ny)+PI)/(PI*2);
		// HSL matches as good or bad as HSI - need HSV?
		//
		//double[] rgb = HSI.hsi2rgb(new double[] {ang * PI * 2, dist, (float)(1f-dist/2f)});
		//return new Color((float)rgb[0], (float)rgb[1], (float)rgb[2]);
		return HSL.HSLtoRGB((float)ang, (float)dist, (float)(1f-dist/2f));
	}

	@Override
	public String getName() {
		return "Ramirez2012";
	}

	@Override
	public String getDescription() {
		return "HSL-based map with white center and maximal saturation.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.HSV;
	}

	@Override
	public List<String> getReferences() {
		return Collections.singletonList("ramirez2012self");
	}

	@Override
	protected Color getColor(float x, float y) {
		throw new IllegalStateException("this method shuld not be called");
	}

}
