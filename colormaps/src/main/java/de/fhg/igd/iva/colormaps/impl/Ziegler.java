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
import java.util.Arrays;
import java.util.List;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

/**
 * @author Taunus Knudsen
 * @since 2012-01-10
 * @version 1.01
 */
public class Ziegler extends AbstractColormap {

	public Ziegler() {
	}

	@Override
	public Color getColor(double valueX1, double valueX2) {
		
		// TODO: This should be transformed into a set of 16 {@link FourCornersAnchorColorMapParameterizable} 
		// maps, similar to RobertsonAndOCallaghan
		
		//rot
		double rot;
		if (valueX1 <= 0.25 && valueX2 <= 0.25)
			rot = interpolate(255, 205, 255, 230, valueX1*4, valueX2*4);
		else if (valueX1 <= 0.25 && valueX2 > 0.25 && valueX2 <= 0.50)
			rot = interpolate(255, 230, 255, 255, valueX1*4, (valueX2-0.25)*4);
		else if (valueX1 <= 0.25 && valueX2 > 0.50 && valueX2 <= 0.75)
			rot = interpolate(255, 255, 255, 230, valueX1*4, (valueX2-0.50)*4);
		else if (valueX1 <= 0.25 && valueX2 > 0.75)
			rot = interpolate(255, 230, 255, 200, valueX1*4, (valueX2-0.75)*4);
		
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 <= 0.25)
			rot = interpolate(205, 150, 230, 170, (valueX1-0.25)*4, valueX2*4);
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 > 0.25 && valueX2 <= 0.50)
			rot = interpolate(230, 170, 255, 200, (valueX1-0.25)*4, (valueX2-0.25)*4);
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 > 0.50 && valueX2 <= 0.75)
			rot = interpolate(255, 200, 230, 150, (valueX1-0.25)*4, (valueX2-0.50)*4);
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 > 0.75)
			rot = interpolate(230, 150, 200, 128, (valueX1-0.25)*4, (valueX2-0.75)*4);
		
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 <= 0.25)
			rot = interpolate(150, 90, 170, 80, (valueX1-0.50)*4, valueX2*4);
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 > 0.25 && valueX2 <= 0.50)
			rot = interpolate(170, 80, 200, 70, (valueX1-0.50)*4, (valueX2-0.25)*4);
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 > 0.50 && valueX2 <= 0.75)
			rot = interpolate(200, 70, 150, 50, (valueX1-0.50)*4, (valueX2-0.50)*4);
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 > 0.75)
			rot = interpolate(150, 50, 128, 50, (valueX1-0.50)*4, (valueX2-0.75)*4);
		
		else if (valueX1 > 0.75 && valueX2 <= 0.25)
			rot = interpolate(90, 40, 80, 30, (valueX1-0.75)*4, valueX2*4);
		else if (valueX1 > 0.75 && valueX2 > 0.25 && valueX2 <= 0.50)
			rot = interpolate(80, 30, 70, 20, (valueX1-0.75)*4, (valueX2-0.25)*4);
		else if (valueX1 > 0.75 && valueX2 > 0.50 && valueX2 <= 0.75)
			rot = interpolate(70, 20, 50, 10, (valueX1-0.75)*4, (valueX2-0.50)*4);
		else if (valueX1 > 0.75 && valueX2 > 0.75)
			rot = interpolate(50, 10, 50, 0, (valueX1-0.75)*4, (valueX2-0.75)*4);
		else{
			System.out.println("undefined state. please refine parameters");
			rot = 0;
		}		
		int r = (int) rot;

		//grün
		double gx = valueX1;
		double gy = valueX2;
		double gruen = 0;
		if (gx > 0.5) //achsensymmetrisch zur yachse(bildmitte)
			gx = 1-gx;
		if (gx <= 0.25 && gy <= 0.25)
			gruen = interpolate(255, 255, 200, 240, gx*4, gy*4);
		else if (gx <= 0.25 && gy > 0.25 && gy <= 0.50)
			gruen = interpolate(200, 240, 128, 160, gx*4, (gy-0.25)*4);
		else if (gx <= 0.25 && gy > 0.50 && gy <= 0.75)
			gruen = interpolate(128, 160, 50, 50, gx*4, (gy-0.50)*4);
		else if (gx <= 0.25 && gy > 0.75)
			gruen = interpolate(50, 50, 0, 0, gx*4, (gy-0.75)*4);
		
		else if (gx > 0.25 && gx <= 0.50 && gy <= 0.25)
			gruen = interpolate(255, 255, 240, 255, (gx-0.25)*4, gy*4);
		else if (gx > 0.25 && gx <= 0.50 && gy > 0.25 && gy <= 0.50)
			gruen = interpolate(240, 255, 160, 170, (gx-0.25)*4, (gy-0.25)*4);
		else if (gx > 0.25 && gx <= 0.50 && gy > 0.50 && gy <= 0.75)
			gruen = interpolate(160, 170, 50, 50, (gx-0.25)*4, (gy-0.50)*4);
		else if (gx > 0.25 && gx <= 0.50 && gy > 0.75)
			gruen = interpolate(50, 50, 0, 0, (gx-0.25)*4, (gy-0.75)*4);			
		int g = (int) gruen;		
		
		//blau
		double blau;
		if (valueX1 <= 0.25 && valueX2 <= 0.25)
			blau = interpolate(0, 0, 0, 60, valueX1*4, valueX2*4);
		else if (valueX1 <= 0.25 && valueX2 > 0.25 && valueX2 <= 0.50)
			blau = interpolate(0, 60, 0, 60, valueX1*4, (valueX2-0.25)*4);
		else if (valueX1 <= 0.25 && valueX2 > 0.50 && valueX2 <= 0.75)
			blau = interpolate(0, 60, 0, 60, valueX1*4, (valueX2-0.50)*4);
		else if (valueX1 <= 0.25 && valueX2 > 0.75)
			blau = interpolate(0, 60, 0, 60, valueX1*4, (valueX2-0.75)*4);
		
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 <= 0.25)
			blau = interpolate(0,0,60,60, (valueX1-0.25)*4, valueX2*4);
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 > 0.25 && valueX2 <= 0.50)
			blau = interpolate(60,60, 60, 200, (valueX1-0.25)*4, (valueX2-0.25)*4);
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 > 0.50 && valueX2 <= 0.75)
			blau = interpolate(60, 200, 60, 170, (valueX1-0.25)*4, (valueX2-0.50)*4);
		else if (valueX1 > 0.25 && valueX1 <= 0.50 && valueX2 > 0.75)
			blau = interpolate(60, 170, 60, 130, (valueX1-0.25)*4, (valueX2-0.75)*4);
		
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 <= 0.25)
			blau = interpolate(0,0,60,60, (valueX1-0.50)*4, valueX2*4);
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 > 0.25 && valueX2 <= 0.50)
			blau = interpolate(60,60, 200, 170, (valueX1-0.50)*4, (valueX2-0.25)*4);
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 > 0.50 && valueX2 <= 0.75)
			blau = interpolate(200, 170, 170, 255, (valueX1-0.50)*4, (valueX2-0.50)*4);
		else if (valueX1 > 0.50 && valueX1 <= 0.75 && valueX2 > 0.75)
			blau = interpolate(170, 255, 130, 170, (valueX1-0.50)*4, (valueX2-0.75)*4);
		
		else if (valueX1 > 0.75 && valueX2 <= 0.25)
			blau = interpolate(0,0,60,60, (valueX1-0.75)*4, valueX2*4);
		else if (valueX1 > 0.75 && valueX2 > 0.25 && valueX2 <= 0.50)
			blau = interpolate(60,60, 170, 130, (valueX1-0.75)*4, (valueX2-0.25)*4);
		else if (valueX1 > 0.75 && valueX2 > 0.50 && valueX2 <= 0.75)
			blau = interpolate(170, 130, 255, 170, (valueX1-0.75)*4, (valueX2-0.50)*4);
		else if (valueX1 > 0.75 && valueX2 > 0.75)
			blau = interpolate(255, 170, 170, 255, (valueX1-0.75)*4, (valueX2-0.75)*4);
		else{
			System.out.println("undefined state. please refine parameters");
			blau = 0;
		}	
		int b = (int) blau;

		return new Color(r, g, b);
	}

	private double interpolate(double start, double end, double position) {
		return start + (end - start) * position;
	}

	private double interpolate(double lo, double ro, double lu, double ru, double positionX, double positionY) {
		double o = interpolate(lo, ro, positionX);
		double u = interpolate(lu, ru, positionX);
		return interpolate(o, u, positionY);
	}
	
	@Override
	public String getName()
	{
		return "Ziegler et al.";
	}

	@Override
	public String getDescription()
	{
		return "RGB colormap with 4x4 discrete color anchors. In the corners: Yellow, Green, Blue, Red";
	}

	@Override
	public ColorSpace getColorSpace()
	{
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() 
	{
		return Arrays.asList("ziegler2007visual", "bernard2013", "bernardEuroVis2014");
	}
}
