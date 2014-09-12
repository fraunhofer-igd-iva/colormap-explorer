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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.quality.AttentionQuality;
import algorithms.quality.ColorAppearanceDivergence;
import algorithms.quality.ColorDivergenceVariance;
import algorithms.quality.ColorDynamicDistBlack;
import algorithms.quality.ColorDynamicDistWhite;
import algorithms.quality.ColorExploitation;
import algorithms.quality.ColormapQuality;
import algorithms.sampling.CircularSampling;
import algorithms.sampling.EvenDistributedDistancePoints;
import algorithms.sampling.GridSampling;
import algorithms.sampling.SamplingStrategy;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

import de.fhg.igd.iva.colormaps.CachedColormap;
import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.KnownColormap;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CompareViewPanel extends JPanel
{
	private static final long serialVersionUID = -262177345702993230L;
	private JComboBox<KnownColormap> mapsCombo;
	private JLabel statsLabel;
	private CachedColormap cachedColormap;
	private JPanel statsBars;
	private Table<KnownColormap, ColormapQuality, Double> table;

	public CompareViewPanel(Table<KnownColormap, ColormapQuality, Double> info)
	{
		this.table = info;

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout());

		JPanel statsPanel = new JPanel(new BorderLayout());

		statsLabel = new JLabel();
		statsLabel.setBorder(BorderFactory.createTitledBorder("Statistics"));
//		statsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		statsBars = new JPanel(new GridLayout(0, 2, 5, 5));
		statsPanel.add(statsBars, BorderLayout.CENTER);
		statsPanel.add(statsLabel, BorderLayout.SOUTH);
		add(statsPanel, BorderLayout.SOUTH);

		mapsCombo = new JComboBox<KnownColormap>(table.rowKeySet().toArray(new KnownColormap[0]));
		mapsCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateSelection();
			}

		});
		mapsCombo.setSelectedIndex(0);

		JComponent cmView = new JComponent()
		{
			private static final long serialVersionUID = 240761518096949199L;

			@Override
			protected void paintComponent(Graphics g1)
			{
				int size = Math.min(getWidth(), getHeight());

				Graphics2D g = (Graphics2D) g1;
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(cachedColormap.getImage(), 0, 0, size, size, null);
			}
		};
		cmView.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(cmView, BorderLayout.CENTER);


		add(mapsCombo, BorderLayout.NORTH);
	}

	protected void updateSelection()
	{
		KnownColormap colormap = (KnownColormap) mapsCombo.getSelectedItem();
		cachedColormap = new CachedColormap(colormap, 512, 512);

		statsBars.removeAll();

		Map<ColormapQuality, Double> row = table.row(colormap);

		for (ColormapQuality metric : row.keySet())
		{
			statsBars.add(new JLabel(metric.getName()));
			statsBars.add(new JStatBar(metric, computeStats(metric), row.get(metric)));
		}

		String statsText = formatMap(row);
		statsLabel.setText("<html>" + statsText + "</html>");
	}

	private String formatMap(Map<ColormapQuality, Double> qualityMap)
	{
		List<String> results = Lists.newArrayList();
		for (ColormapQuality metric : qualityMap.keySet())
		{
			results.add(String.format("%s: %.1f", metric.getName(), qualityMap.get(metric)));
		}

		String stats = Joiner.on("<br/>").join(results);
		return stats;
	}

	private DescriptiveStatistics computeStats(ColormapQuality metric)
	{
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (Colormap cm : table.rowKeySet())
		{
			double quality = table.get(cm, metric);
			stats.addValue(quality);
		}

		return stats;
	}

}
