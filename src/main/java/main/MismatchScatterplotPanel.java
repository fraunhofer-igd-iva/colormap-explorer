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
	private static final long serialVersionUID = 4842610449905121603L;

	private static ViewingConditions comparisonVc = ViewingConditions.sRGB_typical_envirnonment;

	private Colormap2D colormap;

	private int points = 10000;
	
	private boolean useLog;
	
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
	 * @param numPoints the number of points
	 */
	public void setPointNumber(int numPoints)
	{
		this.points = numPoints;
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		Random r = new Random(123456);
		int dia = 3;

		double maxX = getWidth() - dia;
		double maxY = getHeight() - dia;

		Object oldAAhint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < points; i++)
		{
			float ax = r.nextFloat();
			float ay = r.nextFloat();
			
			float bx = r.nextFloat();
			float by = r.nextFloat();
			
			// distance on color map - 0 to 1
			double mapdistance = Math.hypot(bx - ax, by - ay) / Math.sqrt(2.0);
			
			Color colorA = colormap.getColor(ax, ay);
			Color colorB = colormap.getColor(bx, by);
			
			// roughly 0-100
			double cdist = colorDiff(colorA, colorB) / 120f;
			
			g.setColor(new Color(0, 0, 0, 0.1f));
			int xCoord = (int)(maxX * mapdistance);
			int yCoord;
			if (useLog)
				yCoord = (int)((Math.log(cdist / mapdistance)/3)*maxY + (maxY/2));
			else
				yCoord = (int)(maxY * cdist);
			g.fillOval(xCoord, yCoord, dia, dia);
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAAhint);
	}
	
	private PColor convert(Color color) {
		return PColor.create(CS_sRGB.instance, color.getColorComponents(new float[3]));
	}
	
	private double colorDiff(Color c1, Color c2) {
		return ColorTools.distance(convert(c1), convert(c2), comparisonVc);
	}

}
