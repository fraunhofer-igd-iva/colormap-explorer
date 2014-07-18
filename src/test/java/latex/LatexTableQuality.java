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
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.misc.ErrorManager;

import algorithms.JndRegionComputer;
import colormaps.Colormap2D;
import colormaps.transformed.SimpleFilteredColormap2D.ViewType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
        
        List<MetricColormap> mcms = Lists.newArrayList();
        
        Map<Colormap2D, Integer> jndQuality = computeJndMetrics(colormaps);
        
        for (Colormap2D cm : colormaps)
        {
        	String fname = cm.getName() + "_" + ViewType.REAL.toString();
            String relativePath = "images/" + toFilename(fname);

        	MetricColormap mcm = new MetricColormap(cm, relativePath);
			mcms.add(mcm);
			
			mcm.addMetric(Metric.JND_POINT_COUNT, jndQuality.get(cm));
        }

        ST st = templateDir.getInstanceOf("MetricTable");
        st.add("metrics", Metric.values());
		st.add("colors", createColorDefs());
		st.add("colormaps", mcms);

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
	
	private static Map<Colormap2D, Integer> computeJndMetrics(List<Colormap2D> colormaps)
	{
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		Map<Colormap2D, Integer> jndPointCount = Maps.newHashMap();
        for (Colormap2D cm : colormaps)
        {
        	JndRegionComputer jndRegionComp = new JndRegionComputer(cm, 5.0);
        	int cnt = jndRegionComp.getPoints().size();
        	
        	if (cnt > max)
        		max = cnt;
        	
        	if (cnt < min)
        		min = cnt;
        	
        	jndPointCount.put(cm, Integer.valueOf(cnt));
        }
        
		Map<Colormap2D, Integer> result = Maps.newHashMap();

		double range = max - min;
        for (Colormap2D cm : colormaps)
        {
        	int val = jndPointCount.get(cm);
        	double quality = (val - min) / range;
        	int percent = (int)(quality * 100d);
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
