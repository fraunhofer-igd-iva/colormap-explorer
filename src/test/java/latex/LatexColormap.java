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

import java.util.Collections;
import java.util.List;

import colormaps.Colormap2D;

import com.google.common.collect.Lists;

/**
 * Defines a colormap in the LaTeX output
 * @author Martin Steiger
 */
public class LatexColormap
{
	private Colormap2D colormap;
	private final List<String> images = Lists.newArrayList();

	public LatexColormap(Colormap2D cm)
	{
		this.colormap = cm;
	}

	public String getName()
	{
		return colormap.getName();
	}

	public String getDesc()
	{
		return colormap.getDescription().replaceAll("\\&", "\\\\&");
	}

	public String getColorspace()
	{
		return colormap.getColorSpace().toString().replaceAll("\\_", "\\\\_");
	}

	public void addImage(String imgFile)
	{
		images.add(imgFile);
	}

	public List<String> getImages()
	{
		return Collections.unmodifiableList(images);
	}

	public List<String> getRefs()
	{
		return Collections.emptyList();
//		return Collections.unmodifiableList(colormap.getReferences());
	}
}
