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

package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;

import de.fhg.igd.iva.colormaps.ColorSpace;

/**
 * A colormap with four color anchors in the edges: Red, Blue, Green, and Yellow
 * @author Martin Steiger
 */
public class FourCornersAnchorRBGY extends FourCornersAnchorColorMapParameterizable {

	public FourCornersAnchorRBGY()
	{
		super(Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE);
	}

	@Override
	public String getName() {
		return "Four Corners R-B-G-Y";
	}

	@Override
	public String getDescription() {
		return "RGB colormap with four color anchors in the edges: Red, Blue, Green, and Yellow.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}
}
