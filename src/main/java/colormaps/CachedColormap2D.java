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


package colormaps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A cached, but otherwise transparent {@link Colormap2D} 
 * wrapper implementation.
 * @author Martin Steiger
 */
public class CachedColormap2D extends DelegateColormap2D
{
	private final BufferedImage img;

	/**
	 * @param colormap the underlying color map
	 * @param imgWidth the width of the cache image
	 * @param imgHeight the height of the cache image
	 */
	public CachedColormap2D(Colormap2D colormap, int imgWidth, int imgHeight)
	{
		super(colormap);
		
		this.img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

		float maxY = imgHeight - 1;
		float maxX = imgWidth - 1;

		for (int y = 0; y < imgHeight; y++)
		{
			float my = y / maxY;
			for (int x = 0; x < imgWidth; x++)
			{
				float mx = x / maxX;
				Color color = colormap.getColor(mx, my);
				img.setRGB(x, y, color.getRGB());
			}
		}
	}
	
	@Override
	public Color getColor(float mx, float my)
	{
		// TODO: consider using bi-linear interpolation between the four neighboring pixels
		
		int x = (int) (mx * (img.getWidth() - 1) + 0.5);
		int y = (int) (my * (img.getHeight() - 1) + 0.5);
		
		int rgb = img.getRGB(x, y);
		Color color = new Color(rgb);
		
		return color;
	}

	/**
	 * @return the cached image
	 */
	public Image getImage()
	{
		return img;
	}
}
