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
 * Finds the brightest color, but returns the contrast to white as a quality measure.
 * This is believed to be more relevant than dE in the whites; see
 * WCAG 1.4.3 Contrast (Minimum): The visual presentation of text and images of
 * text has a contrast ratio of at least 4.5:1, except for the following: (Level AA)
 * Large Text: Large-scale text and images of large-scale text have a contrast ratio of at least 3:1;
 * @author Simon Thum
 */
public class ColorDynamicWhiteContrast extends ColorDynamic
{
	/**
	 * @param sampling the sampling strategy to use
	 */
	public ColorDynamicWhiteContrast(SamplingStrategy sampling)
	{
		super(sampling);
	}
	

	/**
	 * @return the first figure in a contrast specification (the n in n:1)
	 */
	@Override
	protected double getResult(List<PColor> colors)
	{
		float brightestY = -Float.MAX_VALUE;
		
		for (PColor pcolor : colors)
		{
			float valY = PColor.convert(pcolor, CS_CIEXYZ.instance).get(CS_CIEXYZ.Y);
			brightestY = Math.max(brightestY, valY);
		}
	
		return 1.0 / brightestY;
	}
	
	@Override
	public boolean moreIsBetter()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "White Contrast";
	}

	@Override
	public String getDescription()
	{
		return "The maximum contrast to white";
	}
}
