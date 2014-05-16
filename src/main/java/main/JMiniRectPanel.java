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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import tiling.RectTileModel;
import tiling.Tile;
import colormaps.Colormap2D;

import com.google.common.base.Optional;

/**
 * Displays the colormap based on rectangles
 * @author Martin Steiger
 */
public class JMiniRectPanel extends JPanel
{
	private static final long serialVersionUID = 6582370458976011408L;

	private Colormap2D colormap;
	private RectTileModel tileModel;
	private Optional<Tile> selection = Optional.absent();

	/**
	 * @param colormap the colormap to show
	 * @param cells the number of cells
	 */
	public JMiniRectPanel(Colormap2D colormap, int cells)
	{
		this.colormap = colormap;
		this.tileModel = new RectTileModel(20, 20, cells, cells);
		
		addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				Optional<Tile> newSel = tileModel.getTileAtWorldPos(e.getX(), e.getY());
				if (!selection.equals(newSel))
				{
					selection = newSel;
					repaint();
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mouseMoved(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				selection = Optional.absent();
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		
		int mapWidth = tileModel.getMapWidth();
		int mapHeight = tileModel.getMapHeight();
		int tileWidth = tileModel.getTileWidth();
		int tileHeight = tileModel.getTileHeight();

		Graphics2D g = (Graphics2D)g1;
//		double sx = getWidth() / (float)mapWidth;
//		double sy = getHeight() / (float)mapHeight;
//		g.scale(sx, sy);
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int worldX = tileModel.getWorldX(x, y);
				int worldY = tileModel.getWorldY(x, y);
				
				float relX = (x + 0.5f) / tileModel.getMapWidth();
				float relY = (y + 0.5f) / tileModel.getMapHeight();

				Color color = colormap.getColor(relX, relY);
				g.setColor(color);
				g.fillRect(worldX, worldY, tileWidth, tileHeight);
			}
		}
		
		if (selection.isPresent())
		{
			Tile tile = selection.get();
			int x = tile.getMapX();
			int y = tile.getMapY();
			int worldX = tileModel.getWorldX(x, y);
			int worldY = tileModel.getWorldY(x, y);
			
			g.setColor(Color.BLACK);
			g.drawRect(worldX, worldY, tileWidth, tileHeight);
		}
	}
}
