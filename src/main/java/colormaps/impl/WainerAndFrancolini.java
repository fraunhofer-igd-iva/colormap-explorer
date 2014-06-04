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
import java.util.Collections;
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;

public class WainerAndFrancolini extends AbstractColormap2D {

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
		return "WainerAndFrancolini";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with 4x4 discrete color anchors. The colors in the corners are: Red, Blue, Green, and Yellow.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.RGB;
	}

	private void initializeColorMap() {

		Color nullnull = new Color(43, 68, 143);
		Color nulleins = new Color(87, 62, 129);
		Color nullzwo = new Color(124, 58, 125);
		Color nulldrei = new Color(216, 60, 28);

		Color einsnull = new Color(40, 83, 153);
		Color einseins = new Color(80, 89, 156);
		Color einszwo = new Color(127, 88, 147);
		Color einsdrei = new Color(222, 110, 10);

		Color zwonull = new Color(31, 102, 164);
		Color zwoeins = new Color(77, 110, 170);
		Color zwozwo = new Color(126, 121, 172);
		Color zwodrei = new Color(228, 149, 9);

		Color dreinull = new Color(24, 128, 41);
		Color dreieins = new Color(80, 150, 23);
		Color dreizwo = new Color(148, 183, 9);
		Color dreidrei = new Color(251, 233, 0);

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

	@Override
	public List<String> getReferences() {
		return Collections
				.singletonList("@article{wainer1980empirical,"
						+ "title={An empirical inquiry concerning human understanding of two-variable color maps},"
						+ "author={Wainer, Howard and Francolini, Carl M},"
						+ "journal={The American Statistician},"
						+ "volume={34}," + "number={2}," + "pages={81--93},"
						+ "year={1980}," + "publisher={Taylor and Francis}"
						+ "}");
	}
}
