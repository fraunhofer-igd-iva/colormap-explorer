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

package de.fhg.igd.iva.colormaps;

import java.awt.Color;
import java.awt.geom.AffineTransform;


/**
 * Transforms a given colormap
 * @author Martin Steiger
 */
public class TransformedColormap extends DelegateColormap
{
	private double m00;
	private double m01;
	private double m02;
	private double m10;
	private double m11;
	private double m12;

	public static TransformedColormap identity(Colormap colormap)
	{
		return new TransformedColormap(colormap, 1, 0, 0, 1, 0, 0);
	}
	
	/**
	 * counter-clockwise
	 * @param colormap
	 * @return
	 */
	public static TransformedColormap rotated90(Colormap colormap)
	{
		return new TransformedColormap(colormap, 0, 1, -1, 0, 1, 0); 
	}
	
	public static TransformedColormap rotated180(Colormap colormap)
	{
		return new TransformedColormap(colormap, -1, 0, 0, -1, 1, 1); 
	}
	
	public static TransformedColormap rotated270(Colormap colormap)
	{
		return new TransformedColormap(colormap, 0, -1, 1, 0, 0, 1); 
	}
	
	public static TransformedColormap flippedX(Colormap colormap)
	{
		return new TransformedColormap(colormap, -1, 0, 0, 1, 1, 0); 
	}
	
	public static TransformedColormap flippedY(Colormap colormap)
	{
		return new TransformedColormap(colormap, 1, 0, 0, -1, 0, 1); 
	}
	
	private TransformedColormap(Colormap colormap, 
			double m00, double m10,
            double m01, double m11,
            double m02, double m12)
	{
		super(colormap);
	
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
	}
	
	@Override
	public Color getColor(double ox, double oy)
	{
		double x = ox * m00 + oy * m01 + m02;
		double y = ox * m10 + oy * m11 + m12;

		return getDelegate().getColor(x, y);
	}
	
	/**
	 * @return a <b>new</b> transformation 
	 */
	public AffineTransform getTransformation()
	{
		return new AffineTransform(m00, m10, m01, m11, m02, m12);
	}
}
