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
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import algorithms.MedianDivergenceComputer;
import colormaps.Colormap2D;

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

	private static final Color HELPER_LINE_COLOR = new Color(0, 0, 0, 0.5f);

	private static final Color BORDERLINE_DOT_COLOR = new Color(1,0,0,0.5f);
	
	private static final long serialVersionUID = 4842610449905121603L;

	private Colormap2D colormap;

	private int lines;
	
	private boolean useLog;

	private List<Point2D> points;

	private MedianDivergenceComputer ratioStats;
	
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
	public void setPointSource(List<Point2D> newPoints, int num, MedianDivergenceComputer stats)
	{
		this.lines = num;
		this.points = newPoints;
		this.ratioStats = stats;
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
		
		Iterator<Point2D> pstr = points.iterator();
		
		double medianRatio = ratioStats.getQuantile(0.5);

		for (int i = 0; i < lines; i++)
		{
			Point2D p1 = pstr.next();
			Point2D p2 = pstr.next();
	
			float dist = (float) p1.distance(p2);
			
			Color colorA = colormap.getColor(p1.getX(), p1.getY());
			Color colorB = colormap.getColor(p2.getX(), p2.getY());
			
			// roughly 0-100
			double cdist = MedianDivergenceComputer.colorDiff(colorA, colorB);
			
			// make less relevant dots red
			if (cdist < 1.0 || dist == 0 || dist > 1.0f)
				g.setColor(BORDERLINE_DOT_COLOR);
			else
				g.setColor(NORMAL_DOT_COLOR);
			
			// normalize
			cdist /= medianRatio;
			
			int xCoord = (int)(maxX * dist);
			int yCoord = getYPos(maxY, dist, cdist);
			g.fillOval(xCoord, yCoord, dia, dia);
		}
		
		double upperQuantile = ratioStats.getQuantile(0.95);
		double lowerQuantile = ratioStats.getQuantile(0.05);
		
		
		if (useLog) {
			g.setColor(MEDIAN_LINE_COLOR);
			int yc = getYPos(maxY, 1, 1);
			g.drawLine(0, yc, (int)maxX, yc);

			g.setColor(HELPER_LINE_COLOR);
			yc = getYPos(maxY, 1, lowerQuantile/medianRatio);
			g.drawLine(0, yc, (int)maxX, yc);
			yc = getYPos(maxY, 1, upperQuantile/medianRatio);
			g.drawLine(0, yc, (int)maxX, yc);
		} else {
			g.setColor(MEDIAN_LINE_COLOR);
			int yc = getYPos(maxY, 1, 1);
			g.drawLine(0, (int)maxY, (int)maxX, yc);
			
			g.setColor(HELPER_LINE_COLOR);
			yc = getYPos(maxY, 1, lowerQuantile/medianRatio);
			g.drawLine(0, (int)maxY, (int)maxX, yc);
			yc = getYPos(maxY, 1, upperQuantile/medianRatio);
			g.drawLine(0, (int)maxY, (int)maxX, yc);
			
			String text = String.format("Median ratio delta E to colormap distance: %.1f, Lower: %.1f Upper: %.1f", 
					medianRatio, lowerQuantile, upperQuantile);
			
			g.drawString(text, 50, 50);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAAhint);
	}

	private int getYPos(double maxY, float dist, double cdist) {
		int yCoord;
		if (useLog)
			yCoord = (int)((maxY/2) - (Math.log(cdist / dist)/Math.log(6))*maxY);
		else
			yCoord = (int)(maxY - (maxY * cdist * 2 / 3));
		return yCoord;
	}

}
