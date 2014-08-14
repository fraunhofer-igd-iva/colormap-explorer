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
import java.util.Collections;
import java.util.List;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;

/**
 * <p>
 * Title: BCR37
 * </p>
 * 
 * <p>
 * Description: taken from Poster
 * "Cross-modal Sound-to-Sight Associations with Musical Timbre in Non-Synesthetes"
 * - William S. Griscom and Stephen E. Palmer - Department of Psychology,
 * University of California, Berkeley
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * @author Jürgen Bernard
 * 
 */

public class BCP37 extends AbstractColormap {

	@Override
	public Color getColor(double x, double y) {
		checkRanges(x, y);

		// red
		float r = (float) (124.6f + 91.8f * x - 47.53f * y);

		// green
		float g = (float) (98.8f + 77.73f * x + 23.066f * y);

		// blue
		float b = (float) (145.9f - 36.3f * x + 12.7f * y);

		return new Color(r / 255f, g / 255f, b / 255f);
	}

	@Override
	public String getName() {
		return "BCP 37";
	}

	@Override
	public String getDescription() {
		return "CIELab colormap with colors from the BCP 37 (Berkeley Color Project). Implementation based on a sRGB regression model.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences() {
		return Collections.singletonList("griscom2013violins");
	}
}
