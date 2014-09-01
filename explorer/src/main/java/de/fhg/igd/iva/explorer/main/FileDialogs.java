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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Some helper methods for different file dialogs
 * @author Martin Steiger
 */
public class FileDialogs
{
	private static final IIORegistry REGISTRY = IIORegistry.getDefaultInstance();

	private static class AllImagesFileFilter extends FileFilter
	{
		private String desc;
		private String[] sufs;

		public AllImagesFileFilter(String desc, String[] sufs)
		{
			this.desc = desc;
			this.sufs = Arrays.copyOf(sufs, sufs.length);
			Arrays.sort(this.sufs);
		}

		@Override
		public String getDescription()
		{
			String sufStr = Joiner.on("; *.").join(sufs);
			return desc + " (*." + sufStr + ")";
		}

		@Override
		public boolean accept(File f)
		{
			String name = f.getName().toLowerCase();

			for (String suf : sufs) {
				if (name.endsWith(suf.toLowerCase()))
					return true;
			}

			return false;
		}
	}

	/**
	 * Compares two {@link FileFilter} instances based on their description text
	 */
	private static final Comparator<FileFilter> COMPARATOR = new Comparator<FileFilter>()
	{
		@Override
		public int compare(FileFilter o1, FileFilter o2)
		{
			return o1.getDescription().compareTo(o2.getDescription());
		}
	};

	public static JFileChooser createOpenImageDialog()
	{
		Iterator<ImageReaderSpi> provs = REGISTRY.getServiceProviders(ImageReaderSpi.class, true);

		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new AllImagesFileFilter("All images", ImageIO.getReaderFileSuffixes()));
		updateFileChooser(fc, provs);
		return fc;
	}

	public static JFileChooser createSaveImageDialog()
	{
		Iterator<ImageWriterSpi> provs = REGISTRY.getServiceProviders(ImageWriterSpi.class, true);

		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new AllImagesFileFilter("All images", ImageIO.getWriterFileSuffixes()));
		updateFileChooser(fc, provs);
		return fc;
	}

	public static void updateFileChooser(JFileChooser fc, Iterator<? extends ImageReaderWriterSpi> providers)
	{
		List<FileFilter> filters = Lists.newArrayList();

		// we add them to a list to be able to sort them later
		while (providers.hasNext())
		{
			filters.add(new ImageFileFilter(providers.next()));
		}

		Collections.sort(filters, COMPARATOR);

		// we finally add them to the file chooser
		for (FileFilter filter : filters)
		{
			fc.addChoosableFileFilter(filter);
		}
	}
}
