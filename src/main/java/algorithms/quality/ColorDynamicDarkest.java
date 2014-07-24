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
 * Finds the darkest color
 * @author simon
 */
public class ColorDynamicDarkest extends ColorDynamic
{
	/**
	 * @param sampling the sampling strategy to use
	 */
	public ColorDynamicDarkest(SamplingStrategy sampling)
	{
		super(sampling);
	}
	
	@Override
	protected double getResult(List<PColor> colors)
	{
		float darkestY = Float.MAX_VALUE;
		for (PColor pcolor : colors)
		{
			float valY = PColor.convert(pcolor, CS_CIEXYZ.instance).get(CS_CIEXYZ.Y);
			darkestY = Math.min(darkestY, valY);
		}
		return darkestY * 100d;
	}
	
	@Override
	public boolean moreIsBetter()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "Darkest";
	}

	@Override
	public String getDescription()
	{
		return "Finds the darkest color";
	}
}

