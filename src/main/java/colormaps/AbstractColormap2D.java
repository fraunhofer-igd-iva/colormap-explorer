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

package colormaps;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * A partly (default) implementation of {@link Colormap2D}
 * @author Martin Steiger
 */
public abstract class AbstractColormap2D implements Colormap2D {

	protected void checkRanges(float x, float y) {
		Preconditions.checkArgument(0 <= x && x <= 1, "X must in in range [0..1], but is %s", x);
		Preconditions.checkArgument(0 <= y && y <= 1, "Y must in in range [0..1], but is %s", y);
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public List<String> getReferences()
	{
		return Collections.emptyList();
	}
	
	@Override
	public Color getColor(double x, double y)
	{
		return getColor((float)x, (float)y);
	}
	
	// TODO: remove by fixing all implementations
	protected abstract Color getColor(float x, float y);
}
