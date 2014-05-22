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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Random;

import javax.swing.JPanel;

import colormaps.Colormap2D;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class OverlayExamplePanel extends JPanel
{
	private static final long serialVersionUID = 4842610449905121603L;

	private Colormap2D colormap;

	private Color overlayColor;

	private int fontSize;
	
	/**
	 * @param colormap
	 */
	public OverlayExamplePanel(Colormap2D colormap)
	{
		this.colormap = colormap;
	}

	/**
	 * @param colormap
	 */
	public void setColormap(Colormap2D colormap)
	{
		this.colormap = colormap;
		repaint();
	}
	
	public void setOverlayColor(Color color)
	{
		this.overlayColor = color;
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		Font derivedFont = g.getFont().deriveFont(Font.PLAIN, (float)fontSize);
		
		Random r = new Random(123456);
		int gridX = 10;
		int gridY = 10;

		for (int y = 0; y < getHeight(); y += gridY)
		{
			for (int x = 0; x < getWidth(); x += gridX)
			{
				float cx = r.nextFloat();
				float cy = r.nextFloat();
			
				Color color = colormap.getColor(cx, cy);
			
				g.setColor(color);
				g.fillRect(x, y, gridX, gridY);
			}
		}

		FontRenderContext frc = g.getFontRenderContext();

		String text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
				+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo "
				+ "dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. ";

		AttributedString attributedString = new AttributedString(text);
		attributedString.addAttribute(TextAttribute.FONT, derivedFont);
		AttributedCharacterIterator iterator = attributedString.getIterator();
		LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);

		g.setColor(overlayColor);

		float tx = 0;
        float ty = 0;
        while (measurer.getPosition() < iterator.getEndIndex())
        {
            TextLayout layout = measurer.nextLayout(getWidth());

            ty += layout.getAscent();
            float dx = 0;

            layout.draw(g, tx + dx, ty);
            ty += layout.getDescent() + layout.getLeading();
        }
	}

	/**
	 * @param fontSize
	 */
	public void setFontSize(int fontSize)
	{
		this.fontSize = fontSize;
	}


}
