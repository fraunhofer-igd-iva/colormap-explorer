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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.misc.ErrorManager;

import views.SimpleColormapView;
import views.SimpleColormapView.ViewType;

import com.google.common.collect.Lists;

import de.fhg.igd.iva.colormaps.KnownColormap;

/**
 * Generates LaTeX table output for a list of decomposed colormaps 
 * @author Martin Steiger
 */
public final class LatexTableDecomp
{
	private static final Logger logger = LoggerFactory.getLogger(LatexTableDecomp.class);

	private LatexTableDecomp()
	{
		// private
	}
	
	public static File generateTable(List<KnownColormap> colormaps, File outputFolder) throws IOException 
    {
    	STRawGroupDir templateDir = new STRawGroupDir("src/main/resources/latex");
        templateDir.delimiterStartChar = '$';
        templateDir.delimiterStopChar = '$';

        String imgFolderName = "images/gen";

        List<LatexColormap> lcms = Lists.newArrayList();

		File imageFolder = new File(outputFolder, imgFolderName);
        imageFolder.mkdir();
        
        for (KnownColormap cm : colormaps)
        {
        	LatexColormap lcm = new LatexColormap(cm);
			lcms.add(lcm);
			
			for (ViewType viewType : ViewType.values())
			{
	        	String fname = cm.getName() + "_" + viewType.toString();
				File imgFile = new File(imageFolder, toFilename(fname));
	        	
				if (!imgFile.exists())
				{
					SimpleColormapView filtered = new SimpleColormapView(cm, viewType);
					saveToFile(filtered, imgFile);
				}

	        	String relativePath = imgFolderName + "/" + imgFile.getName();
				lcm.addImage(relativePath);
			} 
			
        }
        
        ST st = templateDir.getInstanceOf("Table");
        st.add("columns", ViewType.values());
		st.add("colormap", lcms);

        File texFile = new File(outputFolder, "colormaps.tex");
        st.write(texFile, ErrorManager.DEFAULT_ERROR_LISTENER);
        logger.info("Created file " + texFile);
        
        return texFile;
    }
    
	private static String toFilename(String name)
	{
		return name
			.replaceAll("\\.", "") 
			.replaceAll(":", "") 
			.replaceAll("\\W", "_")	// NOT a number, letter or underscore 
			+ ".png";
	}

	private static void saveToFile(SimpleColormapView filtered, File file) throws IOException
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
				Color color = filtered.getColor(x / fw, y / fh);
				int rgb = color.getRGB();
				img.setRGB(x, y, rgb);
			}
		}
		
		ImageIO.write(img, "png", file);
		logger.debug("Created image " + file);
    }
}
