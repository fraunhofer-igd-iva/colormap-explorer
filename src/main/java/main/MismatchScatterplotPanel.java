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

package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import colormaps.Colormap2D;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Displays a scatterplot of perceptual color distance over color map distance.
 * @author Simon Thum
 * 	who shamelessly used as a template code from
 * @author Martin Steiger
 */
public class MismatchScatterplotPanel extends JPanel implements ColormapPanel
{
	private static final Color NORMAL_DOT_COLOR = new Color(0, 0, 0, 0.1f);

	private static final Color MEDIAN_LINE_COLOR = new Color(0, 0, 0, 0.8f);

	private static final Color BORDERLINE_DOT_COLOR = new Color(1,0,0,0.5f);
	

	private static final long serialVersionUID = 4842610449905121603L;

	private static ViewingConditions comparisonVc = ViewingConditions.sRGB_typical_envirnonment;

	private Colormap2D colormap;

	private int lines = 0;
	
	private boolean useLog;

	private List<Float> floats;

	private double medianRatio;
	
	/**
	 * @param colormap the colormap for the points
	 * @param useLog use logarithmic scale
	 */
	public MismatchScatterplotPanel(Colormap2D colormap, boolean useLog)
	{
		this.colormap = colormap;
		this.useLog = useLog;
	}

	/**
	 * @param colormap the colormap for the points
	 */
	@Override
	public void setColormap(Colormap2D colormap)
	{
		this.colormap = colormap;
		repaint();
	}
	
	/**
	 * @param num the number of points
	 */
	public void setPointSource(List<Float> floats, int num, double medianRatio)
	{
		this.lines = num;
		this.floats = floats;
		this.medianRatio = medianRatio;
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		int dia = 3;

		double maxX = getWidth() - dia;
		double maxY = getHeight() - dia;

		Object oldAAhint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Iterator<Float> flt = floats.iterator();

		for (int i = 0; i < lines; i++)
		{
			float ax = flt.next();
			float ay = flt.next();
			float bx = flt.next();
			float by = flt.next();
			float dist = (float) Math.hypot(ax-bx, ay-by);
			
			
			Color colorA = colormap.getColor(ax, ay);
			Color colorB = colormap.getColor(bx, by);
			
			// roughly 0-100
			double cdist = colorDiff(colorA, colorB);
			
			// make less relevant dots red
			if (cdist < 1.0 || dist == 0 || dist > 1.0f)
				g.setColor(BORDERLINE_DOT_COLOR);
			else
				g.setColor(NORMAL_DOT_COLOR);
			
			// normalize
			cdist /= medianRatio;
			
			int xCoord = (int)(maxX * dist);
			int yCoord;
			if (useLog)
				yCoord = (int)((Math.log(cdist / dist)/Math.log(2))*maxY + (maxY/2));
			else
				yCoord = (int)(maxY * cdist * 2 / 3);
			g.fillOval(xCoord, yCoord, dia, dia);
		}
		
		
		if (useLog) {
			g.setColor(NORMAL_DOT_COLOR);
			int yc = (int)((Math.log(2)/Math.log(2))*maxY + (maxY/2));
			g.drawLine(0, yc, (int)maxX, yc);
			yc = (int)((Math.log(1/2f)/Math.log(2))*maxY + (maxY/2));
			g.drawLine(0, yc, (int)maxX, yc);
			g.setColor(MEDIAN_LINE_COLOR);
			yc = (int)(maxY/2);
			g.drawLine(0, yc, (int)maxX, yc);
		} else {
			g.setColor(MEDIAN_LINE_COLOR);
			g.drawLine(0, 0, (int)maxX, (int)(maxY * 2 / 3));
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAAhint);
	}
	
	public static PColor convert(Color color) {
		return PColor.create(CS_sRGB.instance, color.getColorComponents(new float[3]));
	}
	
	public static double colorDiff(Color c1, Color c2) {
		return ColorTools.distance(convert(c1), convert(c2), comparisonVc);
	}
	


}
