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

import java.util.List;

import algorithms.sampling.SamplingStrategy;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;

/**
 * Finds the brightest color
 * @author simon
 */
public class ColorDynamicBrightest extends ColorDynamic
{
	/**
	 * @param sampling the sampling strategy to use
	 */
	public ColorDynamicBrightest(SamplingStrategy sampling)
	{
		super(sampling);
	}
	
	@Override
	protected double getResult(List<PColor> colors)
	{
		float brightestY = -Float.MAX_VALUE;

		for (PColor color : colors)
		{
			float valY = PColor.convert(color, CS_CIEXYZ.instance).get(CS_CIEXYZ.Y);
			brightestY = Math.max(brightestY, valY);
		}
		return brightestY * 100d;
	}
	
	@Override
	public boolean moreIsBetter()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "Brightest";
	}

	@Override
	public String getDescription()
	{
		return "Finds the brightest color";
	}
}
