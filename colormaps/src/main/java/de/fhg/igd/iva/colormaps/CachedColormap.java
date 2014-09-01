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


package de.fhg.igd.iva.colormaps;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * A cached, but otherwise transparent {@link KnownColormap}
 * wrapper implementation.
 * @author Martin Steiger
 */
public class CachedColormap extends ImageBasedColormap
{
	private final Colormap delegate;

	/**
	 * @param colormap the underlying color map
	 * @param imgWidth the width of the cache image
	 * @param imgHeight the height of the cache image
	 */
	public CachedColormap(Colormap colormap, int imgWidth, int imgHeight)
	{
		super(createImage(imgWidth, imgHeight));

		this.delegate = colormap;

		float maxY = imgHeight - 1;
		float maxX = imgWidth - 1;

		float[] compArray = new float[3];

		WritableRaster raster = getImage().getRaster();

		for (int y = 0; y < imgHeight; y++)
		{
			float my = y / maxY;
			for (int x = 0; x < imgWidth; x++)
			{
				float mx = x / maxX;
				Color color = colormap.getColor(mx, my);
				color.getColorComponents(compArray);

				raster.setSample(x, y, 0, compArray[0]);
				raster.setSample(x, y, 1, compArray[1]);
				raster.setSample(x, y, 2, compArray[2]);
			}
		}
	}

	private static BufferedImage createImage(int imgWidth, int imgHeight)
	{
		java.awt.color.ColorSpace space = java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_sRGB);
		ColorModel colorModel = new ComponentColorModel(space, false, false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);
		BandedSampleModel model = new BandedSampleModel(DataBuffer.TYPE_FLOAT, imgWidth, imgHeight, 3);
		DataBuffer buffer = model.createDataBuffer();
		WritableRaster raster = Raster.createWritableRaster(model, buffer, null);
		BufferedImage image = new BufferedImage(colorModel, raster, false, null);

//		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

		return image;
	}

	@Override
	public Color getColor(double mx, double my)
	{
		return super.getColor(mx, my);
	}

	/**
	 * Returns the color as defined in the original colormap
	 * @param x the x coordinate in the range [0..1]
	 * @param y the x coordinate in the range [0..1]
	 * @return the uncached color
	 */
	public Color getUncachedColor(double x, double y)
	{
		return delegate.getColor(x, y);
	}
}
