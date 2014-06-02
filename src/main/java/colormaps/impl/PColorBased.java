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

package colormaps.impl;

import java.awt.Color;
import java.awt.color.ColorSpace;

import colormaps.AbstractColormap2D;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.sRGB;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;

/**
 * Helper class fr implementing PColor-based color maps.
 * @author simon
 */
public abstract class PColorBased extends AbstractColormap2D
{
	/**
	 * Like {@link #getColor(float, float)} but returns a pcolor.
	 * @param x the x coordinate in the range [0..1]
	 * @param y the y coordinate in the range [0..1]
	 * @return a pcolor as appropriate for the color map
	 */
	public abstract PColor getPColor(float x, float y);

	@Override
	public Color getColor(float x, float y)
	{
		PColor color = getPColor(x, y);
		PColor srgb = PColor.convert(color, CS_sRGB.instance);
		PColor out;
		if (srgb.isInRange(0.0f, 0.0f))
		{
			out = srgb;
		}
		else
		{
			// TODO clamp is not the best idea but it's unclear what Guo did
			// also ATM we do not have a way of evaluating the delta E in the UI
			// meaning we should have a color class supporting further analysis.
			out = clampRgb((sRGB) srgb);
		}
		return new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB), out.getComponents(), 1.0f);
	}

	private sRGB clampRgb(sRGB c)
	{
		int[] rgb = new int[4];
		for (int j = 0; j < 4; j++)
			rgb[j] = c.getByte(j);

		return sRGB.fromBytes(rgb);
	}

}
