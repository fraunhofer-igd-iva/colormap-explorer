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
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.misc.ErrorManager;

import colormaps.Colormap2D;
import colormaps.impl.BCR37;
import colormaps.impl.Himberg98RGB;

import com.google.common.collect.Lists;

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
		List<Colormap2D> asList = Lists.newArrayList();
		asList.add(new BCR37());
		asList.add(new Himberg98RGB());
		File output = new File(System.getProperty("user.home"),  "colormaps");
		output.mkdirs();
		generateTable(asList, output);
	}
	
    public static void generateTable(List<Colormap2D> colormaps, File outputFolder) throws IOException 
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
        	File imgFile = new File(imageFolder, toFilename(cm.getName()));
        	String relativePath = "images/" + imgFile.getName();	// TODO: make it pretty
			lcm.addImage(relativePath);
        	saveToFile(cm, imgFile);
        }
        
        ST st = templateDir.getInstanceOf("Table");
		st.add("colormap", lcms);

        File fname = new File(outputFolder, "colormaps.tex");
        st.write(fname, ErrorManager.DEFAULT_ERROR_LISTENER);
        System.out.println("Created file " + fname);
    }
    
	private static String toFilename(String name)
	{
		return name.replaceAll(" ", "") + ".png";
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
