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
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import main.ColorMapFinder;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.misc.ErrorManager;

import algorithms.JndRegionComputer;
import colormaps.Colormap2D;
import colormaps.impl.BCP37;
import colormaps.impl.FourCornersAnchor;
import colormaps.impl.Himberg98;
import colormaps.impl.TeulingFig2;
import colormaps.transformed.SimpleFilteredColormap2D;
import colormaps.transformed.SimpleFilteredColormap2D.ViewType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Generates LaTeX table output for a list of colormaps 
 * @author Martin Steiger
 */
public final class LatexGen
{
	private LatexGen()
	{
		// private
	}
	
	public static void main(String[] args) throws IOException
	{
//		List<Colormap2D> colorMaps = Lists.newArrayList();
//		colorMaps.add(new BCP37());
//		colorMaps.add(new Himberg98());
//		colorMaps.add(new FourCornersAnchor());
//		colorMaps.add(new TeulingFig2());
		
		List<Colormap2D> colorMaps = ColorMapFinder.findInPackage("colormaps.impl");
		
		File output = new File(System.getProperty("user.home"),  "colormaps");
		output.mkdirs();
//		generateTable(colorMaps, output);
		
		generateMetricTable(colorMaps, output);
		
//		generateBibTex(colorMaps, output);
		
//		compileLaTeX(new File(output, "colormaps.tex"));
		compileLaTeX(new File(output, "metric_table.tex"));
	}
	
	private static void generateBibTex(List<Colormap2D> colormaps, File folder)
	{
		File bibtex = new File(folder, "colormaps.bib");
		
		try (PrintWriter out = new PrintWriter(bibtex))
		{
			for (Colormap2D cm : colormaps)
	        {
				for (String ref : cm.getReferences())
				{
					out.println(ref);
					out.println();
				}
	        }
			
			System.out.println("Created file " + bibtex);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Could not write to " + bibtex);
		}
	}

	private static void compileLaTeX(File texFile)
	{
		String compiler = "C:\\Program Files (x86)\\MiKTeX 2.9\\miktex\\bin\\pdflatex.exe";
		ProcessBuilder pb = new ProcessBuilder(compiler, "-max-print-line=220", "-synctex=-1", "-interaction=nonstopmode", texFile.getAbsolutePath());
		File workingDir = texFile.getParentFile();
		System.out.println("Working directory: " + workingDir);
		pb.directory(workingDir);
		
		try
		{
			Process process = pb.start();
			BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			 
            String line=null;

            while((line=stdOutput.readLine()) != null) {
                System.out.println(line);
            }

            while((line=stdError.readLine()) != null) {
                System.err.println(line);
            }

			int exitCode = process.waitFor();
			System.out.println("Compiler has terminated with " + exitCode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		
	}

	private static void generateTable(List<Colormap2D> colormaps, File outputFolder) throws IOException 
    {
    	STRawGroupDir templateDir = new STRawGroupDir("src/main/resources");
        templateDir.delimiterStartChar = '$';
        templateDir.delimiterStopChar = '$';
        
        List<LatexColormap> lcms = Lists.newArrayList();
        
        File imageFolder = new File(outputFolder, "images");
        imageFolder.mkdir();
        
        for (Colormap2D cm : colormaps)
        {
        	LatexColormap lcm = new LatexColormap(cm);
			lcms.add(lcm);
			
			for (ViewType viewType : ViewType.values())
			{
	        	String fname = cm.getName() + "_" + viewType.toString();
				File imgFile = new File(imageFolder, toFilename(fname));
	        	
				if (!imgFile.exists())
				{
					SimpleFilteredColormap2D filtered = new SimpleFilteredColormap2D(cm, viewType);
					saveToFile(filtered, imgFile);
				}

	        	String relativePath = "images/" + imgFile.getName();	// TODO: make it pretty
				lcm.addImage(relativePath);
			} 
			
        }
        
        ST st = templateDir.getInstanceOf("Table");
        st.add("columns", ViewType.values());
		st.add("colormap", lcms);

        File fname = new File(outputFolder, "colormaps.tex");
        st.write(fname, ErrorManager.DEFAULT_ERROR_LISTENER);
        System.out.println("Created file " + fname);
    }
    

	private static void generateMetricTable(List<Colormap2D> colormaps, File outputFolder) throws IOException 
    {
    	STRawGroupDir templateDir = new STRawGroupDir("src/main/resources");
        templateDir.delimiterStartChar = '$';
        templateDir.delimiterStopChar = '$';
        
        List<MetricColormap> mcms = Lists.newArrayList();
        
        Map<Colormap2D, Integer> jndQuality = computeJndMetrics(colormaps);
        
        for (Colormap2D cm : colormaps)
        {
        	String fname = cm.getName() + "_" + ViewType.REAL.toString();
        	String relativePath = "images/" + toFilename(fname);	// TODO: make it pretty

        	MetricColormap mcm = new MetricColormap(cm, relativePath);
			mcms.add(mcm);
			
			mcm.addMetric(Metric.JND_POINT_COUNT, jndQuality.get(cm));
        }

		List<LatexColor> colors = Lists.newArrayList();
		
		for (int i=0; i<=20; i++)
		{
			float red = 1f - i / 20f;
			float green = i / 20f;
			float blue = 0.1f;
			
			colors.add(new LatexColor(new Color(red, green, blue), "quality" + i));
		}

        ST st = templateDir.getInstanceOf("MetricTable");
        st.add("metrics", Metric.values());
		st.add("colors", colors);
		st.add("colormaps", mcms);

        File fname = new File(outputFolder, "metric_table.tex");
        st.write(fname, ErrorManager.DEFAULT_ERROR_LISTENER);
        System.out.println("Created file " + fname);
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
			.replaceAll(" ", "_")
			.replaceAll("\\.", "") 
			.replaceAll(":", "") 
			+ ".png";
	}

	private static void saveToFile(Colormap2D map, File file) throws IOException
    {
    	int width = 128;
    	int height = 128;
		float fw = width - 1;		// width-1 -> 1.0
		float fh = height - 1;		// height-1 -> 1.0
		BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
    		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				Color color = map.getColor(x / fw, y / fh);
				int rgb = color.getRGB();
				img.setRGB(x, y, rgb);
			}
		}
		
		ImageIO.write(img, "png", file);
		System.out.println("Created image " + file);
    }
}
