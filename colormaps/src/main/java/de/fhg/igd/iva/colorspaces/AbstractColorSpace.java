/*
 * Copyright (c) 2014, University of Konstanz
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
package de.fhg.igd.iva.colorspaces;

import java.awt.Color;

/**
 * @author Sebastian Mittelstaedt
 */
public abstract class AbstractColorSpace {

	public abstract double[] toRGB(double[] v);
	public abstract Color toColor(double[] v, boolean returnBlackForUndefinedRGB);
	public abstract double[] fromRGB(double[] rgb);
	public abstract double[] fromColor(Color c);

	protected static void fixRoundingErrors(double[] rgb)
	{
		final double eps = 0.01;

		for (int i = 0; i < rgb.length; i++)
		{
			double v = rgb[i];
			if (v > 1 && v < 1 + eps)
				rgb[i] = 1;

			if (v < 0 && v > -eps)
				rgb[i] = 0;
		}
	}

}
