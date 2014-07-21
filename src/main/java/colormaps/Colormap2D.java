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

import java.awt.Color;
import java.util.List;

/**
 * Defines a color map in 2D space
 * @author Martin Steiger
 */
public interface Colormap2D 
{
	/**
	 * @param x the x coordinate in the range [0..1]
	 * @param y the x coordinate in the range [0..1]
	 * @return the 
	 */
	Color getColor(double x, double y);

	/**
	 * @return the short name of the color map
	 */
	String getName();

	/**
	 * @return the full description
	 */
	String getDescription();

	/**
	 * @return the color space
	 */
	ColorSpace getColorSpace();

	/**
	 * @return a list of full BibTeX entries
	 */
	List<String> getReferences();
}
