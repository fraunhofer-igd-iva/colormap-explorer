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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.misc.ErrorManager;

import algorithms.JndRegionComputer;
import algorithms.quality.ColorDynamicDistBlack;
import algorithms.quality.ColorDynamicDistWhite;
import algorithms.quality.ColorDynamicWhiteContrast;
import algorithms.quality.ColorExploitation;
import algorithms.quality.ColormapQuality;
import algorithms.sampling.CircularSampling;
import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;
import colormaps.transformed.SimpleFilteredColormap2D.ViewType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

/**
 * Generates LaTeX table output for a list of colormaps 
 * @author Martin Steiger
 */
public final class LatexTableQuality
{
	private LatexTableQuality()
	{
		// private
	}
	
	public static File generateTable(List<Colormap2D> colormaps, File outputFolder) throws IOException 
    {
    	STRawGroupDir templateDir = new STRawGroupDir("src/main/resources/latex");
        templateDir.delimiterStartChar = '$';
        templateDir.delimiterStopChar = '$';
        
        Map<Colormap2D, MetricColormap> mcms = Maps.newHashMap();

        for (Colormap2D cm : colormaps)
        {
        	String fname = cm.getName() + "_" + ViewType.REAL.toString();
            String relativePath = "images/" + toFilename(fname);

        	MetricColormap mcm = new MetricColormap(cm, relativePath);
			mcms.put(cm, mcm);
        }
        
        SamplingStrategy sampling = new CircularSampling(50);

        List<ColormapQuality> measures = Lists.newArrayList();
        measures.add(new ColorExploitation(sampling));
		measures.add(new ColorDynamicDistBlack(sampling));
        measures.add(new ColorDynamicDistWhite(sampling));
        measures.add(new ColorDynamicWhiteContrast(sampling));

        for (ColormapQuality measure : measures)
        {
        	Map<Colormap2D, Double> mapQualities = computeQuality(colormaps, measure);
        	Map<Colormap2D, Integer> mapPoints = computePoints(mapQualities);
        
            for (Colormap2D cm : colormaps)
            {
            	// TODO: find a better way to truncate
            	double quality = mapQualities.get(cm).intValue();
            	Integer points = mapPoints.get(cm);
				mcms.get(cm).addMetric(measure, quality, points);
	        }
        }

        ST st = templateDir.getInstanceOf("MetricTable");
		st.add("metrics", measures);
		st.add("colors", createColorDefs());
		st.add("colormaps", mcms.values());

        File texFile = new File(outputFolder, "metric_table.tex");
        st.write(texFile, ErrorManager.DEFAULT_ERROR_LISTENER);
        
        return texFile;
    }

	private static List<LatexColor> createColorDefs()
	{
		List<LatexColor> colors = Lists.newArrayList();
		ColorRamp colorramp = new ColorRamp();
		
		for (int i=0; i<=100; i++)
		{
			double val = 1.0 - i / 100d;
			Color col = colorramp.getColor(val);
			
			colors.add(new LatexColor(col, "quality" + i));
		}
		
		return colors;
	}
	
	private static Map<Colormap2D, Double> computeQuality(List<Colormap2D> colormaps, ColormapQuality measure)
	{
		Map<Colormap2D, Double> qualityMap = Maps.newHashMap();
        for (Colormap2D cm : colormaps)
        {
        	double quality = measure.getQuality(cm);
        	qualityMap.put(cm, Double.valueOf(quality));
        }
        
        return qualityMap;
	}
	
	private static Range<Double> getMinMax(Collection<Double> quals)
	{
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (Double quality : quals)
		{	
	    	if (quality > max)
	    		max = quality;
	    	
	    	if (quality < min)
	    		min = quality;
		}
		
		return Range.closed(min, max);
	}

    private static Map<Colormap2D, Integer> computePoints(Map<Colormap2D, Double> qualityMap)
    {
		Map<Colormap2D, Integer> result = Maps.newHashMap();

		Range<Double> range = getMinMax(qualityMap.values());
		
		double span = range.upperEndpoint() - range.lowerEndpoint();
        for (Colormap2D cm : qualityMap.keySet())
        {
        	double val = qualityMap.get(cm);
        	double frac = (val - range.lowerEndpoint()) / span;
        	int percent = (int)(frac * 100d);
			result.put(cm, Integer.valueOf(percent));
        }
        
        return result;
	}
    
	private static String toFilename(String name)
	{
		return name
			.replaceAll("\\.", "") 
			.replaceAll(":", "") 
			.replaceAll("\\W", "_")	// NOT a number, letter or underscore 
			+ ".png";
	}
}
