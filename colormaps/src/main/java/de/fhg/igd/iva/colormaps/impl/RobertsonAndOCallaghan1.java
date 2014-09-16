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

package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import com.google.common.math.DoubleMath;

import de.fhg.igd.iva.colormaps.AbstractKnownColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

public class RobertsonAndOCallaghan1 extends AbstractKnownColormap {

	private FourCornersAnchorColorMapParameterizable[][] colorMaps;

	@Override
	public Color getColor(double x, double y) {

		if (colorMaps == null)
			initializeColorMap();

		checkRanges(x, y);

		double df = 1 / 3.0;

		int indexX = DoubleMath.roundToInt(x / df, RoundingMode.FLOOR);
		int indexY = DoubleMath.roundToInt(y / df, RoundingMode.FLOOR);
		double fx = 3.0 * (x % df);
		double fy = 3.0 * (y % df);

		// explicitly test for this corner cases -> rounding errors
		if (x == 1.0)
		{
			indexX = 2;
			fx = 1.0;
		}

		if (y == 1.0)
		{
			indexY = 2;
			fy = 1.0;
		}

		return colorMaps[indexX][indexY].getColor(fx, fy);
	}

	@Override
	public String getName() {
		return "Robertson and OCallaghan";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with 4x4 discrete color anchors. In the corners: Light-Orange, Brown, Dark-Brown, Blue";
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

		Color nullnull = new Color(198, 119, 95);
		Color nulleins = new Color(212, 137, 100);
		Color nullzwo = new Color(225, 174, 113);
		Color nulldrei = new Color(247, 225, 168);

		Color einsnull = new Color(126, 92, 78);
		Color einseins = new Color(156, 118, 100);
		Color einszwo = new Color(164, 145, 111);
		Color einsdrei = new Color(172, 196, 158);

		Color zwonull = new Color(84, 73, 72);
		Color zwoeins = new Color(80, 74, 70);
		Color zwozwo = new Color(106, 119, 106);
		Color zwodrei = new Color(87, 130, 131);

		Color dreinull = new Color(58, 60, 59);
		Color dreieins = new Color(56, 65, 65);
		Color dreizwo = new Color(65, 80, 84);
		Color dreidrei = new Color(77, 106, 125);

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

}
