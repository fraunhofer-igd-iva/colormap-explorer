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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.misc.ErrorManager;

import views.SimpleColormapView;
import algorithms.quality.AttentionQuality;
import algorithms.quality.ColorAppearanceDivergence;
import algorithms.quality.ColorDivergenceVariance;
import algorithms.quality.ColorDynamicDistBlack;
import algorithms.quality.ColorDynamicDistWhite;
import algorithms.quality.ColorExploitation;
import algorithms.quality.ColormapQuality;
import algorithms.quality.JndRegionSize;
import algorithms.sampling.CircularSampling;
import algorithms.sampling.EvenDistributedDistancePoints;
import algorithms.sampling.GridSampling;
import algorithms.sampling.SamplingStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.KnownColormap;

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
	
	public static File generateTable(List<KnownColormap> colormaps, File outputFolder) throws IOException 
    {
    	STRawGroupDir templateDir = new STRawGroupDir("src/main/resources/latex");
        templateDir.delimiterStartChar = '$';
        templateDir.delimiterStopChar = '$';
        
        Map<KnownColormap, MetricColormap> mcms = Maps.newLinkedHashMap();

        for (KnownColormap cm : colormaps)
        {
        	String fname = cm.getName() + "_" + SimpleColormapView.ViewType.REAL.toString();
            String relativePath = "images/gen/" + toFilename(fname);

        	MetricColormap mcm = new MetricColormap(cm, relativePath);
			mcms.put(cm, mcm);
        }
        
        SamplingStrategy circSampling = new CircularSampling(50);
        SamplingStrategy rectSampling = new GridSampling(50);
        EvenDistributedDistancePoints distSampling = new EvenDistributedDistancePoints(new Random(12345), 2000);
        
        List<ColormapQuality> measures = Lists.newArrayList();
        measures.add(new ColorExploitation(circSampling, 3.0));
		measures.add(new ColorDynamicDistBlack(rectSampling));
        measures.add(new ColorDynamicDistWhite(rectSampling));
//        measures.add(new ColorDynamicWhiteContrast(rectSampling));
//        measures.add(new ColorDivergenceQuantile(0.5));
//        measures.add(new ColorDivergenceQuantile(0.1));
//        measures.add(new ColorDivergenceQuantile(0.9));
        measures.add(new JndRegionSize(circSampling));
        measures.add(new AttentionQuality(rectSampling));
        measures.add(new ColorDivergenceVariance(distSampling));
//        measures.add(new ColorDivergenceVarianceJB(distSampling));
        measures.add(new ColorAppearanceDivergence(0.05, 0.95));
//        measures.add(new ColorAppearanceDivergence(0, 1));

        for (ColormapQuality measure : measures)
        {
        	Map<KnownColormap, Double> mapQualities = computeQuality(colormaps, measure);
        	Map<KnownColormap, Integer> mapPoints = computePoints(mapQualities);
        	Map<KnownColormap, Integer> mapRanks = computeRanks(mapQualities);
        	
        
            for (Colormap cm : colormaps)
            {
            	double quality = mapQualities.get(cm);
            	Integer points = mapPoints.get(cm);
            	Integer rank = mapRanks.get(cm);
				mcms.get(cm).addMetric(measure, quality, points);
	        }
        }

        ColorRamp colorrampQuality = new ColorRamp(new Color(20, 140, 60), new Color(220, 220, 220));
        ColorRamp colorrampMalus = new ColorRamp(new Color(191, 111, 27), new Color(220, 220, 220));
        
        ST st = templateDir.getInstanceOf("MetricTable");
		st.add("metrics", measures);
		st.add("quality", createColorDefs(colorrampQuality, "quality", 100));
		st.add("malus", createColorDefs(colorrampMalus, "malus", 100));
		st.add("colormaps", mcms.values());

        File texFile = new File(outputFolder, "metric_table.tex");
        st.write(texFile, ErrorManager.DEFAULT_ERROR_LISTENER);
        
        return texFile;
    }

	private static Map<KnownColormap, Integer> computeRanks(Map<KnownColormap, Double> mapQualities)
	{
		Map<KnownColormap, Double> sorted = sortByValue(mapQualities);
		Map<KnownColormap, Integer> result = Maps.newLinkedHashMap();
		
		int rank = 1;
		
		for (Map.Entry<KnownColormap, Double> entry : sorted.entrySet())
		{
			result.put(entry.getKey(), rank++);
		}
		
		return result;
	}

	/**
	 * Copied from 
	 * http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java/2581754#2581754
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private static List<LatexColor> createColorDefs(ColorRamp ramp, String prefix, int steps)
	{
		List<LatexColor> colors = Lists.newArrayList();
		
		for (int i=0; i<=steps; i++)
		{
			double val = 1.0 - i / (double)steps;
			Color col = ramp.getColor(val);
			
			colors.add(new LatexColor(col, prefix + i));
		}
		
		return colors;
	}
	
	private static Map<KnownColormap, Double> computeQuality(List<KnownColormap> colormaps, ColormapQuality measure)
	{
		Map<KnownColormap, Double> qualityMap = Maps.newHashMap();
        for (KnownColormap cm : colormaps)
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
			if (!quality.isInfinite() && !quality.isNaN())
			{
		    	if (quality > max)
		    		max = quality;
		    	
		    	if (quality < min)
		    		min = quality;
			}
		}
		
		return Range.closed(min, max);
	}

    private static Map<KnownColormap, Integer> computePoints(Map<KnownColormap, Double> qualityMap)
    {
		Map<KnownColormap, Integer> result = Maps.newHashMap();

		Range<Double> range = getMinMax(qualityMap.values());
		
		double span = range.upperEndpoint() - range.lowerEndpoint();
        for (KnownColormap cm : qualityMap.keySet())
        {
        	double val = qualityMap.get(cm);
        	if (Double.isInfinite(val) || Double.isNaN(val))
        	{
        		result.put(cm, 100);
        	}
        	else
        	{
	        	double frac = (val - range.lowerEndpoint()) / span;
	        	int percent = (int)(frac * 100d);
				result.put(cm, Integer.valueOf(percent));
        	}
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
