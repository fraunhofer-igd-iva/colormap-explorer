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

/**
 * 
 * @author jubernar
 * @deprecated we had three of these. The decision was made to use the variant
 *             with Light-Orange, Brown, Dark-Brown, Blue colors since it
 *             performed slightly better. [JB]
 */
public class RobertsonAndOCallaghan3 extends AbstractColormap2D {

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
		return "Robertson and OCallaghan 3";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with 4x4 discrete color anchors. In the corners: Light-Orange, Pink, Anthracite, Blue";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Collections.singletonList("robertson1986generation");
	}

	private void initializeColorMap() {

		Color nullnull = new Color(178, 102, 128);
		Color nulleins = new Color(195, 132, 137);
		Color nullzwo = new Color(216, 179, 160);
		Color nulldrei = new Color(247, 218, 170);

		Color einsnull = new Color(120, 87, 100);
		Color einseins = new Color(152, 104, 134);
		Color einszwo = new Color(166, 138, 159);
		Color einsdrei = new Color(197, 178, 160);

		Color zwonull = new Color(74, 67, 72);
		Color zwoeins = new Color(98, 87, 111);
		Color zwozwo = new Color(98, 112, 166);
		Color zwodrei = new Color(120, 137, 156);

		Color dreinull = new Color(59, 56, 59);
		Color dreieins = new Color(63, 61, 71);
		Color dreizwo = new Color(60, 67, 78);
		Color dreidrei = new Color(78, 99, 119);

		colorMaps = new FourCornersAnchorColorMapParameterizable[3][3];

		colorMaps[0][0] = new FourCornersAnchorColorMapParameterizable(nullnull, nulleins, einsnull, einseins);
		colorMaps[1][0] = new FourCornersAnchorColorMapParameterizable(nulleins, nullzwo, einseins, einszwo);
		colorMaps[2][0] = new FourCornersAnchorColorMapParameterizable(nullzwo, nulldrei, einszwo, einsdrei);
		colorMaps[0][1] = new FourCornersAnchorColorMapParameterizable(einsnull, einseins, zwonull, zwoeins);
		colorMaps[1][1] = new FourCornersAnchorColorMapParameterizable(einseins, einszwo, zwoeins, zwozwo);
		colorMaps[2][1] = new FourCornersAnchorColorMapParameterizable(einszwo, einsdrei, zwozwo, zwodrei);
		colorMaps[0][2] = new FourCornersAnchorColorMapParameterizable(zwonull, zwoeins, dreinull, dreieins);
		colorMaps[1][2] = new FourCornersAnchorColorMapParameterizable(zwoeins, zwozwo, dreieins, dreizwo);
		colorMaps[2][2] = new FourCornersAnchorColorMapParameterizable(zwozwo, zwodrei, dreizwo, dreidrei);
	}

	private double interpolate(double start, double end, double position) {
		return start + (end - start) * position;
	}

	private double interpolate(double lo, double ro, double lu, double ru, double positionX, double positionY) {
		// TODO: needs testing!
		double o = interpolate(lo, ro, positionX);
		double u = interpolate(lu, ru, positionX);
		return interpolate(o, u, positionY);
	}
}
