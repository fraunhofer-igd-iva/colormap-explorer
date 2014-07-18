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

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;

/**
 * Finds the darkest color
 * @author simon
 */
public class ColorDynamicDarkest extends ColorDynamic
{
	private float darkestY = Float.MAX_VALUE;

	/**
	 * @param sampling the sampling strategy to use
	 */
	public ColorDynamicDarkest(SamplingStrategy sampling)
	{
		super(sampling);
	}
	
	@Override
	protected void addColor(PColor pcolor)
	{
		float valY = PColor.convert(pcolor, CS_CIEXYZ.instance).get(CS_CIEXYZ.Y);
		darkestY = Math.min(darkestY, valY);
	}

	@Override
	protected double getResult()
	{
		return darkestY * 100d;
	}

	@Override
	public String getName()
	{
		return "ColorDynamics - Darkest";
	}

	@Override
	public String getDescription()
	{
		return "Finds the darkest color";
	}
}

