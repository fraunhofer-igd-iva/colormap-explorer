package colormapRegression;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BufferedImageAnalyzer {

	public static void analyzeBufferedImage(String file,
			String statisticsOutputFile) throws IOException {
		File imageFile = new File(file);
		if (imageFile.exists()) {
			try {
				BufferedImage image = loadBufferedImage(imageFile);

				// iterate over the pixels and create a statistical output for
				// each R,G,B channel

				if (image == null)
					throw new IllegalArgumentException();

				File f = new File(statisticsOutputFile);
				FileWriter fw = new FileWriter(f);

				String line = "X" + "\t" + "Y" + "\t" + "R" + "\t" + "G" + "\t"
						+ "B"+ "\n";
				fw.write(line);

				double maxX = 100.0;
				double maxY = 100.0;
				for (double x = 0; x <= maxX; x++)
					for (double y = 0; y <= maxY; y++) {
						int i = (int) round(
								(x / maxX) * (double) image.getWidth(), 0);
						int j = (int) round(
								(y / maxY) * (double) image.getHeight(), 0);
						
						if (i >= image.getWidth())
							i = image.getWidth()-1;
						if (j >= image.getHeight())
							j = image.getHeight()-1;
						
						Color c = new Color(image.getRGB(i, j));

						line = (x / maxX) + "\t" + (y / maxY) + "\t"
								+ c.getRed() + "\t" + c.getGreen() + "\t"
								+ c.getBlue()+ "\n";
						System.out.println(line);
						fw.write(line);
					}

				fw.flush();
				fw.close();

			} catch (IndexOutOfBoundsException iobe) {
				iobe.printStackTrace();
			}
		}
	}

	private static BufferedImage loadBufferedImage(File file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("loadBufferedImage(File file) failed");
		}
		return img;
	}

	private static double round(double value, int decimals) {
		if (Double.isNaN(value))
			return value;
		double fact = Math.pow(10, decimals);
		double d = value * fact;
		d = Math.round(d);
		d /= fact;
		return d;
	}
}