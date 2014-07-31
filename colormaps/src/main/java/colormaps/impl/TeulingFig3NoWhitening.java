package colormaps.impl;

import java.awt.Color;

public class TeulingFig3NoWhitening extends TeulingStyle {

	@Override
	public Color getColor(float x, float y) {
		float r = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.32f);
		float g = getChannel(x, y, Direction.SOUTH_WEST, 0.5f);
		float b = getChannel(x, y, Direction.NORTH_WEST, 0.32f);
		r = clampSafe(r, 0.1f);
		g = clampSafe(g, 0.1f);
		b = clampSafe(b, 0.1f);
		return new Color(r,g,b);
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 3 / 4 (c) upper part.";
	}


	public String getName() {
		return "Teuling et al. Fig. 4c";
	}
}
