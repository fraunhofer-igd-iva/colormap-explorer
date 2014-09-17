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

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import de.fhg.igd.iva.colormaps.CachedColormap;
import de.fhg.igd.iva.colormaps.Colormap;

/**
 * A panel that draw a square-shaped colormap
 * @author Martin Steiger
 */
public class ColormapPanel extends JPanel
{
	private static final long serialVersionUID = 240761518096949199L;

	private CachedColormap colormap;

	private final int size;

	/**
	 * Uses a 512x512 image for caching
	 * @param constantColormap
	 */
	public ColormapPanel(Colormap colormap)
	{
		this(colormap, 512);
	}

	/**
	 * @param colormap the colormap to draw
	 * @param size the size of the cache image
	 */
	public ColormapPanel(Colormap colormap, int size)
	{
		this.size = size;
		this.colormap = new CachedColormap(colormap, size, size);
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		int screenSize = Math.min(getWidth(), getHeight());

		Graphics2D g = (Graphics2D) g1;
		g.drawImage(colormap.getImage(), 0, 0, screenSize, screenSize, null);
	}

	public CachedColormap getColormap()
	{
		return colormap;
	}

	public void setColormap(Colormap colormap)
	{
		this.colormap = new CachedColormap(colormap, size, size);
		repaint();
	}

}
