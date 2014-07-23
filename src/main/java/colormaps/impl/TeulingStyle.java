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

import java.util.Collections;
import java.util.List;

import tiling.Direction;
import colormaps.AbstractColormap2D;

/**
 * Helper class for implementing Teuling-style color maps. These are built from RGB planes
 * and some whitening on top.
 * @author simon
 */
public abstract class TeulingStyle extends AbstractColormap2D
{
	
	
	private static final double WK_MAX_HYPOT = Math.hypot(0.5, 0.5);

	/**
	 * Get a single channel according to the paper's method of
	 * specifying planes.
	 * @param xd distance in x from the channel's maximum corner
	 * @param yd distance in y from the channel's maximum corner
	 * @param a the a coefficient (0-1), the drop from maximum along the WEST-EAST (x) axis
	 * @return
	 */
	float getPlane(float xd, float yd, float a) {
		xd = 1 - xd;
		yd = 1 - yd;
		return (float) (1.0 - xd * a - yd * (1-a));
	}
	
	float getChannel(float x, float y, Direction d, float a) {
		float xd;
		float yd;
		switch (d) {
		case SOUTH_WEST:
			xd = 1-x;
			yd = y;
			break;
		case SOUTH_EAST:
			xd = x;
			yd = y;
			break;
		case NORTH_WEST:
			xd = 1-x;
			yd = 1-y;
			break;
		case NORTH_EAST:
			xd = x;
			yd = 1-y;
			break;
		default:
			throw new IllegalArgumentException("direction not supported");
		}
		
		return getPlane(xd, yd, a);
	}
	
	/**
	 * The whitening kernel in teuling has some max (w) and drops to zero in the
	 * 4 corners.
	 * @param x the x coordnainte
	 * @param y the y coordinate
	 * @param w the maximum value
	 * @return the whitening at x, y for w
	 */
	float getWhitening(float x ,float y, float w) {
		return (float) (w * (1-(Math.hypot(x - 0.5, y - 0.5) / WK_MAX_HYPOT)));
	}
	
	@Override
	public colormaps.ColorSpace getColorSpace() {
		return colormaps.ColorSpace.sRGB;
	}

	@Override
	public List<String> getReferences()
	{
		return Collections.singletonList("teuling_aj_2011");
	}

}
