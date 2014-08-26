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


/**
 * Defines a tile
 * @author Martin Steiger
 */
public class Tile
{
	private final int x;
	private final int y;
	
	/**
	 * @param x the x-coordinate on the map
	 * @param y the y-coordinate on the map
	 */
	public Tile(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return the x coordinate on the map
	 */
	public int getMapX()
	{
		return x;
	}
	
	/**
	 * @return the y coordinate on the map
	 */
	public int getMapY()
	{
		return y;
	}
	
	@Override
	public String toString()
	{
		return x + " / " + y;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Tile other = (Tile) obj;
		
		if (x != other.x)
			return false;
		
		if (y != other.y)
			return false;
		
		return true;
	}
	
	
}
