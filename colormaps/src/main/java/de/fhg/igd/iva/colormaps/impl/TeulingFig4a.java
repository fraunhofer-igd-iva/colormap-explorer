package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;

public class TeulingFig4a extends TeulingStyle {

	@Override
	public Color getColor(double x, double y) {
		double r = getChannel(x, y, Direction.SOUTH_WEST, 0.5);
		double g = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.2);
		double b = getChannel(x, y, Direction.NORTH_WEST, 0.2);
		// some inaccuracies seem to make use of clampSafe manadatory here.
		return new Color((float) clampSafe(r, 0.001), (float) clampSafe(g, 0.001), (float) clampSafe(b, 0.001));
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 4(a). This is as described geometrically, which is not really in line with the shown colormap.";
//		return "See Teuling paper fig. 4(a). This is as described geomerically, which is not "
//				+ "really in line with the colormap given in figure 4(a). "
//				+ "It seems that it only served explanatory purposes in the paper.";
	}

	@Override
	public String getName() {
		return "Teuling et al. Fig. 4a";
	}

}
