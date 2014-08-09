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
import java.util.List;

/**
 * Delegates all method calls to another implementation
 * @author Martin Steiger
 */
public class DelegateColormap2D implements Colormap2D
{
	private final Colormap2D delegate;

	/**
	 * @param colormap the underlying color map
	 */
	public DelegateColormap2D(Colormap2D colormap)
	{
		this.delegate = colormap;
	}

	@Override
	public Color getColor(double x, double y)
	{
		return delegate.getColor(x, y);
	}

	@Override
	public String getName()
	{
		return delegate.getName();
	}

	@Override
	public String getDescription()
	{
		return delegate.getDescription();
	}

	@Override
	public ColorSpace getColorSpace()
	{
		return delegate.getColorSpace();
	}

	@Override
	public List<String> getReferences()
	{
		return delegate.getReferences();
	}
	
	/**
	 * @return the underlying delegate colormap
	 */
	protected Colormap2D getDelegate()
	{
		return delegate;
	}
}
