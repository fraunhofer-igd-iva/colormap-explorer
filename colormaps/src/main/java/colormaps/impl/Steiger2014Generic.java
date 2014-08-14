/*
 * Copyright (c) 2014, University of Konstanz
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
import java.util.List;

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;
import colorspaces.AbstractColorSpace;
import colorspaces.CAT02;
import colorspaces.CIELAB;
import colorspaces.HSI;

/**
 * 
 * @author Sebastian Mittelstädt
 *
 */
public class Steiger2014Generic extends AbstractColormap2D {
	
	private Color topLeft, topRight, bottumRight, bottumLeft;
    private double[] tl,tr,br,bl;
    private AbstractColorSpace colorSpace;
    
    public Steiger2014Generic() {
    	
    	AbstractColorSpace cs_cat02 = new CAT02();
    	AbstractColorSpace cs_hsi = new HSI();
    	
    	Color[] colors = new Color[4];
    	double[][] lms = {{100.0,100.0,0},{0.0,100.0,100.0},{0.0,0,100},{100.0,0,0}};
    	
    	for (int i = 0; i < colors.length; i++) {	
    		double[] rgb = cs_cat02.toRGB(lms[i]);
    		double[] hsi = cs_hsi.fromRGB(rgb);
			colors[i] = cs_hsi.toColor(hsi, false);
		}
    	
        topLeft = colors[0];
        topRight = colors[1];
        bottumRight = colors[2];
        bottumLeft = colors[3];
        
        colorSpace = new CIELAB();
        
        tl = colorSpace.fromColor(topLeft);
        tr = colorSpace.fromColor(topRight);
        br = colorSpace.fromColor(bottumRight);
        bl = colorSpace.fromColor(bottumLeft);
    }

	@Override
	public Color getColor(double x, double y) {
		double[] low = new double[3];
        double[] top = new double[3];
        double[] k = new double[3];
        for (int i = 0; i < top.length; i++) {
            top[i] = (1.0-x)*tl[i] + x*tr[i];
            low[i] = (1.0-x)*bl[i] + x*br[i];
            k[i] = (1.0-y)*top[i] + y*low[i];
        }
        return colorSpace.toColor(k, false);
	}

	@Override
	public String getDescription() {
		return "Generic CIELab-based colormap with perceptually (almost) equi-distant corners";
	}
	
	@Override
	public String getName()
	{
		return "Steiger et al. Cyan";
	}

	@Override
	public ColorSpace getColorSpace()
	{
		return ColorSpace.CIE_Lab;
	}

	@Override
	public List<String> getReferences()
	{
		return Arrays.asList("steiger2014");
	}

}
