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

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * A simple enumeration of directions in 45 degree steps
 * @author Martin Steiger
 */
@SuppressWarnings("javadoc")
public enum Direction
{
	NORTH,
	NORTH_EAST,
	EAST,
	SOUTH_EAST,
	SOUTH,
	SOUTH_WEST,
	WEST,
	NORTH_WEST;

	
	static Map<Direction, Direction> opposites = Maps.newHashMap();

	static {
		opposites.put(NORTH, SOUTH);
		opposites.put(NORTH_EAST, SOUTH_WEST);
		opposites.put(EAST, WEST);
		opposites.put(SOUTH_EAST, NORTH_WEST);
		opposites.put(SOUTH, NORTH);
		opposites.put(SOUTH_WEST, NORTH_EAST);
		opposites.put(WEST, EAST);
		opposites.put(NORTH_WEST, SOUTH_EAST);
	}

	/**
	 * @return the opposite direction
	 */
	public Direction getOpposite() {
		return opposites.get(this);
	}
}
