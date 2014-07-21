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
 * Finds the brightest color, but returns the contrast to white as a quality measure.
 * This is believed to be more relevant than dE in the whites; see
 * WCAG 1.4.3 Contrast (Minimum): The visual presentation of text and images of
 * text has a contrast ratio of at least 4.5:1, except for the following: (Level AA)
 * Large Text: Large-scale text and images of large-scale text have a contrast ratio of at least 3:1;
 * @author Simon Thum
 */
public class ColorDynamicWhiteContrast extends ColorDynamic
{
	private float brightestY = -Float.MAX_VALUE;

	/**
	 * @param sampling the sampling strategy to use
	 */
	public ColorDynamicWhiteContrast(SamplingStrategy sampling)
	{
		super(sampling);
	}
	
	@Override
	protected void addColor(PColor pcolor)
	{
		float valY = PColor.convert(pcolor, CS_CIEXYZ.instance).get(CS_CIEXYZ.Y);
		brightestY = Math.max(brightestY, valY);
	}

	/**
	 * @return the first figure in a contrast specificatrion (the n in n:1)
	 */
	@Override
	protected double getResult()
	{
		return 1d / brightestY;
	}

	@Override
	public String getName()
	{
		return "Contrast against white";
	}

	@Override
	public String getDescription()
	{
		return "Finds the contrast to white";
	}
}
