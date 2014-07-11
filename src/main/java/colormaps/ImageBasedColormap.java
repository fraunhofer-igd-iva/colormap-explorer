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
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

/**
 * An abstract colormap that is based on an image
 * @author Martin Steiger
 */
public abstract class ImageBasedColormap extends AbstractColormap2D 
{
	private final BufferedImage image;

	/**
	 * @param image The image to use for sampling
	 */
	public ImageBasedColormap(BufferedImage image)
	{
		this.image = image;
	}

	@Override
	public Color getColor(float mx, float my)
	{
		checkRanges(mx, my);

		double x = mx * (image.getWidth() - 1);
		double y = my * (image.getHeight() - 1);
		
		int minX = (int)Math.floor(x);
        int maxX = (int)Math.ceil(x);

        int minY = (int)Math.floor(y);
        int maxY = (int)Math.ceil(y);
        
        Color q00 = readColor(minX, minY);
        Color q10 = readColor(maxX, minY);
        Color q01 = readColor(minX, maxY);
        Color q11 = readColor(maxX, maxY);

        double ipx = x - minX;
        double ipy = y - minY;

        Color color = bilerp(q00, q10, q01, q11, ipx, ipy);

        return color;
	}

	private Color readColor(int x, int y)
	{
		WritableRaster raster = image.getRaster();
		
		// TODO: make this more general
		if (raster.getSampleModel().getDataType() == DataBuffer.TYPE_FLOAT)
		{		
			float r = raster.getSampleFloat(x, y, 0);
			float g = raster.getSampleFloat(x, y, 1);
			float b = raster.getSampleFloat(x, y, 2);
		
			return new Color(r, g, b);
		}
		
		if (raster.getSampleModel().getDataType() == DataBuffer.TYPE_BYTE)
		{		
			int r = raster.getSample(x, y, 0);
			int g = raster.getSample(x, y, 1);
			int b = raster.getSample(x, y, 2);
		
			return new Color(r, g, b);
		}
		
		throw new UnsupportedOperationException();
	}

	protected static Color bilerp(Color xt1, Color xt2, Color xb1, Color xb2, double ipx, double ipy) 
	{
		float[] arrt1 = xt1.getColorComponents(new float[3]);
		float[] arrt2 = xt2.getColorComponents(new float[3]);
		
		double rt = arrt1[0] * (1.0 - ipx) + arrt2[0] * ipx;
		double gt = arrt1[1] * (1.0 - ipx) + arrt2[1] * ipx;
		double bt = arrt1[2] * (1.0 - ipx) + arrt2[2] * ipx;

		float[] arrb1 = xb1.getColorComponents(new float[3]);
		float[] arrb2 = xb2.getColorComponents(new float[3]);
		
		double rb = arrb1[0] * (1.0 - ipx) + arrb2[0] * ipx;
		double gb = arrb1[1] * (1.0 - ipx) + arrb2[1] * ipx;
		double bb = arrb1[2] * (1.0 - ipx) + arrb2[2] * ipx;

		double r = rt * (1.0 - ipy) + rb * ipy;
		double g = gt * (1.0 - ipy) + gb * ipy;
		double b = bt * (1.0 - ipy) + bb * ipy;

		return new Color((float)r, (float)g, (float)b);
    }
	
	/**
	 * @return the underlying image
	 */
	public final BufferedImage getImage()
	{
		return image;
	}
}
