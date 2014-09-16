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

import java.util.Arrays;
import java.util.List;

import de.fhg.igd.iva.colormaps.ColorSpace;

public class BremmCIELabStretched extends GuoLabStyle {
	public BremmCIELabStretched() {
		super(a -> 55d, linearBa(-10, 40, -20, 35));
		java.util.function.Function<Double, Double> f = ((Double a) -> a);
	}

	@Override
	public String getName() {
		return "Bremm et al. (stretched)";
	}

	@Override
	public String getDescription() {
		return "Bremm et al. 2011 Fig. 9b: Numbers are guessed and still seem slighly off.";
	}

	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.CIE_Lab;
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList("Bremm2011");
	}
}
