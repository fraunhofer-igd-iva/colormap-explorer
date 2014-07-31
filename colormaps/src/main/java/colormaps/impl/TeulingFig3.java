package colormaps.impl;

import java.awt.Color;

public class TeulingFig3 extends TeulingStyle {

	@Override
	public Color getColor(float x, float y) {
		float r = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.32f);
		float g = getChannel(x, y, Direction.SOUTH_WEST, 0.5f);
		float b = getChannel(x, y, Direction.NORTH_WEST, 0.32f);
		float w = getWhitening(x, y, 0.5f);
		r = clampSafe(r+w, 0.1f);
		g = clampSafe(g+w, 0.1f);
		b = clampSafe(b+w, 0.1f);
		return new Color(r,g,b);
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 3 / 4 (c). This colormap is as described, not as shown in the paper. Orientation as in Fig. 4";
//		return "See Teuling paper fig. 3 / 4 (c). Note: In the paper, (at least) the green channel is "
//				+ "not maxed out in the SW corner, suggesting there are more tweaks than described. This colormap "
//				+ "is as described, not as shown in the paper. Orientation as in Fig. 4";
	}


}
