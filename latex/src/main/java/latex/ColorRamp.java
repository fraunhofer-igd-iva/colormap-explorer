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

package latex;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Hue-Lightness colormap that between (dark) blue and (light) yellow. 
 * Preserves contrast to black and white colors. Lookup colormap creation at:
 * http://tristen.ca/hcl-picker/#/hlc/9/1/2E4052/F9E261 
 * @author Juergen Bernard
 */
public class ColorRamp
{
	private final List<Color> colors = Lists.newArrayList();
	
	/**
	 * @param colors
	 */
	public ColorRamp(Color c0, Color... more)
	{
		colors.add(c0);
		colors.addAll(Arrays.asList(more));
	}

	/** 
	 * linear min/max scaling; assumes value in [0.0 .. 1.0]
	 * @param value
	 * @return
	 */
	public Color getColor(double value)
	{
		double indexExact = value * (colors.size() - 1);
		int indexLow = (int) Math.floor(indexExact);
		if (indexLow >= colors.size())
		{
			indexLow = colors.size() - 1;
		}
		if (indexLow < 0)
		{
			indexLow = 0;
		}
		int indexHigh = indexLow + 1;
		if (indexHigh >= colors.size())
		{
			indexHigh = colors.size() - 1;
		}
		if (indexHigh < 0)
		{
			indexHigh = 0;
		}
		if (indexLow == indexHigh)
			return colors.get(indexLow);
		
		Color cL = colors.get(indexLow);
		Color cH = colors.get(indexHigh);
		int r = (int) ((1 - (indexExact - indexLow)) * cL.getRed() + (1 - (indexHigh - indexExact)) * cH.getRed());
		int g = (int) ((1 - (indexExact - indexLow)) * cL.getGreen() + (1 - (indexHigh - indexExact)) * cH.getGreen());
		int b = (int) ((1 - (indexExact - indexLow)) * cL.getBlue() + (1 - (indexHigh - indexExact)) * cH.getBlue());
		return new Color(r, g, b);
	}
}
