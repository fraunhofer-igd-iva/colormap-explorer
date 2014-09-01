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
package de.fhg.igd.iva.explorer.plot;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * Organizes several {@link ColormapPlotPanel} instances in a panel
 */
public class ColormapPlotter extends JPanel
{
	private static final long serialVersionUID = 3624032186318966794L;

	private final Collection<ColormapPlotPanel> panels = new LinkedHashSet<>();

	public ColormapPlotter()
    {
        JPanel plotsContainer = new JPanel(new GridLayout(2,2));

        ColormapPlotPanel colormapPlotPanelRgb = new ColormapPlotPanel();
        colormapPlotPanelRgb.setBorder(BorderFactory.createTitledBorder("RGB"));
        colormapPlotPanelRgb.setLabels("R", "G", "B");
        colormapPlotPanelRgb.setLabelDigits(1);
        plotsContainer.add(colormapPlotPanelRgb);
        panels.add(colormapPlotPanelRgb);

        final ColormapPlotPanel colormapPlotPanelXyz = new ColormapPlotPanel();
        colormapPlotPanelXyz.setBorder(BorderFactory.createTitledBorder("XYZ"));
        colormapPlotPanelXyz.setCoordinateConverter(CoordinateConverters.createXyz());
        colormapPlotPanelXyz.setBounds(0, 100, 0, 100, 0, 100);
        colormapPlotPanelXyz.setLabels("X", "Y", "Z");
        colormapPlotPanelXyz.setLabelDigits(0);
        plotsContainer.add(colormapPlotPanelXyz);
        panels.add(colormapPlotPanelXyz);

        final ColormapPlotPanel colormapPlotPanelHsb = new ColormapPlotPanel();
        colormapPlotPanelHsb.setBorder(BorderFactory.createTitledBorder("HSB"));
        colormapPlotPanelHsb.setCoordinateConverter(CoordinateConverters.createHsb());
        colormapPlotPanelHsb.setLabels("HSX", "HSY", "B");
        colormapPlotPanelHsb.setLabelDigits(1);
        plotsContainer.add(colormapPlotPanelHsb);
        panels.add(colormapPlotPanelHsb);

//        final ColormapPlotPanel colormapPlotPanelLabSM = new ColormapPlotPanel();
//        colormapPlotPanelLabSM.setBorder(BorderFactory.createTitledBorder("LabSM"));
//        colormapPlotPanelLabSM.setCoordinateConverter(CoordinateConverters.createLabSM());
//        colormapPlotPanelLabSM.setBounds(0, 100, -100, 100, -100, 100);
//        plotsContainer.add(colormapPlotPanelLabSM);
//        panels.add(colormapPlotPanelLabSM);

        final ColormapPlotPanel colormapPlotPanelLab = new ColormapPlotPanel();
        colormapPlotPanelLab.setBorder(BorderFactory.createTitledBorder("Lab"));
        colormapPlotPanelLab.setCoordinateConverter(CoordinateConverters.createLab());
        colormapPlotPanelLab.setBounds(0, 100, -100, 100, -100, 100);
        colormapPlotPanelLab.setLabels("L", "a", "b");
        colormapPlotPanelLab.setLabelDigits(0);
        plotsContainer.add(colormapPlotPanelLab);
        panels.add(colormapPlotPanelLab);

        setLayout(new BorderLayout());
        this.add(plotsContainer, BorderLayout.CENTER);
    }

	/**
	 * @param selection
	 */
	public void setColormap(Colormap selection)
	{
		for (ColormapPlotPanel panel : panels)
		{
//			colormap = new CachedColormap(event.getSelection(), 512, 512);
			panel.setColormap(selection);
		}

	}
}
