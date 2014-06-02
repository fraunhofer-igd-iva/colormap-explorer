package colormaps.impl;

import java.awt.Color;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class RGBRobertsonAndOCallaghan2 extends AbstractColormap2D {

	RGBFourAnchorColorMapDynamic[][] colorMaps = null;

	@Override
	public Color getColor(float x, float y) {

		if (colorMaps == null)
			initializeColorMap();

		checkRanges(x, y);

		float x_ = x;
		int indexX = 0;
		while (x_ > 1 / 3.0f) {
			indexX++;
			x_ -= 1 / 3.0f;
		}

		float y_ = y;
		int indexY = 0;
		while (y_ > 1 / 3.0f) {
			indexY++;
			y_ -= 1 / 3.0f;
		}

		if (indexX == 3 || indexY == 3)
			System.out.println("argh");

		return colorMaps[indexX][indexY].getColor(x_ * 3, y_ * 3);
	}

	@Override
	public String getName() {
		return "RGBRobertsonAndOCallaghan2";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with 4x4 discrete color anchors. The colors in the corners are: Dark-Green, Green, Orange, Brown";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.RGB;
	}

	private void initializeColorMap() {

		Color nullnull = new Color(113,80,79);
		Color nulleins = new Color(90,76,75);
		Color nullzwo = new Color(66,66,69);
		Color nulldrei = new Color(73,88,84);

		Color einsnull = new Color(162,110,98);
		Color einseins = new Color(127,96,91);
		Color einszwo = new Color(86,82,81);
		Color einsdrei = new Color(77,108,93);

		Color zwonull = new Color(180,113,93);
		Color zwoeins = new Color(145,101,84);
		Color zwozwo = new Color(113,98,92);
		Color zwodrei = new Color(79,123,99);

		Color dreinull = new Color(235,128,94);
		Color dreieins = new Color(182,114,88);
		Color dreizwo = new Color(134,103,86);
		Color dreidrei = new Color(91,132,102);

		colorMaps = new RGBFourAnchorColorMapDynamic[3][3];

		colorMaps[0][0] = new RGBFourAnchorColorMapDynamic(nullnull, nulleins,
				einsnull, einseins);
		colorMaps[1][0] = new RGBFourAnchorColorMapDynamic(nulleins, nullzwo,
				einseins, einszwo);
		colorMaps[2][0] = new RGBFourAnchorColorMapDynamic(nullzwo, nulldrei,
				einszwo, einsdrei);
		colorMaps[0][1] = new RGBFourAnchorColorMapDynamic(einsnull, einseins,
				zwonull, zwoeins);
		colorMaps[1][1] = new RGBFourAnchorColorMapDynamic(einseins, einszwo,
				zwoeins, zwozwo);
		colorMaps[2][1] = new RGBFourAnchorColorMapDynamic(einszwo, einsdrei,
				zwozwo, zwodrei);
		colorMaps[0][2] = new RGBFourAnchorColorMapDynamic(zwonull, zwoeins,
				dreinull, dreieins);
		colorMaps[1][2] = new RGBFourAnchorColorMapDynamic(zwoeins, zwozwo,
				dreieins, dreizwo);
		colorMaps[2][2] = new RGBFourAnchorColorMapDynamic(zwozwo, zwodrei,
				dreizwo, dreidrei);
	}

	private double interpolate(double start, double end, double position) {
		return start + (end - start) * position;
	}

	private double interpolate(double lo, double ro, double lu, double ru,
			double positionX, double positionY) {
		// TODO: needs testing!
		double o = interpolate(lo, ro, positionX);
		double u = interpolate(lu, ru, positionX);
		return interpolate(o, u, positionY);
	}
}
