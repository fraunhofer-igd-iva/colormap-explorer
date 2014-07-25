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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		return colormap.getColorSpace().toString().replaceAll("\\_", "");
	}

	public void addImage(String imgFile)
	{
		images.add(imgFile);
	}

	public List<String> getImages()
	{
		return Collections.unmodifiableList(images);
	}

	public List<String> getShortRefs()
	{
		return colormap.getReferences();
//		List<String> refs = Lists.newArrayList();
//		
//		// we match "@" followed by 1+ alphanumeric chars, "{" a 
//		// group with 1+ alphanumeric chars, "," and anything that follows  
//		// e.g. @inproceedings{himberg1998enhancing,
//		Pattern refPattern = Pattern.compile("@\\w+\\{(\\w+),.*");
//		
//		for (String bibtex : colormap.getReferences())
//		{
//			Matcher match = refPattern.matcher(bibtex);
//			if (match.matches())
//			{
//				String shortRef = match.group(1);
//				refs.add(shortRef);
//			}
//			else
//			{
//				System.out.println("Warning: Could not match BibTeX entry: " + bibtex);
//			}
//		}
//		
//		return refs;
	}
}
