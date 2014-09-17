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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.colormaps.KnownColormap;
import de.fhg.igd.iva.explorer.events.ColormapSelectionEvent;
import de.fhg.igd.iva.explorer.events.MyEventBus;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class OverviewPanel extends JPanel
{
	private final JList<KnownColormap> list;

	public OverviewPanel(List<KnownColormap> colormaps)
	{
		list = new JList<KnownColormap>(colormaps.toArray(new KnownColormap[0]));

		list.setCellRenderer(new ListCellRenderer<KnownColormap>()
		{
			private Map<KnownColormap, JComponent> renderers = Maps.newHashMap();

			@Override
			public Component getListCellRendererComponent(JList<? extends KnownColormap> list, KnownColormap cm, int index, boolean isSelected, boolean cellHasFocus)
			{
				JComponent parent = renderers.get(cm);

				if (parent == null)
				{
					parent = new JPanel(new BorderLayout(5, 5));
					JLabel label = new JLabel(cm.getName());
					ColormapPanel cmPanel = new ColormapPanel(cm, 160);
					cmPanel.setPreferredSize(new Dimension(160, 160));
					parent.add(label, BorderLayout.NORTH);
					parent.add(cmPanel, BorderLayout.CENTER);
					parent.setBorder(BorderFactory.createEtchedBorder());
					renderers.put(cm, parent);
				}

				parent.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

				return parent;
			}
		});
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(0);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				KnownColormap colormap = list.getSelectedValue();
				MyEventBus.getInstance().post(new ColormapSelectionEvent(colormap));
			}
		});

		setLayout(new BorderLayout());
		add(new JScrollPane(list), BorderLayout.CENTER);

		MyEventBus.getInstance().register(this);
	}

	@Subscribe
	public void onSelect(ColormapSelectionEvent event)
	{
		KnownColormap colormap = event.getSelection();
		if (!Objects.equals(colormap, list.getSelectedValue()))
		{
			ListModel<KnownColormap> model = list.getModel();
			for (int i = 0; i < model.getSize(); i++)
			{
				if (model.getElementAt(i).equals(colormap))
				{
					list.setSelectedIndex(i);
					break;
				}
			}
		}
	}
}

