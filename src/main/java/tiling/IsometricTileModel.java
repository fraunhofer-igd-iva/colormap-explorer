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

package tiling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Optional;


/**
 * A diamond-shaped tile model
 * @author Martin Steiger
 */
public class IsometricTileModel implements TileModel
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
	public IsometricTileModel(int tileWidth, int tileHeight, int mapWidth, int mapHeight)
	{
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
	}

	private static boolean isOdd(int v)
	{
		return v % 2 == 1;
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
		return mapX * tileWidth + (mapY % 2) * tileWidth / 2;
	}

	@Override
	public int getWorldY(int x, int y)
	{
		return y * tileHeight / 2;
	}
	
	@Override
	public Optional<Tile> getTileAtWorldPos(int worldX, int worldY)
	{
		// Origin is at (0, h/2)
		double worldY2 = worldY - tileHeight * 0.5; 

		//     ( w/2 )        ( w/2 )
		// r = (     )    c = (     ) 
		//     ( h/2 )        (-h/2 )
		
		// x = r * w + c * w
		// y = r * h - c * h
		
		// solving for r gives r = y/h + c
		
		// Math.floor() rounds negative values down whereas casting to int rounds them up
		int r = (int) Math.floor(worldX / (double) tileWidth + worldY2 / tileHeight); 
		int c = (int) Math.floor(worldX / (double) tileWidth - worldY2 / tileHeight);

		// r and c are only one tile edge long
		int x = (int)Math.floor((r + c) / 2.0);
		
		// r - c always >= 0
		int y = r - c;
		
		if (x >= 0 && x < mapWidth &&
			y >= 0 && y < mapHeight)

		{
			return Optional.of(getTile(x, y));
		}
		
		return Optional.absent();
	}

	@Override
	public List<Tile> getTilesInRect(int worldX0, int worldY0, int worldX1, int worldY1)
	{
		// this computes the map-y based on rectangular shapes 
		// it is then independent of x - basically the inverse of getWorldY()
		int y0 = (worldY0 * 2) / tileHeight - 1;
		int y1 = (worldY1 * 2) / tileHeight;
		
		// Restrict to map bounds
		int minY = Math.max(y0, 0);
		int maxY = Math.min(y1, mapHeight - 1);

		List<Tile> result = new ArrayList<Tile>();

		for (int y = minY; y <= maxY; y++)
		{
			int x0 = (worldX0 - (y % 2) * tileWidth / 2) / tileWidth;
			int x1 = (worldX1 - (y % 2) * tileWidth / 2) / tileWidth;

			int minX = Math.max(x0, 0);
			int maxX = Math.min(x1, mapWidth - 1);

			for (int x = minX; x <= maxX; x++)
			{
				result.add(getTile(x, y));
			}
		}

		return result;
	}
 
	@Override
	public Collection<Direction> validDirections(int x, int y)
	{
		throw new UnsupportedOperationException();
//		Set<Direction> allowed = new HashSet<>();
//		
//		if (y > 0)
//			allowed.add(NORTH);
//
//		if (x > 0)
//			allowed.add(WEST);
//
//		if (y < mapHeight - 1)
//			allowed.add(SOUTH);
//
//		if (x < mapWidth - 1)
//			allowed.add(EAST);
//
		// TODO: add diagonal checks
//
//		return allowed;
	}
	
	@Override
	public Tile getNeighborFor(int x, int y, Direction dir)
	{
		switch (dir)
		{
		case EAST:
			return getTile(x + 1, y);
			
		case NORTH:
			return getTile(x, y - 2);

		case SOUTH:
			return getTile(x, y + 2);
			
		case WEST:
			return getTile(x - 1, y);

		case NORTH_EAST:
			if (isOdd(y)) 
				return getTile(x + 1, y - 1); else 
				return getTile(x, y - 1);
			
		case NORTH_WEST:
			if (isOdd(y))
				return getTile(x, y - 1); else
				return getTile(x - 1, y - 1);
		case SOUTH_EAST:
			if (isOdd(y))
				return getTile(x + 1, y + 1); else
				return getTile(x, y + 1);
		case SOUTH_WEST:
			if (isOdd(y))
				return getTile(x, y + 1); else
				return getTile(x - 1, y + 1);
			
		default:
			throw new IllegalArgumentException("Invalid direction " + dir);
		}
	}
	
}
