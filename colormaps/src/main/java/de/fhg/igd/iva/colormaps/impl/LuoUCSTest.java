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

package de.fhg.igd.iva.colormaps.impl;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author jubernar
 * @deprecated sollte nicht ins Paper. War für Debug issues gedacht (validierung der Metriken) 
 *
 */
@Deprecated
public class LuoUCSTest extends GuoLabStyle
{
	public LuoUCSTest()
	{
		super(constant1f(70f), linearAB(20, 80, 30, -60));
	}

	@Override
	public String getName()
	{
		return "Test Scale like Luo UCS paper fig. 3a";
	}

	@Override
	public String getDescription()
	{
		return "CAM02-UCS, Luo 2006 ";
	}

	@Override
	public List<String> getReferences() {
		return Collections.singletonList("Luo06");
	}
}
