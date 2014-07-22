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

public class FourCornersAnchor extends AbstractColormap2D {

	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);
		
		// rot
		double rot;
		if (x <= 0.25 && y <= 0.25)
			rot = interpolate(255, 205, 255, 230, x * 4, y * 4);
		else if (x <= 0.25 && y > 0.25 && y <= 0.50)
			rot = interpolate(255, 230, 255, 255, x * 4, (y - 0.25) * 4);
		else if (x <= 0.25 && y > 0.50 && y <= 0.75)
			rot = interpolate(255, 255, 255, 230, x * 4, (y - 0.50) * 4);
		else if (x <= 0.25 && y > 0.75)
			rot = interpolate(255, 230, 255, 200, x * 4, (y - 0.75) * 4);

		else if (x > 0.25 && x <= 0.50 && y <= 0.25)
			rot = interpolate(205, 150, 230, 170, (x - 0.25) * 4, y * 4);
		else if (x > 0.25 && x <= 0.50 && y > 0.25 && y <= 0.50)
			rot = interpolate(230, 170, 255, 200, (x - 0.25) * 4, (y - 0.25) * 4);
		else if (x > 0.25 && x <= 0.50 && y > 0.50 && y <= 0.75)
			rot = interpolate(255, 200, 230, 150, (x - 0.25) * 4, (y - 0.50) * 4);
		else if (x > 0.25 && x <= 0.50 && y > 0.75)
			rot = interpolate(230, 150, 200, 128, (x - 0.25) * 4, (y - 0.75) * 4);

		else if (x > 0.50 && x <= 0.75 && y <= 0.25)
			rot = interpolate(150, 90, 170, 80, (x - 0.50) * 4, y * 4);
		else if (x > 0.50 && x <= 0.75 && y > 0.25 && y <= 0.50)
			rot = interpolate(170, 80, 200, 70, (x - 0.50) * 4, (y - 0.25) * 4);
		else if (x > 0.50 && x <= 0.75 && y > 0.50 && y <= 0.75)
			rot = interpolate(200, 70, 150, 50, (x - 0.50) * 4, (y - 0.50) * 4);
		else if (x > 0.50 && x <= 0.75 && y > 0.75)
			rot = interpolate(150, 50, 128, 50, (x - 0.50) * 4, (y - 0.75) * 4);

		else if (x > 0.75 && y <= 0.25)
			rot = interpolate(90, 40, 80, 30, (x - 0.75) * 4, y * 4);
		else if (x > 0.75 && y > 0.25 && y <= 0.50)
			rot = interpolate(80, 30, 70, 20, (x - 0.75) * 4, (y - 0.25) * 4);
		else if (x > 0.75 && y > 0.50 && y <= 0.75)
			rot = interpolate(70, 20, 50, 10, (x - 0.75) * 4, (y - 0.50) * 4);
		else if (x > 0.75 && y > 0.75)
			rot = interpolate(50, 10, 50, 0, (x - 0.75) * 4, (y - 0.75) * 4);
		else {
			System.out.println("undefined state. please refine parameters");
			rot = 0;
		}

		// grün
		double gx = x;
		double gy = y;
		double gruen = 0;
		if (gx > 0.5) // achsensymmetrisch zur yachse(bildmitte)
			gx = 1 - gx;
		if (gx <= 0.25 && gy <= 0.25)
			gruen = interpolate(255, 255, 200, 240, gx * 4, gy * 4);
		else if (gx <= 0.25 && gy > 0.25 && gy <= 0.50)
			gruen = interpolate(200, 240, 128, 160, gx * 4, (gy - 0.25) * 4);
		else if (gx <= 0.25 && gy > 0.50 && gy <= 0.75)
			gruen = interpolate(128, 160, 50, 50, gx * 4, (gy - 0.50) * 4);
		else if (gx <= 0.25 && gy > 0.75)
			gruen = interpolate(50, 50, 0, 0, gx * 4, (gy - 0.75) * 4);

		else if (gx > 0.25 && gx <= 0.50 && gy <= 0.25)
			gruen = interpolate(255, 255, 240, 255, (gx - 0.25) * 4, gy * 4);
		else if (gx > 0.25 && gx <= 0.50 && gy > 0.25 && gy <= 0.50)
			gruen = interpolate(240, 255, 160, 170, (gx - 0.25) * 4, (gy - 0.25) * 4);
		else if (gx > 0.25 && gx <= 0.50 && gy > 0.50 && gy <= 0.75)
			gruen = interpolate(160, 170, 50, 50, (gx - 0.25) * 4, (gy - 0.50) * 4);
		else if (gx > 0.25 && gx <= 0.50 && gy > 0.75)
			gruen = interpolate(50, 50, 0, 0, (gx - 0.25) * 4, (gy - 0.75) * 4);

		// blau
		double blau;
		if (x <= 0.25 && y <= 0.25)
			blau = interpolate(0, 0, 0, 60, x * 4, y * 4);
		else if (x <= 0.25 && y > 0.25 && y <= 0.50)
			blau = interpolate(0, 60, 0, 60, x * 4, (y - 0.25) * 4);
		else if (x <= 0.25 && y > 0.50 && y <= 0.75)
			blau = interpolate(0, 60, 0, 60, x * 4, (y - 0.50) * 4);
		else if (x <= 0.25 && y > 0.75)
			blau = interpolate(0, 60, 0, 60, x * 4, (y - 0.75) * 4);

		else if (x > 0.25 && x <= 0.50 && y <= 0.25)
			blau = interpolate(0, 0, 60, 60, (x - 0.25) * 4, y * 4);
		else if (x > 0.25 && x <= 0.50 && y > 0.25 && y <= 0.50)
			blau = interpolate(60, 60, 60, 200, (x - 0.25) * 4, (y - 0.25) * 4);
		else if (x > 0.25 && x <= 0.50 && y > 0.50 && y <= 0.75)
			blau = interpolate(60, 200, 60, 170, (x - 0.25) * 4, (y - 0.50) * 4);
		else if (x > 0.25 && x <= 0.50 && y > 0.75)
			blau = interpolate(60, 170, 60, 130, (x - 0.25) * 4, (y - 0.75) * 4);

		else if (x > 0.50 && x <= 0.75 && y <= 0.25)
			blau = interpolate(0, 0, 60, 60, (x - 0.50) * 4, y * 4);
		else if (x > 0.50 && x <= 0.75 && y > 0.25 && y <= 0.50)
			blau = interpolate(60, 60, 200, 170, (x - 0.50) * 4, (y - 0.25) * 4);
		else if (x > 0.50 && x <= 0.75 && y > 0.50 && y <= 0.75)
			blau = interpolate(200, 170, 170, 255, (x - 0.50) * 4, (y - 0.50) * 4);
		else if (x > 0.50 && x <= 0.75 && y > 0.75)
			blau = interpolate(170, 255, 130, 170, (x - 0.50) * 4, (y - 0.75) * 4);

		else if (x > 0.75 && y <= 0.25)
			blau = interpolate(0, 0, 60, 60, (x - 0.75) * 4, y * 4);
		else if (x > 0.75 && y > 0.25 && y <= 0.50)
			blau = interpolate(60, 60, 170, 130, (x - 0.75) * 4, (y - 0.25) * 4);
		else if (x > 0.75 && y > 0.50 && y <= 0.75)
			blau = interpolate(170, 130, 255, 170, (x - 0.75) * 4, (y - 0.50) * 4);
		else if (x > 0.75 && y > 0.75)
			blau = interpolate(255, 170, 170, 255, (x - 0.75) * 4, (y - 0.75) * 4);
		else {
			System.out.println("undefined state. please refine parameters");
			blau = 0;
		}

		return new Color((float)rot / 255f, (float)gruen / 255f, (float)blau / 255f);
	}

	@Override
	public String getName() {
		return "Four Corners R-B-G-Y";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with four color anchors in the edges: Red, Blue, Green, and Yellow.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}
	
	@Override
	public List<String> getReferences() {
		return Arrays.asList("ziegler2007visual", "bernard2013", "bernardEuroVis2014");
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
