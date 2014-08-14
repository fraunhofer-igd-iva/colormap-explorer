package colormaps.impl;

import java.awt.Color;

public class TeulingFig3NoWhitening extends TeulingStyle {

	@Override
	public Color getColor(double x, double y) {
		double r = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.32);
		double g = getChannel(x, y, Direction.SOUTH_WEST, 0.5);
		double b = getChannel(x, y, Direction.NORTH_WEST, 0.32);
		r = clampSafe(r, 0.1f);
		g = clampSafe(g, 0.1f);
		b = clampSafe(b, 0.1f);
		return new Color((float) r, (float) g, (float) b);
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 3 / 4 (c) upper part.";
	}

	@Override
	public String getName() {
		return "Teuling et al. Fig. 4c";
	}
}
