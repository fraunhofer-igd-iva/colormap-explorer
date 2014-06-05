/*
 * Copyright 2014 Fraunhofer IGD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class RobertsonAndOCallaghan2 extends AbstractColormap2D {

	private FourCornersAnchorColorMapParameterizable[][] colorMaps;

	@Override
	public Color getColor(float x, float y) {

		if (colorMaps == null)
			initializeColorMap();

		checkRanges(x, y);

		float fx = x;
		int indexX = 0;
		while (fx > 1 / 3.0f) {
			indexX++;
			fx -= 1 / 3.0f;
		}

		float fy = y;
		int indexY = 0;
		while (fy > 1 / 3.0f) {
			indexY++;
			fy -= 1 / 3.0f;
		}

		if (indexX == 3 || indexY == 3)
			System.out.println("argh");

		return colorMaps[indexX][indexY].getColor(fx * 3, fy * 3);
	}

	@Override
	public String getName() {
		return "Robertson and OCallaghan 1988 Orange-Green";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with 4x4 discrete color anchors. In the corners: Dark-Green, Green, Orange, Brown";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.RGB;
	}
	
	@Override
	public List<String> getReferences() {
		return Collections.singletonList("robertson1986generation");
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

		colorMaps = new FourCornersAnchorColorMapParameterizable[3][3];

		colorMaps[0][0] = new FourCornersAnchorColorMapParameterizable(nullnull, nulleins,
				einsnull, einseins);
		colorMaps[1][0] = new FourCornersAnchorColorMapParameterizable(nulleins, nullzwo,
				einseins, einszwo);
		colorMaps[2][0] = new FourCornersAnchorColorMapParameterizable(nullzwo, nulldrei,
				einszwo, einsdrei);
		colorMaps[0][1] = new FourCornersAnchorColorMapParameterizable(einsnull, einseins,
				zwonull, zwoeins);
		colorMaps[1][1] = new FourCornersAnchorColorMapParameterizable(einseins, einszwo,
				zwoeins, zwozwo);
		colorMaps[2][1] = new FourCornersAnchorColorMapParameterizable(einszwo, einsdrei,
				zwozwo, zwodrei);
		colorMaps[0][2] = new FourCornersAnchorColorMapParameterizable(zwonull, zwoeins,
				dreinull, dreieins);
		colorMaps[1][2] = new FourCornersAnchorColorMapParameterizable(zwoeins, zwozwo,
				dreieins, dreizwo);
		colorMaps[2][2] = new FourCornersAnchorColorMapParameterizable(zwozwo, zwodrei,
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
