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

import java.awt.BasicStroke;
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
import javax.swing.ToolTipManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import views.ColormapView;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.math.IntMath;

import de.fhg.igd.iva.explorer.events.ColormapSelectionEvent;
import de.fhg.igd.iva.explorer.events.MyEventBus;
import de.fhg.igd.iva.explorer.events.TileSelectionEvent;
import de.fhg.igd.iva.explorer.tiling.Direction;
import de.fhg.igd.iva.explorer.tiling.HexTileModel;
import de.fhg.igd.iva.explorer.tiling.Tile;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

/**
 * Displays the colormap based on rectangles
 * @author Martin Steiger
 */
public class DecomposedPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(DecomposedPanel.class);
	
	private static final long serialVersionUID = 6582370458976011408L;

	private static final ViewingConditions VIEW_ENV = ViewingConditions.sRGB_typical_envirnonment;
	private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_sRGB);

	
	private ColormapView colormap;
	private HexTileModel tileModel;
	private Optional<Tile> selection = Optional.absent();
	private final Polygon hexagon;

	/**
	 * @param colormap the colormap to show
	 */
	public DecomposedPanel(ColormapView colormap)
	{
		setToolTipText(colormap.getDescription());
		
		this.colormap = colormap;
		int cells = 10;
		
		// the rounding error is < 0.1 pixel for widths: 22 / 30 / 38 / 52
		int width = 32;
		int topLen = width / 2;
		
		// adjust height to create regular hexagons
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
		
		MyEventBus.getInstance().register(this);
	}

	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		Optional<Tile> oldSelection = selection;
		
		// clear and re-set selection to trigger update
		updateTileSelection(Optional.<Tile>absent());
		updateTileSelection(oldSelection);
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
		
		TileSelectionEvent event = new TileSelectionEvent(tiles, DecomposedPanel.this.colormap, tileModel);
		MyEventBus.getInstance().post(event);
		
		logger.debug("Selected tiles: " + tiles);
		
		repaint();
	}

	/**
	 * @param colorMapView the new colormap view
	 */
	public void setColorMapView(ColormapView colorMapView)
	{
		this.colormap = colorMapView;
		setToolTipText(colormap.getDescription());
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		int cellsX = IntMath.divide(getWidth(), tileModel.getAvgTileWidth(), RoundingMode.UP);
		int cellsY = IntMath.divide(getHeight(), tileModel.getTileHeight(), RoundingMode.UP);
		tileModel.setMapWidth(cellsX);
		tileModel.setMapHeight(cellsY);
		
		// cut of partly draw hexagons
		g.clipRect(0, 0, tileModel.getWorldWidth(), tileModel.getWorldHeight());

		drawTiles(g);

		Object oldAAhint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawVectorField(g);
		drawSelection(g);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAAhint);

		g1.setClip(null);
	}

	private void drawVectorField(Graphics2D g)
	{
		int mapWidth = tileModel.getMapWidth();
		int mapHeight = tileModel.getMapHeight();

		double maxDist = tileModel.getTileHeight() * 0.5;	// distance to tile border
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {

				if (!shouldDraw(x, y))
					continue;
				
				double forceX = 0; 
				double forceY = 0;

				int worldX = tileModel.getWorldX(x, y);
				int worldY = tileModel.getWorldY(x, y);
				Color color = getTileColor(worldX, worldY);
				PColor color2 = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
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
						PColor ncolor2 = PColor.create(COLOR_SPACE, ncolor.getColorComponents(new float[3]));
						
						double weight = ColorTools.distance(color2, ncolor2, VIEW_ENV);
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

	private boolean shouldDraw(int x, int y)
	{
		if (selection.isPresent())
		{
			Tile tile = selection.get();
			
			// don't draw selected tile
			if (tile.getMapX() == x && tile.getMapY() == y)
				return false;
			
			// don't draw neighbors of the selected tile
			Collection<Direction> dirs = tileModel.validDirections(tile.getMapX(), tile.getMapY());
			for (Direction dir : dirs)
			{
				Tile neighbor = tileModel.getNeighborFor(tile.getMapX(), tile.getMapY(), dir);
				if (neighbor.getMapX() == x && neighbor.getMapY() == y)
					return false;
			}
			
			return true;
		}

		return true;
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
		double mapX = (double)worldX / tileModel.getWorldWidth();
		double mapY = (double)worldY / tileModel.getWorldHeight();
		
		Color color = colormap.getColor(mapX, mapY);
		return color;
	}

	private double getTileReliability(int worldX, int worldY)
	{
		double mapX = (double)worldX / tileModel.getWorldWidth();
		double mapY = (double)worldY / tileModel.getWorldHeight();
		
		return colormap.getReliability(mapX, mapY);
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
				float scale = (float) getTileReliability(worldX, worldY);

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
			drawHexagonFrame(g, tile, Color.BLACK);

			double totalDist = computeTotalColorDistance(tile);
			
			if (totalDist > 0)
			{
				drawDistanceGrid(g, tile, totalDist);
			}
		}
	}

	private void drawDistanceGrid(Graphics2D g, Tile tile, double totalDist)
	{
		int x = tile.getMapX();
		int y = tile.getMapY();
		int worldX = tileModel.getWorldX(x, y);
		int worldY = tileModel.getWorldY(x, y);
		
		Color color = getTileColor(worldX, worldY);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));
		
		// draw arrows to all neighbors weighted by their color distance
		Collection<Direction> dirs = tileModel.validDirections(x, y);
		for (Direction dir : dirs)
		{
			Tile n = tileModel.getNeighborFor(x, y, dir);
			int neighX = tileModel.getWorldX(n.getMapX(), n.getMapY());
			int neighY = tileModel.getWorldY(n.getMapX(), n.getMapY());

			Color ncolor = getTileColor(neighX, neighY);
			PColor pncolor = PColor.create(COLOR_SPACE, ncolor.getColorComponents(new float[3]));
			
			double dist = ColorTools.distance(pcolor, pncolor, VIEW_ENV);
			
			double weight = 10 * dist / totalDist;
			
//			int dx = neighX - worldX;
//			int dy = neighY - worldY;
//			int tx = (int)(worldX + dx * weight + 0.5);
//			int ty = (int)(worldY + dy * weight + 0.5);
//			drawArrow(g, worldX, worldY, tx, ty, Color.BLACK);

			g.setStroke(new BasicStroke((float)weight));
			drawArrow(g, worldX, worldY, neighX, neighY, Color.BLACK);
		}
		
		g.setStroke(new BasicStroke());
	}

	private void drawHexagonFrame(Graphics2D g, Tile tile, Color color)
	{
		int x = tile.getMapX();
		int y = tile.getMapY();
		int worldX = tileModel.getWorldX(x, y);
		int worldY = tileModel.getWorldY(x, y);
		
		g.setColor(color);
		g.translate(worldX, worldY);
		g.draw(hexagon);
		g.translate(-worldX, -worldY);
	}

	private double computeTotalColorDistance(Tile tile)
	{
		int x = tile.getMapX();
		int y = tile.getMapY();

		int worldX = tileModel.getWorldX(x, y);
		int worldY = tileModel.getWorldY(x, y);

		Color color = getTileColor(worldX, worldY);
		PColor pcolor = PColor.create(COLOR_SPACE, color.getColorComponents(new float[3]));

		double sum = 0;
		
		for (Direction dir : tileModel.validDirections(x, y))
		{
			Tile n = tileModel.getNeighborFor(x, y, dir);
			int neighX = tileModel.getWorldX(n.getMapX(), n.getMapY());
			int neighY = tileModel.getWorldY(n.getMapX(), n.getMapY());

			Color ncolor = getTileColor(neighX, neighY);
			PColor pncolor = PColor.create(COLOR_SPACE, ncolor.getColorComponents(new float[3]));
			
			double dist = ColorTools.distance(pcolor, pncolor, VIEW_ENV);
			
			sum += Math.abs(dist);
		}
		
		return sum;
	}
}
