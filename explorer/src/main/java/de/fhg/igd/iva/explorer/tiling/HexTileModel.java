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

package de.fhg.igd.iva.explorer.tiling;

import static de.fhg.igd.iva.explorer.tiling.Direction.NORTH;
import static de.fhg.igd.iva.explorer.tiling.Direction.NORTH_EAST;
import static de.fhg.igd.iva.explorer.tiling.Direction.NORTH_WEST;
import static de.fhg.igd.iva.explorer.tiling.Direction.SOUTH;
import static de.fhg.igd.iva.explorer.tiling.Direction.SOUTH_EAST;
import static de.fhg.igd.iva.explorer.tiling.Direction.SOUTH_WEST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class HexTileModel implements TileModel
{
	private int mapWidth;
	private int mapHeight;

	private int tileWidth;
	private int tileHeight;

	private int topLength;

	
	/**
	 * @param tileWidth width of a single tile
	 * @param tileHeight height of a single tile
	 * @param topLength the top length of a hexagon
	 * @param mapWidth width of the map
	 * @param mapHeight height of the map
	 */
	public HexTileModel(int tileWidth, int tileHeight, int topLength, int mapWidth, int mapHeight)
	{
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.topLength = topLength;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
	}

	private static boolean isOdd(int v)
	{
		return v % 2 == 1;
	}

	private static boolean isEven(int v)
	{
		return v % 2 == 0;
	}

	@Override
	public int getMapHeight()
	{
		return mapHeight;
	}

	@Override
	public int getMapWidth()
	{
		return mapWidth;
	}	

	/**
	 * @return the top length of the hexagon
	 */
	public int getTopLength()
	{
		return topLength;
	}

	/**
	 * @return the tile height
	 */
	public int getTileHeight()
	{
		return tileHeight;
	}
	
	/**
	 * @return the tile width
	 */
	public int getTileWidth()
	{
		return tileWidth;
	}

	@Override
	public Tile getTile(int x, int y)
	{
		Preconditions.checkArgument(x >= 0 && x < mapWidth, "x must be in [0..%s], but is %s", mapWidth, x);
		Preconditions.checkArgument(y >= 0 && y < mapHeight, "y must be in [0..%s], but is %s", mapHeight, y);
		
		return new Tile(x, y);
	}

	@Override
	public int getWorldX(int x, int y)
	{
		return x * (topLength + tileWidth) / 2;
	}

	public int getAvgTileWidth()
	{
		return (topLength + tileWidth) / 2;
	}

	@Override
	public int getWorldY(int x, int y)
	{
		return y * tileHeight + (x % 2) * tileHeight / 2;
	}
	
	@Override
	public int getWorldWidth()
	{
		return (mapWidth-1) * (topLength + tileWidth) / 2;
	}

	@Override
	public int getWorldHeight()
	{
		return mapHeight * tileHeight - tileHeight / 2;
	}

	@Override
	public Optional<Tile> getTileAtWorldPos(int x, int y)
	{
		double a = tileWidth / 2.0;
		double b = tileHeight / 2.0;
		double c = topLength / 2.0;
		
		// Find out which major row and column we are on:
	    int row = (int)(y / b);
	    int col = (int)(x / (a + c));
	 
	    // Compute the offset into these row and column:
	    double dy = y - row * b;
	    double dx = x - col * (a + c);
	 
	    // Are we on the left of the hexagon edge, or on the right?
	    if (((row ^ col) & 1) == 0)
	        dy = b - dy;
	    int right = dy * (a - c) < b * (dx - c) ? 1 : 0;
	 
	    // Now we have all the information we need, just fine-tune row and column.
	    row += (col ^ row ^ right) & 1;
	    col += right;
		
		if (col >= 0 && col < mapWidth &&
			row >= 0 && row < mapHeight * 2)

		{
			return Optional.of(getTile(col, row / 2));
		}
		
		return Optional.absent();
	}

	@Override
	public List<Tile> getTilesInRect(int worldX0, int worldY0, int worldX1, int worldY1)
	{
		int tileHeight2 = tileHeight / 2;
		
		int avgWidth = (topLength + tileWidth) / 2;
		
		// the width of one diagonal
		int leftIn = (tileWidth - topLength) / 2;
		
		// this computes the map-y based on rectangular shapes 
		// it is then independent of x - basically the inverse of getWorldY()
		
		int y02 = worldY0 / tileHeight2 - 1;	// the prev. row is displayed as it is 2 rows high
		int y12 = worldY1 / tileHeight2 - 1;
		int x0 = (worldX0 - leftIn) / avgWidth;
		int x1 = worldX1 / avgWidth;
		
		// Restrict to map bounds
		int minY = Math.max(y02, 0);
		int maxY = Math.min(y12, 2 * mapHeight - 2);
		int minX = Math.max(x0, 0);
		int maxX = Math.min(x1, mapWidth - 1);

		List<Tile> result = new ArrayList<Tile>();

		// if the top y/2 value is odd
		// then add every odd x tile
		if (minY % 2 == 1)
		{
			for (int x = minX + (minX + 1) % 2; x <= maxX; x += 2)
			{
				result.add(getTile(x, minY / 2));
			}
			
			minY++;
		}
		
		// minY is always even here
		
		for (int y = minY; y <= maxY; y += 2)
		{
			for (int x = minX; x <= maxX; x++)
			{
				result.add(getTile(x, y / 2));
			}
		}

		// if the bottom y/2 value is odd
		// then add every even x tile
		if (maxY % 2 == 1)
		{
			for (int x = minX + (minX % 2); x < maxX + 1; x += 2)
			{
				result.add(getTile(x, maxY / 2 + 1));
			}
		}
		
		return result;
	}

	@Override
	public Collection<Direction> validDirections(int x, int y)
	{
		Set<Direction> allowed = new HashSet<>();
		
		if (y > 0)
			allowed.add(NORTH);
		
		if (y > 0 || isOdd(x))
		{
			if (x > 0)
				allowed.add(NORTH_WEST);
			
			if (x < mapWidth - 1)
				allowed.add(NORTH_EAST);
		}

		if (y < mapHeight - 1 || isEven(x))
		{
			if (x > 0)
				allowed.add(SOUTH_WEST);
			
			if (x < mapWidth - 1)
				allowed.add(SOUTH_EAST);
		}
		
		if (y < mapHeight - 1)
			allowed.add(SOUTH);
		
		return allowed;
	}

	@Override
	public Tile getNeighborFor(int x, int y, Direction dir)
	{
		switch (dir)
		{
		case NORTH:
			return getTile(x, y - 1);

		case SOUTH:
			return getTile(x, y + 1);
			
		case NORTH_EAST:
			if (isOdd(x)) 
				return getTile(x + 1, y); else 
				return getTile(x + 1, y - 1);
			
		case NORTH_WEST:
			if (isOdd(x))
				return getTile(x - 1, y); else
				return getTile(x - 1, y - 1);
			
		case SOUTH_EAST:
			if (isOdd(x))
				return getTile(x + 1, y + 1); else
				return getTile(x + 1, y);
			
		case SOUTH_WEST:
			if (isOdd(x))
				return getTile(x - 1, y + 1); else
				return getTile(x - 1, y);

		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}

	public void setMapWidth(int mapWidth)
	{
		this.mapWidth = mapWidth; 
	}

	public void setMapHeight(int mapHeight)
	{
		this.mapHeight = mapHeight; 
	}
}
