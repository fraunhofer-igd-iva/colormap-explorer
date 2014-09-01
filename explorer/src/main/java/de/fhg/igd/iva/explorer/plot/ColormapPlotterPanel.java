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

import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.explorer.events.ColormapSelectionEvent;
import de.fhg.igd.iva.explorer.events.MyEventBus;

/**
 * Wraps a {@link ColormapPlotter}
 */
public class ColormapPlotterPanel extends JPanel
{
	private static final long serialVersionUID = 2249539348617380135L;
	private ColormapPlotter plots;

	public ColormapPlotterPanel()
    {
        plots = new ColormapPlotter();

        setLayout(new BorderLayout());
        add(plots, BorderLayout.CENTER);

		MyEventBus.getInstance().register(this);
    }

	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		if (!this.isVisible())
			return;

		plots.setColormap(event.getSelection());
	}

	@Override
	public void setVisible(boolean aFlag)
	{
		super.setVisible(aFlag);

		if (aFlag)
		{
			ColormapSelectionEvent event = MyEventBus.getLast(ColormapSelectionEvent.class);
			onSelect(event);
			repaint();
		}
	}

}
