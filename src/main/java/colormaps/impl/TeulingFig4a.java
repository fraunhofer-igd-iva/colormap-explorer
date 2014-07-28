package colormaps.impl;

import java.awt.Color;

import tiling.Direction;

public class TeulingFig4a extends TeulingStyle {

	@Override
	public Color getColor(float x, float y) {
		float r = getChannel(x, y, Direction.SOUTH_WEST, 0.5f);
		float g = getChannel(x, y, Direction.SOUTH_EAST, 1 - 0.2f);
		float b = getChannel(x, y, Direction.NORTH_WEST, 0.2f);
		// some inaccuracies seem to make use of clampSafe manadatory here.
		return new Color(clampSafe(r, 0.001f), clampSafe(g, 0.001f), clampSafe(b, 0.001f));
	}

	@Override
	public String getDescription() {
		return "See Teuling paper fig. 4(a). This is as described geometrically, which is not really in line with the shown colormap.";
//		return "See Teuling paper fig. 4(a). This is as described geomerically, which is not "
//				+ "really in line with the colormap given in figure 4(a). "
//				+ "It seems that it only served explanatory purposes in the paper.";
	}

	public String getName() {
		return "Teuling et al. Fig. 4a";
	}

}
