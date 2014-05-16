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

import java.util.Collection;
import java.util.List;

import com.google.common.base.Optional;


/**
 * Defines a terrain model in a very general form
 * @author Martin Steiger
 */
public interface TileModel
{
	/**
	 * @param worldX0 the left world coordinate
	 * @param worldY0 the top world coordinate
	 * @param worldX1 the right world coordinate
	 * @param worldY1 the bottom world coordinate
	 * @return a list of all tiles that intersect with the given rect, <b>sorted by y-value</b>
	 */
	List<Tile> getTilesInRect(int worldX0, int worldY0, int worldX1, int worldY1);

	/**
	 * @param worldX the world x
	 * @param worldY the world y
	 * @return the tile at the given location if available
	 */
	Optional<Tile> getTileAtWorldPos(int worldX, int worldY);

	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return the world x coordinate
	 */
	int getWorldY(int mapX, int mapY);

	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return the world x coordinate
	 */
	int getWorldX(int mapX, int mapY);

	/**
	 * @param x the map x coordinate
	 * @param y the map y coordinate
	 * @return the tile
	 */
	Tile getTile(int x, int y);

	/**
	 * @return the map width
	 */
	int getMapWidth();

	/**
	 * @return the map height
	 */
	int getMapHeight();

	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return an unsorted collection of valid directions
	 */
	Collection<Direction> validDirections(int mapX, int mapY);

	/**
	 * @param dir the direction of interest
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return the neighbor tile in that direction
	 */
	Tile getNeighborFor(int mapX, int mapY, Direction dir);
}
