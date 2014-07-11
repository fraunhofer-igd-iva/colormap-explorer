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
package latex;

import java.awt.Color;

/**
 * Defines a color in the LaTeX output
 * @author Martin Steiger
 */
public class LatexColor
{
	private String name;
	private Color color;

	public LatexColor(Color color, String name)
	{
		this.name = name;
		this.color = color;
	}

	public String getName()
	{
		return name;
	}

	public double getR()
	{
		return color.getRed() / 255.0;
	}

	public double getG()
	{
		return color.getGreen() / 255.0;
	}

	public double getB()
	{
		return color.getBlue() / 255.0;
	}
}
