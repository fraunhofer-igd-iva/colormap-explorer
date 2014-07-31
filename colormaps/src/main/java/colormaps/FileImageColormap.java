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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Loads a colormap from an image file
 * @author Martin Steiger
 */
public abstract class FileImageColormap extends ImageBasedColormap
{
	/**
	 * @param imagePath the (relative) path to the image
	 * @throws IOException if the image cannot be loaded
	 */
	public FileImageColormap(String imagePath) throws IOException
	{
		super(loadImage(imagePath));
	}
	
	private static BufferedImage loadImage(String imagePath) throws IOException
	{
		URL imageUrl = FileImageColormap.class.getResource(imagePath);
		
		BufferedImage image = ImageIO.read(imageUrl);
		return image;
	}
}
