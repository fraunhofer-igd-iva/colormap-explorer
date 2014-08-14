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

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * Displays colored points on a given background
 * @author Martin Steiger
 */
public class PointsExamplePanel extends JPanel implements ColormapPanel
{
	private static final long serialVersionUID = 4842610449905121603L;

	private Colormap colormap;

	private int alpha;
	
	/**
	 * @param colormap the colormap for the points
	 */
	public PointsExamplePanel(Colormap colormap)
	{
		this.colormap = colormap;
	}

	@Override
	public void setColormap(Colormap colormap)
	{
		this.colormap = colormap;
		repaint();
	}
	
	/**
	 * @param alpha the transparency of the points
	 */
	public void setAlpha(int alpha)
	{
		this.alpha = alpha;
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		Random r = new Random(123456);
		int count = 100;
		int dia = 15;

		double maxX = getWidth() - dia;
		double maxY = getHeight() - dia;

		Object oldAAhint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < count; i++)
		{
			int x = (int) (r.nextDouble() * maxX);
			int y = (int) (r.nextDouble() * maxY);
			
			float cx = r.nextFloat();
			float cy = r.nextFloat();
			
			Color color = colormap.getColor(cx, cy);
			
			if (color.getAlpha() != alpha)
			{
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
			}
			
			g.setColor(color);
			g.fillOval(x, y, dia, dia);
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAAhint);
	}

}
