// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package colormaps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.KnownColormap;

/**
 *
 */
public class DumpToFolder
{
    public static void main(String[] args) throws IOException
    {
        File folder = new File("D:\\colormaps");
        folder.mkdirs();
        int imgSize = 1024;
        String format = "png";

        ServiceLoader<KnownColormap> loader = ServiceLoader.load(KnownColormap.class);
        for (KnownColormap map : loader)
        {
            Class<? extends KnownColormap> clazz = map.getClass();

            if (clazz.isAnnotationPresent(Deprecated.class))
            {
                System.out.println("Skipping deprecated implementation: " + map.getName());
            }
            else
            {
                System.out.println("Using implementation: " + map.getName());
                renderToImage(map, new File(folder, map.getName() + "." + format), format, imgSize, imgSize);
            }
        }

    }

    private static void renderToImage(Colormap cm, File file, String format, int width, int height) throws IOException
    {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gi = bi.createGraphics();

        float maxY = height - 1;
        float maxX = width - 1;

        WritableRaster raster = bi.getRaster();

        for (int y = 0; y < height; y++)
        {
            float my = y / maxY;
            for (int x = 0; x < width; x++)
            {
                float mx = x / maxX;
                Color color = cm.getColor(mx, my);

                raster.setSample(x, y, 0, color.getRed());
                raster.setSample(x, y, 1, color.getGreen());
                raster.setSample(x, y, 2, color.getBlue());
            }
        }

        gi.dispose();

        ImageIO.write(bi, format, file);
    }
}
