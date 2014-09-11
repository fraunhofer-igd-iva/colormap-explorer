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


package de.fhg.igd.iva.explorer.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.math.RoundingMode;

import javax.swing.JComponent;

import com.google.common.collect.Range;
import com.google.common.math.DoubleMath;

import algorithms.quality.ColormapQuality;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class JStatBar extends JComponent
{
	private static final long serialVersionUID = 259663603963750291L;
	private ColormapQuality metric;
	private double quality;
	private Range<Double> range;

	/**
	 * @param metric
	 * @param range
	 * @param double1
	 */
	public JStatBar(ColormapQuality metric, Range<Double> range, double quality)
	{
		this.metric = metric;
		this.range = range;
		this.quality = quality;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(80, 15);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		int width = getWidth() - 1;
		int height = getHeight() - 1;

		int insetX = 2;
		int insetY = 2;

		g.setColor(Color.WHITE);
		g.fillRect(insetX, insetY, width - 2 * insetX, height - 2 * insetY);

		g.setColor(Color.BLACK);
		g.drawRect(insetX, insetY, width - 2 * insetX, height - 2 * insetY);

		double min = range.lowerEndpoint().doubleValue();		// assume that the range is closed
		double max = range.upperEndpoint().doubleValue();		// assume that the range is closed

		int dx = DoubleMath.roundToInt((width - 2 * insetX) * (quality - min) / (max - min), RoundingMode.HALF_UP);
		int barWidth = 4;
		g.drawRect(insetX + dx - barWidth / 2, 0, barWidth, height);
		g.setColor(Color.GRAY);
		g.fillRect(insetX + dx - barWidth / 2, 0, barWidth, height);
	}
}
