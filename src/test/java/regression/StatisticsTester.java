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
package regression;

import java.io.IOException;

public class StatisticsTester {

	public static void main(String[] args) throws IOException {
		// BufferedImageAnalyzer.analyzeBufferedImage(
		// "src/main/java/colormapRegression/images/magentaGreen.png",
		// "output.txt");

		// BufferedImageAnalyzer.analyzeBufferedImage(
		// "src/main/java/colormapRegression/images/orangeLightBlue.png",
		// "output.txt");

		BufferedImageAnalyzer.analyzeBufferedImage("/images/greenRedYellowBlack.png", "output.txt");
	}

}
