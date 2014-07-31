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

import colormaps.AbstractColormap2D;
import colormaps.ColorSpace;
import colormaps.Colormap2D;

/**
 * Transforms a given colormap
 * @author Martin Steiger
 */
public abstract class TransformedColormap2D extends AbstractColormap2D {

	private Colormap2D colormap;

	protected TransformedColormap2D(Colormap2D colormap)
	{
		this.colormap = colormap;
	}
	
	public Colormap2D getColormap() {
		return colormap;
	}

	/**
	 * @param colormap the colormap to set
	 */
	public void setColormap(Colormap2D colormap) {
		this.colormap = colormap;
	}
	
	@Override
	public ColorSpace getColorSpace() {
		return ColorSpace.NONE;
	}

}
