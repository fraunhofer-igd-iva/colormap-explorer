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

/**
 * A colormap with a constant gray value of 0.5
 * @author Martin Steiger
 */
public class ConstantColormap2D extends AbstractColormap2D
{
	private final Color color;

	/**
	 * Default constructor using 50% on all channels
	 */
	public ConstantColormap2D()
	{
		color = new Color(0.5f, 0.5f, 0.5f);
	}

	/**
	 * @param color the constant color to use
	 */
	public ConstantColormap2D(Color color)
	{
		this.color = color;
	}

	@Override
	public Color getColor(float x, float y) {
		return color;
	}

	@Override
	public String getName()
	{
		return "Constant Gray 0.5";
	}

	@Override
	public String getDescription()
	{
		return getName();
	}

	@Override
	public ColorSpace getColorSpace()
	{
		return ColorSpace.CONSTANT_GRAY;
	}
}
