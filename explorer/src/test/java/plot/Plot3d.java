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

package plot;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.fhg.igd.iva.colormaps.impl.CubeDiagonalCuttingGreenMagenta;
import de.fhg.igd.iva.explorer.plot.ColormapPlotter;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class Plot3d
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);

		final ColormapPlotter plotterPanel = new ColormapPlotter();
		plotterPanel.setColormap(new CubeDiagonalCuttingGreenMagenta());

		frame.setTitle("3D Plotter");
		frame.setLayout(new BorderLayout());
		frame.add(plotterPanel);

		frame.setVisible(true);
	}
}
