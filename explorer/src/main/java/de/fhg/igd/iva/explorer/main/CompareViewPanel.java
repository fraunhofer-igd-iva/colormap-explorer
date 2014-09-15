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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import algorithms.quality.ColormapQuality;

import com.google.common.collect.Table;

import de.fhg.igd.iva.colormaps.CachedColormap;
import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.ImageBasedColormap;
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

//		statsLabel = new JLabel();
//		statsLabel.setBorder(BorderFactory.createTitledBorder("Statistics"));
//		statsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		statsBars = new JPanel(new GridBagLayout());
		statsPanel.add(statsBars, BorderLayout.NORTH);
//		statsPanel.add(statsLabel, BorderLayout.SOUTH);
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
				g.drawImage(getCachedColormap().getImage(), 0, 0, size, size, null);
			}
		};
		cmView.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(cmView, BorderLayout.CENTER);


		add(mapsCombo, BorderLayout.NORTH);
	}

	protected ImageBasedColormap getCachedColormap()
	{
		return cachedColormap;
	}

	protected void updateSelection()
	{
		KnownColormap colormap = (KnownColormap) mapsCombo.getSelectedItem();
		cachedColormap = new CachedColormap(colormap, 512, 512);

		statsBars.removeAll();

		Map<ColormapQuality, Double> row = table.row(colormap);

		Insets insets = new Insets(0, 0, 0, 0);
		Insets insets5 = new Insets(0, 5, 0, 0);

		GridBagConstraints gbcName = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, insets, 0, 0);
		GridBagConstraints gbcQual = new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, insets5, 0, 0);
		GridBagConstraints gbcRank = new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, insets5, 0, 0);
		GridBagConstraints gcbStat = new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, insets5, 0, 0);

		statsBars.add(new JLabel("Name"), gbcName);
		statsBars.add(new JLabel("Score"), gbcQual);
		statsBars.add(new JLabel("Rank"), gbcRank);

		GridBagConstraints gbcSpace = new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, insets, 0, 0);
		JSeparator spacing = new JSeparator(SwingConstants.HORIZONTAL);
		statsBars.add(spacing, gbcSpace);

		int rowIdx = 2;
		for (ColormapQuality metric : row.keySet())
		{
			double quality = row.get(metric);
			DescriptiveStatistics stats = computeStats(metric);
			int rank = Arrays.binarySearch(stats.getSortedValues(), quality) + 1;

			gbcName = (GridBagConstraints) gbcName.clone();
			gbcQual = (GridBagConstraints) gbcQual.clone();
			gbcRank = (GridBagConstraints) gbcRank.clone();
			gcbStat = (GridBagConstraints) gcbStat.clone();

			gbcName.gridy = rowIdx;
			gbcQual.gridy = rowIdx;
			gbcRank.gridy = rowIdx;
			gcbStat.gridy = rowIdx;

			statsBars.add(new JLabel(metric.getName()), gbcName);
			statsBars.add(new JLabel(String.format("%.2f", quality)), gbcQual);
			statsBars.add(new JLabel(Integer.toString(rank)), gbcRank);
			statsBars.add(new JStatBar(metric, stats, quality), gcbStat);
			rowIdx++;
		}
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
