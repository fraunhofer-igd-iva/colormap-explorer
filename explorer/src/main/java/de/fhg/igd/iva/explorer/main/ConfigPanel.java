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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import views.ColormapView;
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import de.fhg.igd.iva.colormaps.Colormap;
import de.fhg.igd.iva.colormaps.KnownColormap;
import de.fhg.igd.iva.colormaps.FileImageColormap;
import de.fhg.igd.iva.colorspaces.CIELABLch;
import de.fhg.igd.iva.colorspaces.RGB;
import de.fhg.igd.iva.colorspaces.XYZ;
import de.fhg.igd.iva.explorer.events.ColormapSelectionEvent;
import de.fhg.igd.iva.explorer.events.MyEventBus;
import de.fhg.igd.iva.explorer.events.TileSelectionEvent;
import de.fhg.igd.iva.explorer.tiling.Tile;
import de.fhg.igd.iva.explorer.tiling.TileModel;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CIECAM02;
import de.fhg.igd.pcolor.colorspace.CS_CIEXYZ;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;

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

	private JComboBox<KnownColormap> mapsCombo;

	private JLabel descLabel;
	private JLabel refsLabel;

	private final JFileChooser fileDialog;

	/**
	 * @param colorMaps the (sorted) list of all available color maps
	 * @param database the BibTeX database
	 */
	public ConfigPanel(List<KnownColormap> colorMaps, final BibTeXDatabase database)
	{
		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.database = database;

		this.fileDialog = FileDialogs.createOpenImageDialog();

		mapsCombo = new JComboBox<KnownColormap>(colorMaps.toArray(new KnownColormap[0]));

		final JButton importButton = new JButton("Import colormap from image", loadIconImage("/icons/icon.png"));
		importButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				JRootPane parent = ConfigPanel.this.getRootPane();
				importImage(parent);
			}
		});

		final JPanel cmPanel = new JPanel(new BorderLayout(5, 5));
		cmPanel.add(mapsCombo, BorderLayout.NORTH);
		cmPanel.add(importButton, BorderLayout.CENTER);

		JPanel cmInfoPanel = new JPanel(new BorderLayout(5, 5));
		descLabel = new JLabel();
		descLabel.setBorder(BorderFactory.createTitledBorder("Description"));
		refsLabel = new JLabel();
		refsLabel.setBorder(BorderFactory.createTitledBorder("References"));

		cmInfoPanel.add(descLabel, BorderLayout.NORTH);
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
				updateSelection();
			}

		});
		mapsCombo.setSelectedIndex(0);

		MyEventBus.getInstance().register(this);
	}

	protected void updateSelection()
	{
		KnownColormap colormap = (KnownColormap) mapsCombo.getSelectedItem();
		descLabel.setText("<html>" + colormap.getDescription() + "</html>");

		String refs = getBibTeX(colormap);
		refsLabel.setText("<html>" + refs + "</html>");

		MyEventBus.getInstance().post(new ColormapSelectionEvent(colormap));
		logger.debug("Selected colormap " + colormap);
	}

	protected void importImage(Component parent)
	{
		if (fileDialog.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
		{
			KnownColormap colormap;
			try
			{
				colormap = new FileImageColormap(fileDialog.getSelectedFile());
				mapsCombo.addItem(colormap);
				mapsCombo.setSelectedItem(colormap);
			}
			catch (IOException e)
			{
				String msg = "Could not open image file\n" + e.getLocalizedMessage();
				logger.error("Could not open image file", e);
				JOptionPane.showMessageDialog(parent, msg, "An error occurred", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private Icon loadIconImage(String fname)
	{
		Icon icon = null;
		try (InputStream stream = getClass().getResourceAsStream(fname))
//		try (InputStream stream = ClassLoader.getSystemResourceAsStream(fname))
		{
			if (stream == null)
				throw new FileNotFoundException(fname);

			icon = new ImageIcon(ImageIO.read(stream));
		} catch (IOException e)
		{
			logger.error("Could not load image", e);
		}
		return icon;
	}

	private String getBibTeX(KnownColormap colormap)
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

		userString = userString.replaceAll("\\\\\"\\{u\\}", "ü");	// matches \"{u}
		userString = userString.replaceAll("\\{\\\\\"u\\}", "ü");	// matches {\"u}

		userString = userString.replaceAll("\\\\\"\\{a\\}", "ä");	// matches \"{a}
		userString = userString.replaceAll("\\{\\\\\"a\\}", "ä");	// matches {\"a}

		userString = userString.replaceAll("\\\\\"\\{o\\}", "ö");	// matches \"{o}
		userString = userString.replaceAll("\\{\\\\\"o\\}", "ö");	// matches {\"o}

		return left + userString;
	}

	@Subscribe
	public void onSelect(TileSelectionEvent event)
	{
		tileInfoPanel.removeAll();

		ColormapView colormap = event.getColormap();
		TileModel tileModel = event.getTileModel();

		Set<Tile> tiles = event.getSelection();

		for (Tile tile : tiles)
		{
			int x = tile.getMapX();
			int y = tile.getMapY();
			int worldX = tileModel.getWorldX(x, y);
			int worldY = tileModel.getWorldY(x, y);

			double mapX = (double)worldX / tileModel.getWorldWidth();
			double mapY = (double)worldY / tileModel.getWorldHeight();

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

			addInfo(tileInfoPanel, "Hue", String.valueOf((int)(hsb[0] * 360)) + " °");
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
