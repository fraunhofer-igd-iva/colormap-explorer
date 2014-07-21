package colormaps.impl;

import java.awt.Color;

import tiling.Direction;

public class TeulingFig2 extends TeulingStyle {

	@Override
	public Color getColor(float x, float y) {
		float r = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.72f);
		float g = getChannel(x, y, Direction.SOUTH_WEST, 0.5f);
		float b = getChannel(x, y, Direction.NORTH_WEST, 0.72f);
		return new Color(r,g,b);
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 2 or 4(b). Orientation as in Fig. 4.";
	}


}
