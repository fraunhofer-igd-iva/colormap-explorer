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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tiling.Tile;
import tiling.TileModel;
import algorithms.quality.ColorDivergenceQuantile;
import algorithms.quality.ColorDynamicBrightest;
import algorithms.quality.ColorDynamicDarkest;
import algorithms.quality.ColorDynamicDistBlack;
import algorithms.quality.ColorDynamicDistWhite;
import algorithms.quality.ColorDynamicWhiteContrast;
import algorithms.quality.ColormapQuality;
import algorithms.sampling.GridSampling;
import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;
import colorspaces.CIELABLch;
import colorspaces.RGB;
import colorspaces.XYZ;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CIECAM02;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;

import events.ColormapSelectionEvent;
import events.MyEventBus;
import events.TileSelectionEvent;

/**
 * The config and info panel at the left
 * @author Martin Steiger
 */
public class ConfigPanel extends JPanel
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigPanel.class);
	
	private static final long serialVersionUID = -3147864756762616815L;

	private final JPanel tileInfoPanel;

	private final BibTeXDatabase database;

	/**
	 * @param colorMaps the (sorted) list of all available color maps
	 * @param database the BibTeX database
	 */
	public ConfigPanel(List<Colormap2D> colorMaps, final BibTeXDatabase database)
	{
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		this.database = database;
		
		final JComboBox<Colormap2D> mapsCombo = new JComboBox<Colormap2D>(colorMaps.toArray(new Colormap2D[0]));
		
		final JPanel cmPanel = new JPanel(new BorderLayout(5, 5));
		cmPanel.add(new JLabel("Colormap"), BorderLayout.NORTH);
		cmPanel.add(mapsCombo, BorderLayout.CENTER);
		
		JPanel cmInfoPanel = new JPanel(new BorderLayout(5, 5));
		final JLabel descLabel = new JLabel();
		descLabel.setBorder(BorderFactory.createTitledBorder("Description"));
		final JLabel refsLabel = new JLabel();
		refsLabel.setBorder(BorderFactory.createTitledBorder("References"));
		
		final JLabel statsLabel = new JLabel();
		statsLabel.setBorder(BorderFactory.createTitledBorder("Statistics"));
		
		cmInfoPanel.add(descLabel, BorderLayout.NORTH);
		cmInfoPanel.add(statsLabel, BorderLayout.CENTER);
		cmInfoPanel.add(refsLabel, BorderLayout.SOUTH);

		cmPanel.add(cmInfoPanel, BorderLayout.SOUTH);

		add(cmPanel, BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		
		tileInfoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
		tileInfoPanel.setBorder(BorderFactory.createTitledBorder("Tile Info"));
		centerPanel.add(tileInfoPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);

		mapsCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Colormap2D colormap = (Colormap2D) mapsCombo.getSelectedItem();
				descLabel.setText("<html>" + colormap.getDescription() + "</html>");

				String refs = getBibTeX(colormap);
				refsLabel.setText("<html>" + refs + "</html>");

				String stats = getStats(colormap);
				statsLabel.setText("<html>" + stats + "</html>");
				
				MyEventBus.getInstance().post(new ColormapSelectionEvent(colormap));
				logger.debug("Selected colormap " + colormap);
			}

		});
		mapsCombo.setSelectedIndex(0);

		MyEventBus.getInstance().register(this);
	}

	private String getStats(Colormap2D colormap)
	{
		SamplingStrategy sampling = new GridSampling(50);	// 50x50 = 2500 sample points
		
		// TODO: iterate over all available quality measures
		List<ColormapQuality> measures = Lists.newArrayList();
		List<String> results = Lists.newArrayList();
		
		measures.add(new ColorDynamicBrightest(sampling));
		measures.add(new ColorDynamicDarkest(sampling));
		measures.add(new ColorDynamicDistBlack(sampling));
		measures.add(new ColorDynamicDistWhite(sampling));
		measures.add(new ColorDynamicWhiteContrast(sampling));
        measures.add(new ColorDivergenceQuantile(0.5));
        measures.add(new ColorDivergenceQuantile(0.1));
        measures.add(new ColorDivergenceQuantile(0.9));
		
		for (ColormapQuality measure : measures)
		{
			double quality = measure.getQuality(colormap);
			results.add(String.format("%s: %.1f", measure.getName(), quality));
		}

		String stats = Joiner.on("<br/>").join(results);
		return stats;
	}

	private String getBibTeX(Colormap2D colormap)
	{
		List<String> entries = Lists.newArrayList();
		for (String ref : colormap.getReferences())
		{
			BibTeXEntry entry = database.resolveEntry(new Key(ref));
			if (entry != null)
			{
				entries.add(getField(entry, BibTeXEntry.KEY_TITLE));					
				entries.add(getField(entry, BibTeXEntry.KEY_AUTHOR));					
				entries.add(getField(entry, BibTeXEntry.KEY_YEAR));					
				entries.add(getField(entry, BibTeXEntry.KEY_HOWPUBLISHED));
				entries.add("");
			}
			else
			{
				logger.warn("Invalid BibTeX reference " + ref);
			}
		}
		
		String refs = Joiner.on("<br/>").skipNulls().join(entries);
		return refs;
	}

	private String getField(BibTeXEntry entry, Key key)
	{
		Value field = entry.getField(key);
		if (field == null)
			return null;
		
		String left = key.toString();
		left = Character.toUpperCase(left.charAt(0)) + left.substring(1);
		left = "<b>" + left + ":</b> ";
		
		// remove capitalization-preserving brackets { }
		String userString = field.toUserString();
		if (userString.startsWith("{") && userString.endsWith("}"))
		{
			userString = userString.substring(1, userString.length() - 2);
		}
		
		userString = userString.replaceAll("\\\\\"\\{u\\}", "�");	// matches \"{u}
		userString = userString.replaceAll("\\{\\\\\"u\\}", "�");	// matches {\"u}

		userString = userString.replaceAll("\\\\\"\\{a\\}", "�");	// matches \"{a}
		userString = userString.replaceAll("\\{\\\\\"a\\}", "�");	// matches {\"a}

		userString = userString.replaceAll("\\\\\"\\{o\\}", "�");	// matches \"{o}
		userString = userString.replaceAll("\\{\\\\\"o\\}", "�");	// matches {\"o}

		return left + userString;
	}
	
	@Subscribe
	public void onSelect(TileSelectionEvent event)
	{
		tileInfoPanel.removeAll();

		Colormap2D colormap = event.getColormap();
		TileModel tileModel = event.getTileModel();
		
		Set<Tile> tiles = event.getSelection();

		for (Tile tile : tiles)
		{
			int x = tile.getMapX();
			int y = tile.getMapY();
			int worldX = tileModel.getWorldX(x, y);
			int worldY = tileModel.getWorldY(x, y);
			
			float mapX = (float)worldX / tileModel.getWorldWidth();
			float mapY = (float)worldY / tileModel.getWorldHeight();
			
			Color color = colormap.getColor(mapX, mapY);

			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();

			int luma = RGB.getLumaByte(color);
			double luminance = XYZ.rgb2xyz(RGB.color2rgb(color))[1];
			float[] hsb = Color.RGBtoHSB(red, green, blue, null);
			double[] lch = new CIELABLch().fromColor(color);
			double attention = Math.sqrt(lch[0]*lch[0]+lch[1]*lch[1]);
			float[] appearanceCorrelates = getAppearance(color);
			
			addInfo(tileInfoPanel, "Relative X", String.format("%.3f", mapX));
			addInfo(tileInfoPanel, "Relative Y", String.format("%.3f", mapY));

			addInfo(tileInfoPanel, "Red", String.valueOf(red));
			addInfo(tileInfoPanel, "Green", String.valueOf(green));
			addInfo(tileInfoPanel, "Blue", String.valueOf(blue));
			
			addInfo(tileInfoPanel, "Hue", String.valueOf((int)(hsb[0] * 360)) + " �");
			addInfo(tileInfoPanel, "Saturation", String.valueOf((int)(hsb[1] * 100)) + "%");
			addInfo(tileInfoPanel, "Value", String.valueOf((int)(hsb[2] * 100)) + "%");
			addInfo(tileInfoPanel, "Luma", String.valueOf(luma));
			addInfo(tileInfoPanel, "Luminance", String.format("%.1f %%", luminance));
			addInfo(tileInfoPanel, "Lightness", String.format("%.0f", lch[0]));
			addInfo(tileInfoPanel, "Chroma", String.format("%.0f", lch[1]));
			addInfo(tileInfoPanel, "Attention", String.format("%.3f", attention/100.0));
			addInfo(tileInfoPanel, "CAM Hue", String.format("%.0f", appearanceCorrelates[CS_CIECAM02.h]));
			addInfo(tileInfoPanel, "CAM Hue composition", String.format("%.0f", appearanceCorrelates[CS_CIECAM02.H]));
			addInfo(tileInfoPanel, "CAM Colorfulness", String.format("%.0f", appearanceCorrelates[CS_CIECAM02.C]));
			addInfo(tileInfoPanel, "CAM Saturation", String.format("%.0f", appearanceCorrelates[CS_CIECAM02.s]));
			addInfo(tileInfoPanel, "CAM Lightness (J)", String.format("%.1f", appearanceCorrelates[CS_CIECAM02.J]));
			
		}
		
		tileInfoPanel.revalidate();
	}

	private float[] getAppearance(Color color) {
		PColor pColorTmp = PColor.create(CS_sRGB.instance, color.getColorComponents(new float[3]));
		float[] xyz = PColor.convert(pColorTmp, CS_CIEXYZ.instance).getComponents();
		float[] appearanceCorrelates = CS_CIECAM02.defaultInstance.fromCIEXYZ(xyz);
		return appearanceCorrelates;
	}

	private void addInfo(Container c, String label, String value)
	{
		JTextField tfX = new JTextField(value);
		tfX.setEditable(false);
		tfX.setHorizontalAlignment(SwingConstants.RIGHT);
		tfX.setBackground(Color.WHITE);
		c.add(new JLabel(label));
		c.add(tfX);
	}
}
