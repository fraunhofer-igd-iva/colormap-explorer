package colormapRegression;

import java.io.IOException;

import regression.BufferedImageAnalyzer;

public class StatisticsTester {

	public static void main(String[] args) throws IOException {
		// BufferedImageAnalyzer.analyzeBufferedImage(
		// "src/main/java/colormapRegression/images/magentaGreen.png",
		// "output.txt");

		// BufferedImageAnalyzer.analyzeBufferedImage(
		// "src/main/java/colormapRegression/images/orangeLightBlue.png",
		// "output.txt");

		// BufferedImageAnalyzer.analyzeBufferedImage(
		// "src/main/java/colormapRegression/images/greenRedYellowBlack.png",
		// "output.txt");

		BufferedImageAnalyzer
				.analyzeBufferedImage(
						"src/main/java/colormapRegression/images/LespinasGreenPurple.png",
						"output.txt");

	}
}
