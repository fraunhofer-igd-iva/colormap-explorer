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
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tiling.Direction;
import tiling.HexTileModel;
import tiling.Tile;
import colormaps.Colormap2D;

import com.google.common.base.Optional;
import com.google.common.math.IntMath;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

import events.MyEventBus;
import events.TileSelectionEvent;

/**
 * Displays the colormap based on rectangles
 * @author Martin Steiger
 */
public class JMiniHexPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(JMiniHexPanel.class);
	
	private static final long serialVersionUID = 6582370458976011408L;

	private Colormap2D colormap;
	private HexTileModel tileModel;
	private Optional<Tile> selection = Optional.absent();
	private final Polygon hexagon;

	/**
	 * @param colormap the colormap to show
	 */
	public JMiniHexPanel(Colormap2D colormap)
	{
		this.colormap = colormap;
		int cells = 10;
		
		// the rounding error is < 0.1 pixel for widths: 22 / 30 / 38 / 52
		int width = 32;
		int topLen = width / 2;
		double hf = 0.5 + width * Math.sqrt(3) * 0.5;
		int height = (int) hf;

		this.tileModel = new HexTileModel(width, height, topLen, cells, cells);
		
		addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				Optional<Tile> newSel = tileModel.getTileAtWorldPos(e.getX(), e.getY());
				updateTileSelection(newSel);
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mouseMoved(e);
			}
		});
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				updateTileSelection(Optional.<Tile>absent());
			}
		});
		
		hexagon = new Polygon();
		int tl = tileModel.getTopLength();
		int w = tileModel.getTileWidth();
		int h = tileModel.getTileHeight();
		
		hexagon.addPoint(- tl / 2, -h / 2);
		hexagon.addPoint(tl / 2, -h / 2);
		hexagon.addPoint(w / 2, 0);
		hexagon.addPoint(tl / 2, h / 2);
		hexagon.addPoint(- tl / 2, h / 2);
		hexagon.addPoint(-w / 2, 0);
	}

	protected void updateTileSelection(Optional<Tile> newSel)
	{
		if (selection.equals(newSel))
			return;
		
		selection = newSel;
		
		Set<Tile> tiles;
		if (selection.isPresent())
			tiles = Collections.singleton(selection.get()); else
			tiles = Collections.emptySet();
		
		TileSelectionEvent event = new TileSelectionEvent(tiles, JMiniHexPanel.this.colormap, tileModel);
		MyEventBus.getInstance().post(event);
		
		logger.debug("Selected tiles: " + tiles);
		
		repaint();
	}

	/**
	 * @param colorMap the new colormap
	 */
	public void setColorMap(Colormap2D colorMap)
	{
		this.colormap = colorMap;
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		int cellsX = IntMath.divide(getWidth(), tileModel.getAvgTileWidth(), RoundingMode.HALF_UP);
		int cellsY = IntMath.divide(getHeight(), tileModel.getTileHeight(), RoundingMode.HALF_UP);
		tileModel.setMapWidth(cellsX);
		tileModel.setMapHeight(cellsY);
		
		// cut of partly draw hexagons
		g.clipRect(0, 0, tileModel.getWorldWidth(), tileModel.getWorldHeight());

		drawTiles(g);

		Object oldAAhint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawVectorField(g);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAAhint);

		drawSelection(g);
		g1.setClip(null);
	}

	private void drawVectorField(Graphics2D g)
	{
		int mapWidth = tileModel.getMapWidth();
		int mapHeight = tileModel.getMapHeight();

		ViewingConditions env = ViewingConditions.sRGB_typical_envirnonment;
		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);

		double maxDist = tileModel.getTileHeight() * 0.5;	// distance to tile border
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				double forceX = 0; 
				double forceY = 0;

				int worldX = tileModel.getWorldX(x, y);
				int worldY = tileModel.getWorldY(x, y);
				Color color = getTileColor(worldX, worldY);
				PColor color2 = PColor.create(colorSpace, color.getColorComponents(new float[3]));
				Collection<Direction> neighs = tileModel.validDirections(x, y);
				
				for (Direction dir : neighs)
				{
					// compute only if opposing forces can be computed as well
					if (neighs.contains(dir.getOpposite()))
					{
						Tile n = tileModel.getNeighborFor(x, y, dir);
						int nworldX = tileModel.getWorldX(n.getMapX(), n.getMapY());
						int nworldY = tileModel.getWorldY(n.getMapX(), n.getMapY());
						Color ncolor = getTileColor(nworldX, nworldY);
						PColor ncolor2 = PColor.create(colorSpace, ncolor.getColorComponents(new float[3]));
						
						double weight = ColorTools.distance(color2, ncolor2, env);
						double dx = nworldX - worldX;
						double dy = nworldY - worldY;
						double len = Math.sqrt(dx * dx + dy * dy);
						forceX += weight * dx / len;
						forceY += weight * dy / len;
					}
				}
				
				double force = Math.sqrt(forceX * forceX + forceY * forceY);
				if (force > maxDist) {
					forceX = forceX * maxDist / force;
					forceY = forceY * maxDist / force;
				}
				
				Color arrColor = Color.BLACK;
				int tx = (int)(worldX + forceX + 0.5);
				int ty = (int)(worldY + forceY + 0.5);
				drawArrow(g, worldX, worldY, tx, ty, arrColor);
			}
		}
	}

	private void drawArrow(Graphics2D g, int sx, int sy, int tx, int ty, Color color)
	{
		double dAng = Math.toRadians(30);

		double dx = sx - tx;
		double dy = sy - ty;
		double len = Math.sqrt(dx * dx + dy * dy);
		double wlen = len / 3;	// one third of the total arrow length is for the wings

		double ang = Math.atan2(dy, dx);
		
		int w1x = (int) (tx + wlen * Math.cos(ang + dAng) + 0.5);
		int w1y = (int) (ty + wlen * Math.sin(ang + dAng) + 0.5);

		int w2x = (int) (tx + wlen * Math.cos(ang - dAng) + 0.5);
		int w2y = (int) (ty + wlen * Math.sin(ang - dAng) + 0.5);

		g.setColor(color);

		g.drawLine(sx, sy, tx, ty);
		g.drawLine(tx, ty, w1x, w1y);
		g.drawLine(tx, ty, w2x, w2y);
	}

	private Color getTileColor(int worldX, int worldY)
	{
		float mapX = (float)worldX / tileModel.getWorldWidth();
		float mapY = (float)worldY / tileModel.getWorldHeight();
		
		Color color = colormap.getColor(mapX, mapY);
		return color;
	}

	private void drawTiles(Graphics2D g)
	{
		int mapWidth = tileModel.getMapWidth();
		int mapHeight = tileModel.getMapHeight();

		AffineTransform at = g.getTransform();
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int worldX = tileModel.getWorldX(x, y);
				int worldY = tileModel.getWorldY(x, y);
				Color color = getTileColor(worldX, worldY);
				float scale = color.getAlpha() / 255f;

				g.setColor(color);
				g.translate(worldX, worldY);
				g.scale(scale, scale);
				g.fill(hexagon);
				g.draw(hexagon);
				g.setTransform(at);
			}
		}
	}

	private void drawSelection(Graphics2D g)
	{
		if (selection.isPresent())
		{
			Tile tile = selection.get();
			int x = tile.getMapX();
			int y = tile.getMapY();
			int worldX = tileModel.getWorldX(x, y);
			int worldY = tileModel.getWorldY(x, y);
			
			g.setColor(Color.BLACK);
			g.translate(worldX, worldY);
			g.draw(hexagon);
			g.translate(-worldX, -worldY);

			// draw debug info - arrows to all valid neighbors
//			for (Direction dir : tileModel.validDirections(x, y))
//			{
//				Tile n = tileModel.getNeighborFor(x, y, dir);
//				drawArrow(g, worldX, worldY, tileModel.getWorldX(n.getMapX(), n.getMapY()), tileModel.getWorldY(n.getMapX(), n.getMapY()), Color.BLACK);
//			}
		}
	}

}
