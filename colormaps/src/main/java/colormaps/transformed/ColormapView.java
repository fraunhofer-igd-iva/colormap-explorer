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

package colormaps.transformed;

import java.awt.Color;

/**
 * A view on a colormap
 * @author Martin Steiger
 */
public interface ColormapView
{
	/**
	 * @param mx the x coordinate in the range [0..1]
	 * @param my the x coordinate in the range [0..1]
	 * @return the color value at [mx, my] 
	 */
	Color getColor(double mx, double my);

	/**
	 * @param mx the x coordinate in the range [0..1]
	 * @param my the x coordinate in the range [0..1]
	 * @return the reliability of the view [0..1]
	 */
	double getReliability(double mx, double my);

	/**
	 * @return a human-readable description of the view
	 */
	String getDescription();
}
