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

package algorithms.quality;

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public interface ColormapQuality 
{
	/**
	 * @param colormap2d the colormap to use
	 * @return the quality in [0..1]
	 */
	double getQuality(Colormap colormap2d);

	/**
	 * @return true if higher quality score is "better", false if "lower" quality score is better
	 */
	boolean moreIsBetter();
	
	/**
	 * @return the name (id)
	 */
	String getName();

	/**
	 * @return a proper description of what it does
	 */
	String getDescription();
}
