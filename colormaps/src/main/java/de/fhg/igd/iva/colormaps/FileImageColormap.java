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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * Loads a colormap from an image file
 * @author Martin Steiger
 */
public class FileImageColormap extends ImageBasedColormap implements KnownColormap
{
	private final String name;
	private final String desc;

	/**
	 * @param imageResource the (relative) path to the image
	 * @throws IOException if the image cannot be loaded
	 */
	public FileImageColormap(String imageResource) throws IOException
	{
		super(loadImageResource(imageResource));
		this.name = imageResource;
		this.desc = "Internal Image";
	}

	public FileImageColormap(File imageFile) throws IOException
	{
		super(ImageIO.read(imageFile));
		this.name = imageFile.getName();
		this.desc = imageFile.getCanonicalPath();
	}

	private static BufferedImage loadImageResource(String imagePath) throws IOException
	{
		URL imageUrl = FileImageColormap.class.getResource(imagePath);
		BufferedImage image = ImageIO.read(imageUrl);
		return image;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public String getDescription()
	{
		return desc;
	}

	@Override
	public ColorSpace getColorSpace()
	{
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences()
	{
		return Collections.emptyList();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(desc, name);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
	
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		FileImageColormap other = (FileImageColormap) obj;

		if (!Objects.equals(desc, other.desc))
			return false;
		
		if (!Objects.equals(name, other.name))
			return false;
		
		return true;
	}
}

