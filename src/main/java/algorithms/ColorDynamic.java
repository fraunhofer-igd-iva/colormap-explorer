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


package algorithms;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colormaps.Colormap2D;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Compute global statistics for a color maps
 * @author simon
 */
public class ColorDynamic
{
	private static final Logger logger = LoggerFactory.getLogger(ColorDynamic.class);

	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	
	CIEXYZ WHITE = CIEXYZ.fromCIEXYZ100(100, 100, 100);
	CIEXYZ BLACK = CIEXYZ.fromCIEXYZ100(0, 0, 0);
	
	private final Colormap2D colormap;
	
	int frequency;

	float dE_white = 1000, dE_black = 1000, brightestY = 0, darkestY = 1000;
	
	/**
	 * @param colormap the colormap to use
	 * @param jndThreshold the jnd threshold for the set of distinguishable points
	 */
	public ColorDynamic(Colormap2D colormap, int frequency)
	{
		this.colormap = colormap;
		this.frequency = frequency;
	}

	public void computeStats()
	{
		for (int y = 0; y < frequency; y++)
		{
			float my = y / (float)(frequency - 1);
			for (int x = 0; x < frequency; x++)
			{
				float mx = x / (float)(frequency - 1);
				
				Color color = colormap.getColor(mx, my);
				PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

				update(pcolor);
			}
			
			logger.debug(String.format("Sampling: %3.0f%%", 100d * y / frequency));
		}
		
		logger.debug("Sampling: 100%");
	}
	
	// update the statistics
	private void update(PColor pcolor) {
		float Y = PColor.convert(pcolor, CS_CIEXYZ.instance).get(CS_CIEXYZ.Y);
		darkestY = Math.min(darkestY, Y);
		brightestY = Math.max(brightestY, Y);
		dE_white = Math.min(dE_white, ColorTools.distance(pcolor, WHITE, VIEW_ENV));
		dE_black = Math.min(dE_black, ColorTools.distance(pcolor, BLACK, VIEW_ENV));
	}

	public Colormap2D getColormap() {
		return colormap;
	}

	public float getdE_white() {
		return dE_white;
	}

	public float getdE_black() {
		return dE_black;
	}

	public float getBrightestY() {
		return brightestY * 100;
	}

	public float getDarkestY() {
		return darkestY * 100;
	}

}
