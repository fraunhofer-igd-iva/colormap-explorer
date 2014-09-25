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
import java.awt.Graphics2D;
import java.math.RoundingMode;

import javax.swing.JComponent;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.quality.ColormapQuality;

import com.google.common.collect.Range;
import com.google.common.math.DoubleMath;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class JStatBar extends JComponent
{
	private static final long serialVersionUID = 259663603963750291L;
	private ColormapQuality metric;
	private double quality;
	private DescriptiveStatistics stats;
	private int insetX = 2;
	private int insetY = 2;

	/**
	 * @param metric
	 * @param range
	 * @param descriptiveStatistics
	 * @param double1
	 */
	public JStatBar(ColormapQuality metric, DescriptiveStatistics stats, double quality)
	{
		this.metric = metric;
		this.stats = stats;
		this.quality = quality;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(80, 19);
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);

		Graphics2D g = (Graphics2D) g1;

		int width = getWidth() - 1;
		int height = getHeight() - 1;

		int whiskerSize = 6;

		// fill background rect
		g.setColor(Color.WHITE);
		g.fillRect(insetX, insetY, width - 2 * insetX, height - 2 * insetY);

		int q10X = mapXToScreen(stats.getPercentile(10.0), width);
		int q25X = mapXToScreen(stats.getPercentile(25.0), width);
		int q50X = mapXToScreen(stats.getPercentile(50.0), width);
		int q75X = mapXToScreen(stats.getPercentile(75.0), width);
		int q90X = mapXToScreen(stats.getPercentile(90.0), width);

		g.setColor(Color.PINK);
		int leftX = Math.min(q25X, q75X);
		int rightX = Math.max(q25X, q75X);
		
		g.fillRect(insetX + leftX, insetY+1, rightX - leftX, height - 2 * insetY-1);
		g.drawLine(insetX + q10X, height / 2, insetX + q90X, height / 2);
		g.drawLine(insetX + q10X, (height - whiskerSize) / 2 , insetX + q10X, (height + whiskerSize) / 2);
		g.drawLine(insetX + q10X, height / 2, insetX + q90X, height / 2);
		g.drawLine(insetX + q90X, (height - whiskerSize) / 2 , insetX + q90X, (height + whiskerSize) / 2);

		g.setColor(Color.RED);
		g.drawLine(insetX + q50X, insetY+1, insetX + q50X, height - insetY-1);

		// draw outline border rect
		g.setColor(Color.BLACK);
		g.drawRect(insetX, insetY, width - 2 * insetX, height - 2 * insetY);

		int buttonX = mapXToScreen(quality, width);
		drawButton(g, buttonX, height);
	}

	private void drawButton(Graphics2D g, int dx, int height)
	{
		int barWidth = 4;
		g.setColor(Color.GRAY);
		g.fillRect(insetX + dx - barWidth / 2, 0, barWidth, height);
		g.draw3DRect(insetX + dx - barWidth / 2, 0, barWidth, height, true);
	}

	private int mapXToScreen(double val, int width)
	{
		double min = stats.getMin();
		double max = stats.getMax();
		
		int barWidth = width - 2 * insetX;
		
		int value = DoubleMath.roundToInt(barWidth * (val - min) / (max - min), RoundingMode.HALF_UP);

		if (!metric.moreIsBetter())
			value = barWidth - value;	// maybe we have to subtract 1px here
		
		return value;
	}
}
