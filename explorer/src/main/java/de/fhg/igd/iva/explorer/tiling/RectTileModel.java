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

import static de.fhg.igd.iva.explorer.tiling.Direction.EAST;
import static de.fhg.igd.iva.explorer.tiling.Direction.NORTH;
import static de.fhg.igd.iva.explorer.tiling.Direction.NORTH_EAST;
import static de.fhg.igd.iva.explorer.tiling.Direction.NORTH_WEST;
import static de.fhg.igd.iva.explorer.tiling.Direction.SOUTH;
import static de.fhg.igd.iva.explorer.tiling.Direction.SOUTH_EAST;
import static de.fhg.igd.iva.explorer.tiling.Direction.SOUTH_WEST;
import static de.fhg.igd.iva.explorer.tiling.Direction.WEST;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.math.IntMath;


/**
 * A rectangular tile model
 * @author Martin Steiger
 */
public class RectTileModel implements TileModel
{
	private int mapWidth;
	private int mapHeight;

	private int tileWidth;
	private int tileHeight;

	/**
	 * @param tileWidth width of a single tile
	 * @param tileHeight height of a single tile
	 * @param mapWidth width of the map
	 * @param mapHeight height of the map
	 */
	public RectTileModel(int tileWidth, int tileHeight, int mapWidth, int mapHeight)
	{
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
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
	public Tile getTile(int mapX, int mapY)
	{
		return new Tile(mapX, mapY);
	}

	@Override
	public int getWorldX(int mapX, int mapY)
	{
		return mapX * tileWidth;
	}

	@Override
	public int getWorldY(int x, int y)
	{
		return y * tileHeight;
	}
	
	@Override
	public int getWorldWidth()
	{
		return tileWidth * mapWidth;
	}
	
	@Override
	public int getWorldHeight()
	{
		return tileHeight * mapHeight;
	}
	
	@Override
	public Optional<Tile> getTileAtWorldPos(int worldX, int worldY)
	{
		int x = IntMath.divide(worldX, tileWidth, RoundingMode.FLOOR);
		int y = IntMath.divide(worldY, tileHeight, RoundingMode.FLOOR);
		
		if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight)
		{
			return Optional.of(getTile(x, y));
		}
		
		return Optional.absent();
	}

	@Override
	public List<Tile> getTilesInRect(int worldX0, int worldY0, int worldX1, int worldY1)
	{
		int x0 = IntMath.divide(worldX0, tileWidth, RoundingMode.FLOOR);
		int y0 = IntMath.divide(worldY0, tileHeight, RoundingMode.FLOOR);
		int x1 = IntMath.divide(worldX1, tileWidth, RoundingMode.FLOOR);
		int y1 = IntMath.divide(worldY1, tileHeight, RoundingMode.FLOOR);
		
		// Restrict to map bounds
		int minX = Math.max(x0, 0);
		int maxX = Math.min(x1, mapWidth - 1);
		int minY = Math.max(y0, 0);
		int maxY = Math.min(y1, mapHeight - 1);

		List<Tile> result = new ArrayList<Tile>();

		for (int y = minY; y <= maxY; y++)
		{
			for (int x = minX; y <= maxX; x++)
			{
				result.add(getTile(x, y));
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

		if (x > 0)
			allowed.add(WEST);

		if (y < mapHeight - 1)
			allowed.add(SOUTH);

		if (x < mapWidth - 1)
			allowed.add(EAST);

		if (x > 0 && y > 0)
			allowed.add(NORTH_WEST);

		if (x > 0 && y < mapHeight - 1)
			allowed.add(SOUTH_WEST);

		if (x < mapWidth - 1 && y > 0)
			allowed.add(NORTH_EAST);

		if (x < mapWidth - 1 && y < mapHeight - 1)
			allowed.add(SOUTH_EAST);

		return allowed;
	}
	
	@Override
	public Tile getNeighborFor(int x, int y, Direction dir)
	{
		switch (dir)
		{
		case EAST:
			return getTile(x + 1, y);
			
		case NORTH:
			return getTile(x, y - 1);

		case SOUTH:
			return getTile(x, y + 1);
			
		case WEST:
			return getTile(x - 1, y);

		case NORTH_EAST:
			return getTile(x + 1, y - 1); 
			
		case NORTH_WEST:
			return getTile(x - 1, y - 1);
			
		case SOUTH_EAST:
			return getTile(x + 1, y + 1);
			
		case SOUTH_WEST:
			return getTile(x - 1, y + 1);
			
		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}
	
}
