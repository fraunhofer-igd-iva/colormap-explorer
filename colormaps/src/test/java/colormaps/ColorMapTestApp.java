// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package colormaps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.impl.FourCornersAnchorRBGY;
import de.fhg.igd.iva.colormaps.impl.Ziegler;

/**
 * Opens a small JFrame window that draws a colormap
 * @author Martin Steiger
 */
public class ColorMapTestApp
{
	/**
	 * @param args ignored
	 */
	public static void main(String[] args)
	{
		final JFrame frame = new JFrame("Colormap Testing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent comp = new JComponent()
        {
			private static final long serialVersionUID = -5658146923830045505L;

			@Override
			public void paint(Graphics g)
			{
//				RGBColorMap2DKonstanzLike cm = new RGBColorMap2DKonstanzLike();
//				Colormap cm = new Ziegler();
				Colormap cm = new FourCornersAnchorRBGY();
	
				double width = this.getWidth();
				double height = this.getHeight();
	
				int bs = 2;
				
				for (int x = bs / 2; x < width; x += bs)
				{
					for (int y = bs / 2; y < height; y += bs)
					{
						Color c = cm.getColor((float)(x / width), (float)(y / height));
						g.setColor(c);
						g.fillRect(x - bs / 2, y - bs / 2, bs, bs);
					}
				}
			}
		};
		
		comp.setPreferredSize(new Dimension(512, 512));
			
		frame.add(comp);
        
		// repaint continuously to reflect code changes ^^
        Timer t = new Timer(500, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.invalidate();
				frame.repaint();
				System.out.println("REPAINT");
			}
		});
		
        t.start();
        frame.pack();
		frame.setVisible(true);
	}
	
}
