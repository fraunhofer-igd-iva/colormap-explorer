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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import colormaps.ColorSpace;
import colormaps.FileImageColormap;

/**
 * Steiger et al. (2014): CIELab cyan
 * @author Martin Steiger
 */
public class Steiger14 extends FileImageColormap
{

	/**
	 * @throws IOException if the image cannot be loaded
	 */
	public Steiger14() throws IOException
	{
		super("/images/steiger_lab_tuerkis.png");
	}

	@Override
	public String getName()
	{
		return "Steiger et al. (2014): CIELab cyan";
	}

	@Override
	public String getDescription()
	{
		return "CIELab-based colormap with perceptually (almost) equi-distant corners";
	}

	@Override
	public ColorSpace getColorSpace()
	{
		return ColorSpace.CIE_Lab;
	}

	@Override
	public List<String> getReferences()
	{
		return Arrays.asList("steiger2014");
	}
}
