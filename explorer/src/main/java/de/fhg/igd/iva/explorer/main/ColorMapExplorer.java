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

package de.fhg.igd.iva.explorer.main;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import version.GitVersion;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.fhg.igd.iva.colormaps.KnownColormap;
import de.fhg.igd.iva.colormaps.impl.ConstantColormap;
import de.fhg.igd.iva.explorer.plot.ColormapPlotterPanel;

/**
 * The main window, also the entry point for the application.
 * @author Martin Steiger
 */
public class ColorMapExplorer extends JFrame
{
	private static final Logger logger = LoggerFactory.getLogger(ColorMapExplorer.class);

	private static final long serialVersionUID = 339070765825907575L;

	/**
	 * @param colorMaps a list of colormaps
	 * @param database the BibTeX database
	 */
	public ColorMapExplorer(List<KnownColormap> colorMaps, BibTeXDatabase database)
	{
		super("ColorMap Explorer - " + GitVersion.getVersion());

		Preconditions.checkArgument(!colorMaps.isEmpty());

		final ConfigPanel configPane = new ConfigPanel(colorMaps, database);

		final DecomposedViewPanel viewPanel = new DecomposedViewPanel();
		final PointsExampleViewPanel pointsExampleView = new PointsExampleViewPanel();
		final OverlayExampleViewPanel overlayExampleView = new OverlayExampleViewPanel();
		final AnalysisPanel analysisPanel = new AnalysisPanel();
		final JndViewPanel jndViewPanel = new JndViewPanel();
		final ColormapPlotterPanel plotterPanel = new ColormapPlotterPanel();

		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Decomposed Colormap", viewPanel);
		tabPane.add("Points Example View", pointsExampleView);
		tabPane.add("Overlay Example View", overlayExampleView);
		tabPane.add("Analysis View", analysisPanel);
		tabPane.add("JND View", jndViewPanel);
		tabPane.add("3D View", plotterPanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, configPane, tabPane);
		splitPane.setDividerLocation(250);

		// Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(50, 50);
		configPane.setMinimumSize(minimumSize);
		tabPane.setMinimumSize(minimumSize);

		getContentPane().add(splitPane);
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

		List<KnownColormap> colorMaps = discoverColormaps();

		BibTeXDatabase database = new BibTeXDatabase();
		try (InputStream bibtex = ColorMapExplorer.class.getResourceAsStream("/latex/colorBib.bib");
			 InputStreamReader reader = new InputStreamReader(bibtex, Charsets.ISO_8859_1))
		{
			BibTeXParser bibtexParser = new BibTeXParser();
			database = bibtexParser.parse(reader);
		}
		catch (IOException e)
		{
			logger.error("Could not open bibtex file", e);
		}
		catch (TokenMgrException | ParseException e)
		{
			logger.error("Could not parse bibtex file", e);
		}

		ColorMapExplorer frame = new ColorMapExplorer(colorMaps, database);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1160, 855);
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
	}

	private static List<KnownColormap> discoverColormaps()
	{
		ServiceLoader<KnownColormap> loader = ServiceLoader.load(KnownColormap.class);
		List<KnownColormap> colorMaps = Lists.newArrayList();

		for (KnownColormap map : loader)
		{
			Class<? extends KnownColormap> clazz = map.getClass();

			if (clazz.isAnnotationPresent(Deprecated.class))
			{
				logger.info("Skipping deprecated implementation {}", clazz);
			}
			else
			{
				logger.debug("Discovered implementation {}", clazz);
				colorMaps.add(map);
			}
		}

		if (colorMaps.isEmpty())
		{
			logger.warn("No colormaps were discovered - using default");
			colorMaps.add(new ConstantColormap(Color.GRAY));
		}
		return colorMaps;
	}
}
