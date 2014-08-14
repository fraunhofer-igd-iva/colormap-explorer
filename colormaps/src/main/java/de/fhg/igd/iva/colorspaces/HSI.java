/*
 * Copyright (c) 2014, University of Konstanz
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
package de.fhg.igd.iva.colorspaces;

import java.awt.Color;

/**
 * 
 * @author Sebastian Mittelstädt
 *
 */
public class HSI extends AbstractColorSpace {
	
	public static double[] rgb2hsi(double[] rgb) {
        double r = rgb[0];
        double g = rgb[1];
        double b = rgb[2];

        double mid = (r + g + b) / 3.0;
        double mr = r - mid;
        double mg = g - mid;
        double mb = b - mid;

        double intensity = mid + Math.sqrt(2.0 * (mr * mr + mg * mg + mb * mb) / 3.0);

        double cos_hue = (2.0 * mr - mg - mb) / Math.sqrt((mr * mr + mg * mg + mb * mb) * 6.0);

        double saturation = 2.0 * (intensity - mid) / intensity;

        cos_hue = cos_hue > 1.0 ? 1.0 : cos_hue;
        cos_hue = cos_hue < -1.0 ? -1.0 : cos_hue;

        double hue = Math.acos(cos_hue) * 3.0 / Math.PI;

        if (r == g && r == b) {
            hue = 0.0;
        }


        if (b > g) {
            hue = 6.0 - hue;
        }


        return new double[]{hue, saturation, intensity};
    }
    
    public static double[] hsi2rgb(double[] hsi) {
        double hue = hsi[0];
        double saturation = hsi[1];
        double intensity = hsi[2];
        
        intensity = intensity < 0.0 ? 0.0 : intensity;
        intensity = intensity > 1.3 ? 1.3 : intensity;
        saturation = saturation < 0.0 ? 0.0 : saturation;
        saturation = saturation > 1.3 ? 1.3 : saturation;
        
        if (saturation == 0.0) {
            intensity = intensity > 1.0 ? 1.0 : intensity;
            return new double[]{intensity,intensity,intensity};
        }
        
        double red = value(hue + 0.0, intensity, saturation); //   0 Grad =   0 pi
        double green = value(hue + 4.0, intensity, saturation); // 240 Grad = 4/3 pi
        double blue = value(hue + 2.0, intensity, saturation); // 120 Grad = 2/3 pi

        return new double[]{red,green,blue};
    }
    
    public static double value(double hue_phase, double intensity, double saturation) {
        double pure = 0.5 * (1.0 + Math.cos(hue_phase * Math.PI / 3.0));
        return (intensity * (1.0 - saturation * (1.0 - pure)));
    }
	
	@Override
	public double[] toRGB(double[] v) {
		return hsi2rgb(v);
	}

	@Override
	public Color toColor(double[] v, boolean returnBlackForUndefinedRGB) {
		return RGB.rgb2color(hsi2rgb(v), returnBlackForUndefinedRGB);
	}

	@Override
	public double[] fromRGB(double[] rgb) {
		return rgb2hsi(rgb);
	}

	@Override
	public double[] fromColor(Color c) {
		return rgb2hsi(RGB.color2rgb(c));
	}
}
