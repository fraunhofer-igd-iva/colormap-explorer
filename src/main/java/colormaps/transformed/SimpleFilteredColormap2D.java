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

package colormaps.transformed;

import java.awt.Color;

import colormaps.Colormap2D;

/**
 * Transforms a given colormap by filtering a channel
 * @author Martin Steiger
 */
public class SimpleFilteredColormap2D extends TransformedColormap2D {

	public enum ViewType
	{
		REAL,
		LUMINANCE,
		RED,
		GREEN,
		BLUE,
		HUE,
		SATURATION,
		VALUE,
		
		TEST;
	}
	
	private ViewType viewType;

	/**
	 * @param colormap the original color map
	 * @param viewType the filter type
	 */
	public SimpleFilteredColormap2D(Colormap2D colormap, ViewType viewType)
	{
		super(colormap);
		this.viewType = viewType;
	}
	
	@Override
	public Color getColor(float mx, float my)
	{
		Color color = getColormap().getColor(mx, my);
		
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		float[] hsv;
		
		switch (viewType)
		{
		case REAL:
			return color;
		case LUMINANCE:
			int y = (int) (0.299f * red + 0.587f * green + 0.114f * blue + 0.5);
			return new Color(y, y ,y);
		case RED:
			return new Color(red, 0, 0);
		case GREEN:
			return new Color(0, green, 0);
		case BLUE:
			return new Color(0, 0, blue);
		case HUE:
			hsv = Color.RGBtoHSB(red, green, blue, null);
			Color rgb = new Color(Color.HSBtoRGB(hsv[0], 1, 1));
			return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), (int)(hsv[1] * 255 + 0.5));
		case SATURATION:
			hsv = Color.RGBtoHSB(red, green, blue, null);
			return new Color(hsv[1], hsv[1], hsv[1]);
		case VALUE:
			hsv = Color.RGBtoHSB(red, green, blue, null);
			return new Color(hsv[2], hsv[2], hsv[2]);

		default:
			return Color.LIGHT_GRAY;
		}
	}

	@Override
	public String getName() {
		return viewType.name();
	}

	@Override
	public String getDescription() {
		return "Filtered views on a colormap";
	}
}
