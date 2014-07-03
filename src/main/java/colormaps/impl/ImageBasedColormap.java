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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import colormaps.AbstractColormap2D;

/**
 * An abstract colormap that is based on an image
 * @author Martin Steiger
 */
public abstract class ImageBasedColormap extends AbstractColormap2D {

	private final BufferedImage image;

	/**
	 * @param imagePath the (relative) path to the image
	 * @throws IOException if the image cannot be loaded
	 */
	public ImageBasedColormap(String imagePath) throws IOException
	{
		URL imageUrl = getClass().getResource(imagePath);
		
		this.image = ImageIO.read(imageUrl);
	}
	
	@Override
	public Color getColor(float x, float y) {
		checkRanges(x, y);
		
		int imgX = (int) (x * (image.getWidth() - 1) + 0.5);
		int imgY = (int) (y * (image.getHeight() - 1) + 0.5);
		
		return new Color(image.getRGB(imgX, imgY));
	}

	/**
	 * @return the underlying image
	 */
	public BufferedImage getImage()
	{
		return image;
	}
}
