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

package main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colormaps.Colormap2D;

/**
 * Entry point for the application
 * @author Martin Steiger
 */
public final class MyMain
{
	private static final Logger logger = LoggerFactory.getLogger(MyMain.class);
	
	private MyMain()
	{
		// private
	}
	
	/**
	 * @param args (ignored)
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)	// admittedly, this is horrible, but we don't really care about l&f
		{
			logger.error("Cannot set look & feel", e);
		}
		
		List<Colormap2D> colorMaps = getColormaps("colormaps.impl");
		
		JColorMapExplorer frame = new JColorMapExplorer(colorMaps);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(728, 600);
		frame.setLocationByPlatform(true);

		frame.setVisible(true);
	}

	private static List<Colormap2D> getColormaps(String packageName)
	{
		Set<URL> urls = ClasspathHelper.forPackage(packageName);
//		Set<URL> urls = ClasspathHelper.forJavaClassPath();
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.setUrls(urls);
		Reflections ref = new Reflections(config);
		Set<Class<? extends Colormap2D>> colormapClasses = ref.getSubTypesOf(Colormap2D.class);

		List<Colormap2D> colorMaps = new ArrayList<>(colormapClasses.size());
		
		for (Class<? extends Colormap2D> clazz : colormapClasses) {
			try
			{
				if (clazz.getName().startsWith(packageName))
				{
					colorMaps.add(clazz.newInstance());
				}
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				logger.error("Could not run default constructor of " + clazz.getName(), e);
			}
		}
		
		Collections.sort(colorMaps, new Comparator<Colormap2D>()
		{
			@Override
			public int compare(Colormap2D o1, Colormap2D o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return colorMaps;
	}
}
