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

import algorithms.sampling.SamplingStrategy;

import de.fhg.igd.pcolor.CIEXYZ;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Compute global statistics for a color map
 * @author simon
 */
public class ColorDynamicDistWhite extends ColorDynamic
{
	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final CIEXYZ WHITE = CIEXYZ.fromCIEXYZ100(100, 100, 100);

	private float distWhite = Float.MAX_VALUE;

	/**
	 * @param sampling the sampling strategy to use
	 */
	public ColorDynamicDistWhite(SamplingStrategy sampling)
	{
		super(sampling);
	}
	
	@Override
	protected void addColor(PColor pcolor)
	{
		distWhite = Math.min(distWhite, ColorTools.distance(pcolor, WHITE, VIEW_ENV));
	}

	@Override
	protected double getResult()
	{
		return distWhite;
	}

	@Override
	public String getName()
	{
		return "WhiteDistance";
	}

	@Override
	public String getDescription()
	{
		return "Finds the smallest distance to white";
	}
	
}
