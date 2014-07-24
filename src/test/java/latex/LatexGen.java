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
package latex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import main.ColorMapFinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import colormaps.Colormap2D;
import colormaps.impl.BCP37;
import colormaps.impl.BaumOrangeCyan;
import colormaps.impl.FourCornersAnchor;
import colormaps.impl.LespintasPurpleGreen;
import colormaps.impl.RuppertGreenPurple;
import colormaps.impl.Steiger2014Generic;
import colormaps.impl.TeulingFig2;

import com.google.common.collect.Lists;

/**
 * Generates LaTeX table output for a list of colormaps 
 * @author Martin Steiger
 */
public final class LatexGen
{
	private static final String MIKTEX_PATH = "C:\\Program Files (x86)\\MiKTeX 2.9\\";
	private static final Logger logger = LoggerFactory.getLogger(LatexGen.class);
	
	private LatexGen()
	{
		// private
	}
	
	public static void main(String[] args) throws Exception
	{
//		List<Colormap2D> colorMaps = Lists.newArrayList();
//		colorMaps.add(new BaumOrangeCyan());
//		colorMaps.add(new BCP37());
//		colorMaps.add(new FourCornersAnchor());
//		colorMaps.add(new LespintasPurpleGreen());
//		colorMaps.add(new Steiger2014Generic());
//		colorMaps.add(new TeulingFig2());
		
		List<Colormap2D> colorMaps = ColorMapFinder.findInPackage("colormaps.impl");
		
		File output = new File(System.getProperty("user.home"),  "colormaps");
		output.mkdirs();

//		createDecomposedTable(colorMaps, output);
		createQualityTable(colorMaps, output);
	}

	private static void createQualityTable(List<Colormap2D> colorMaps, File output) throws Exception
	{
		File texFile = LatexTableQuality.generateTable(colorMaps, output);
		File pdf = compileLaTeX(texFile);
		
		logger.info("PDF file created at " + pdf.getCanonicalPath());
	}

	private static void createDecomposedTable(List<Colormap2D> colorMaps, File output) throws IOException, Exception
	{
		File texFile = LatexTableDecomp.generateTable(colorMaps, output);

		// first run creates .aux file 
		compileLaTeX(texFile);
		
		// read .aux file
		runBibTex(output);
		
		// create pdf with correct refs
		File decompTablePdf = compileLaTeX(texFile);
		
		logger.info("PDF file created at " + decompTablePdf.getCanonicalPath());
	}
	
	private static void runBibTex(File folder) throws Exception
	{
		String bibName = "colormaps";
		
		Path target = Paths.get(folder.getAbsolutePath(), bibName + ".bib");
		try (InputStream is = LatexGen.class.getResource("/latex/colorBib.bib").openStream())
		{
			Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
		}
		
		String compiler = MIKTEX_PATH + "miktex\\bin\\bibtex.exe";
		ProcessBuilder pb = new ProcessBuilder(compiler, bibName);
		File workingDir = target.getParent().toFile();
		logger.debug("Working directory: " + workingDir);
		pb.directory(workingDir);
		
		int exitCode = runCommand(pb, LoggerFactory.getLogger("BibTeX"));
		logger.debug("BibTeX has terminated with " + exitCode);
	}

	private static File compileLaTeX(File texFile) throws Exception
	{
		String compiler = MIKTEX_PATH + "miktex\\bin\\pdflatex.exe";
		String texPath = texFile.getAbsolutePath();
		ProcessBuilder pb = new ProcessBuilder(compiler, "-max-print-line=220", "-synctex=-1", "-interaction=nonstopmode", texPath);
		File workingDir = texFile.getParentFile();
		logger.debug("Working directory: " + workingDir);
		pb.directory(workingDir);
		
		Logger latexLogger = LoggerFactory.getLogger("LaTeX");
		
		int exitCode = runCommand(pb, latexLogger);
		logger.debug("Compiler has terminated with " + exitCode);
	
		return new File(texPath.substring(0, texPath.lastIndexOf('.')) + ".pdf");
	}

	private static int runCommand(ProcessBuilder pb, Logger log) throws IOException, InterruptedException
	{
		Process process = pb.start();
		BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		 
        String line=null;

		while ((line = stdOutput.readLine()) != null)
		{
			log.debug(line);
		}

		while ((line = stdError.readLine()) != null)
		{
			log.warn(line);
		}

		int exitCode = process.waitFor();
		return exitCode;
	}
}

