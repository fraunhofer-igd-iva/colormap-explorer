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


package de.fhg.igd.iva.explorer.plot;

import java.awt.Color;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

/**
 * Tests the {@link CoordinateConverters} implementations
 * @author Martin Steiger
 */
public class ConverterTest
{
	@Test
	public void testRgb()
	{
		CoordinateConverter converter = CoordinateConverters.createRgb();
		test(converter);
	}

	@Test
	public void testXyz()
	{
		CoordinateConverter converter = CoordinateConverters.createXyz();
		test(converter);
	}

	@Test
	public void testLab()
	{
		CoordinateConverter converter = CoordinateConverters.createLab();
		test(converter);
	}


	@Test
	public void testHsb()
	{
		CoordinateConverter converter = CoordinateConverters.createHsb();
		test(converter);
	}

	/**
	 * @param converter
	 */
	private void test(CoordinateConverter converter)
	{
		Function<Color, float[]> fromColor = converter.getColorToCubeCoordinates();
		Function<float[], float[]> toColor = converter.getCubeCoordinatesToRgbColorComponents();

		Random r = new Random(234234);

		for (int i = 0; i < 100; i++)
		{
			Color color = new Color(r.nextInt(), false);

			float[] vals = fromColor.apply(color);
			float[] rgb = toColor.apply(vals);

			testEquals(color, new Color(rgb[0], rgb[1], rgb[2]));
		}
	}

	/**
	 * @param color
	 * @param color2
	 */
	private void testEquals(Color color, Color color2)
	{
		Assert.assertEquals(color, color2);

//		int thres = 0;
//		if (Math.abs(color.getRed() - color2.getRed()) > thres)
//			Assert.fail("Red difference too large - " + color + " to " + color2);
//
//		if (Math.abs(color.getGreen() - color2.getGreen()) > thres)
//			Assert.fail("Green difference too large - " + color + " to " + color2);
//
//		if (Math.abs(color.getBlue() - color2.getBlue()) > thres)
//			Assert.fail("Blue difference too large - " + color + " to " + color2);
	}
}
