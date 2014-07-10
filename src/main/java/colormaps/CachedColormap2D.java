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

import com.google.common.base.Preconditions;

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
		Preconditions.checkArgument(0 <= mx && mx <= 1, "X must in in range [0..1], but is %s", mx);
		Preconditions.checkArgument(0 <= my && my <= 1, "Y must in in range [0..1], but is %s", my);

		double x = mx * (img.getWidth() - 1);
		double y = my * (img.getHeight() - 1);
		
		int minX = (int)Math.floor(x);
        int maxX = (int)Math.ceil(x);

        int minY = (int)Math.floor(y);
        int maxY = (int)Math.ceil(y);

        Color q00 = new Color(img.getRGB(minX, minY));
        Color q10 = new Color(img.getRGB(maxX, minY));
        Color q01 = new Color(img.getRGB(minX, maxY));
        Color q11 = new Color(img.getRGB(maxX, maxY));

        double ipx = x - minX;
        double ipy = y - minY;

        Color color = bilerp(q00, q10, q01, q11, ipx, ipy);

        return color;
	}

	private static Color bilerp(Color xt1, Color xt2, Color xb1, Color xb2, double p, double ipy) 
	{
		float[] arrt1 = xt1.getColorComponents(new float[3]);
		float[] arrt2 = xt2.getColorComponents(new float[3]);
		
		double rt = arrt1[0] * (1.0 - p) + arrt2[0] * p;
		double gt = arrt1[1] * (1.0 - p) + arrt2[1] * p;
		double bt = arrt1[2] * (1.0 - p) + arrt2[2] * p;

		float[] arrb1 = xb1.getColorComponents(new float[3]);
		float[] arrb2 = xb2.getColorComponents(new float[3]);
		
		double rb = arrb1[0] * (1.0 - p) + arrb2[0] * p;
		double gb = arrb1[1] * (1.0 - p) + arrb2[1] * p;
		double bb = arrb1[2] * (1.0 - p) + arrb2[2] * p;

		double r = rt * (1.0 - ipy) + rb * ipy;
		double g = gt * (1.0 - ipy) + gb * ipy;
		double b = bt * (1.0 - ipy) + bb * ipy;

		return new Color((float)r, (float)g, (float)b);
    }
	
	/**
	 * @return the cached image
	 */
	public Image getImage()
	{
		return img;
	}
}
