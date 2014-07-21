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

/**
 * Hue-Lightness colormap that between (dark) blue and (light) yellow. 
 * Preserves contrast to black and white colors. Lookup colormap creation at:
 * http://tristen.ca/hcl-picker/#/hlc/9/1/2E4052/F9E261 
 * @author Juergen Bernard
 */
public class ColorRampBGLY
{
	private final Color[] colors = new Color[] 
	{ 
		new Color(46, 64, 82), new Color(42, 86, 101), new Color(35, 110, 114),
		new Color(39, 133, 120), new Color(63, 155, 120), new Color(99, 177, 114), 
		new Color(143, 196, 105), new Color(193, 213, 98), new Color(249, 226, 97) 
	};

	/** 
	 * linear min/max scaling; assumes value in [0.0 .. 1.0]
	 * @param value
	 * @return
	 */
	public Color getColor(double value)
	{
		double indexExact = value * (colors.length - 1);
		int indexLow = (int) Math.floor(indexExact);
		if (indexLow >= colors.length)
		{
			indexLow = colors.length - 1;
		}
		if (indexLow < 0)
		{
			indexLow = 0;
		}
		int indexHigh = indexLow + 1;
		if (indexHigh >= colors.length)
		{
			indexHigh = colors.length - 1;
		}
		if (indexHigh < 0)
		{
			indexHigh = 0;
		}
		if (indexLow == indexHigh)
			return colors[indexLow];
		
		Color cL = colors[indexLow];
		Color cH = colors[indexHigh];
		int r = (int) ((1 - (indexExact - indexLow)) * cL.getRed() + (1 - (indexHigh - indexExact)) * cH.getRed());
		int g = (int) ((1 - (indexExact - indexLow)) * cL.getGreen() + (1 - (indexHigh - indexExact)) * cH.getGreen());
		int b = (int) ((1 - (indexExact - indexLow)) * cL.getBlue() + (1 - (indexHigh - indexExact)) * cH.getBlue());
		return new Color(r, g, b);
	}
}
