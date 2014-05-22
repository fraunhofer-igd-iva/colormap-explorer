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

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import colormaps.Colormap2D;

import com.google.common.base.Preconditions;

/**
 * The main window
 * @author Martin Steiger
 */
public class ColorMapExplorer extends JFrame
{
	private static final long serialVersionUID = 339070765825907575L;

	/**
	 * @param colorMaps a list of colormaps
	 */
	public ColorMapExplorer(List<Colormap2D> colorMaps) 
	{
		super("ColorMap Explorer");
		
		Preconditions.checkArgument(!colorMaps.isEmpty());
		
		final ConfigPanel configPane = new ConfigPanel(colorMaps);
		
		final DecomposedViewPanel viewPanel = new DecomposedViewPanel();
		final PointsExampleViewPanel pointsExampleView = new PointsExampleViewPanel(); 
		final OverlayExampleViewPanel overlayExampleView = new OverlayExampleViewPanel(); 
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Decomposed Colormap", viewPanel);
		tabPane.add("Points Example View", pointsExampleView);
		tabPane.add("Overlay Example View", overlayExampleView);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, configPane, tabPane);
		splitPane.setDividerLocation(250);

		// Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(50, 50);
		configPane.setMinimumSize(minimumSize);
		tabPane.setMinimumSize(minimumSize);
		
		getContentPane().add(splitPane);
	}
}
