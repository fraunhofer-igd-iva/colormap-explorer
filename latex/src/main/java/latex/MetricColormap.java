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
package latex;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import algorithms.quality.ColormapQuality;

import com.google.common.collect.Maps;

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * Defines a colormap in the LaTeX output
 * @author Martin Steiger
 */
public class MetricColormap
{
	private final Colormap colormap;
	private final Map<ColormapQuality, String> metrics = Maps.newHashMap();
	private final Map<ColormapQuality, String> colors = Maps.newHashMap();
	private final String imagePath;

	public MetricColormap(Colormap cm, String imagePath)
	{
		this.colormap = cm;
		this.imagePath = imagePath;
	}

	public String getName()
	{
		return colormap.getName();
	}
	
	public String getImagePath()
	{
		return imagePath;
	}

	public void addMetric(ColormapQuality metric, Double quality, Integer points)
	{
		String format;
		
		if (quality.isInfinite())
		{
			format = "$\\infty$";
		} 
		else
		{
			format = String.format(Locale.ENGLISH, "%.1f", quality);
		}
		
		
		metrics.put(metric, format);
		
		String colorName = (metric.moreIsBetter() ? "quality" : "malus") + points;
		colors.put(metric, colorName);
	}

	public String getColorspace()
	{
		return colormap.getColorSpace().toString().replaceAll("\\_", "");
	}
	
	public Map<ColormapQuality, String> getValues()
	{
		return Collections.unmodifiableMap(metrics);
	}
	
	public Map<ColormapQuality, String> getColors()
	{
		return Collections.unmodifiableMap(colors);
	}	
}
