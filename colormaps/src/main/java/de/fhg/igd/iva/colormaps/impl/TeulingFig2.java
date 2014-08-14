package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;

public class TeulingFig2 extends TeulingStyle {

	@Override
	public Color getColor(double x, double y) {
		double r = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.72f);
		double g = getChannel(x, y, Direction.SOUTH_WEST, 0.5f);
		double b = getChannel(x, y, Direction.NORTH_WEST, 0.72f);
		return new Color((float)r, (float)g, (float)b);
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 2 or 4(b). Orientation as in Fig. 4.";
	}

	@Override
	public String getName() {
		return "Teuling et al. Fig. 2";
	}

}
