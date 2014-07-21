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

package colormaps.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BremmFig9b extends GuoLabStyle {
	public BremmFig9b() {
		super(constant1f(55f), linearBa(-10, 40, -20, 35));
	}

	@Override
	public String getName() {
		return "Bremm 2011 Fig. 9b";
	}

	@Override
	public String getDescription() {
		return "Bremm 2011 Fig. 9b: Numbers are guessed and still seem slighly off.";
	}

	@Override
	public List<String> getReferences() {
		return Arrays.asList();
	}

}
