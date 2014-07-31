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


package algorithms.quality;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.util.List;

import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;

import com.google.common.collect.Lists;

import de.fhg.igd.pcolor.PColor;

/**
 * Compute global statistics for a color maps
 * @author simon
 */
public abstract class ColorDynamic implements ColormapQuality
{
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	
	private SamplingStrategy sampling;
	
	/**
	 * @param sampling the sampling to use
	 */
	public ColorDynamic(SamplingStrategy sampling)
	{
		this.sampling = sampling;
	}
	
	@Override
	public double getQuality(Colormap2D colormap)
	{
		List<PColor> colors = Lists.newArrayList();
		for (Point2D pt : sampling.getPoints())
		{
			Color color = colormap.getColor(pt.getX(), pt.getY());
			PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
			colors.add(pcolor);
		}
		
		return getResult(colors);
	}

	protected abstract double getResult(List<PColor> colors);
}
