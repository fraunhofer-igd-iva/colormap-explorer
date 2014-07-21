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
package colorspaces;

import java.awt.Color;

/**
 *
 * @author Sebastian Mittelstädt
 */
public class CAT02 extends AbstractColorSpace {
    
    

    public static double[] xyz2cat02(double[] xyz) {
        double x = xyz[0];
        double y = xyz[1];
        double z = xyz[2];

        double L = 0.7328 * x + 0.4296 * y - 0.1624 * z;
        double M = -0.7036 * x + 1.6975 * y + 0.0061 * z;
        double S = 0.0030 * x + 0.0136 * y + 0.9834 * z;

        return new double[]{L, M, S};
    }

    public static double[] cat022xyz(double[] cat) {
        double L = cat[0];
        double M = cat[1];
        double S = cat[2];

        double x = 1.096124 * L - 0.278869 * M + 0.182745 * S;
        double y = 0.454369 * L + 0.473533 * M + 0.072098 * S;
        double z = -0.009628 * L - 0.005698 * M + 1.015326 * S;

        return new double[]{x,y,z};
    }

	@Override
	public double[] toRGB(double[] v) {
		double[] xyz = cat022xyz(v);
		return XYZ.xyz2rgb(xyz);
	}

	@Override
	public Color toColor(double[] v, boolean returnBlackForUndefinedRGB) {
		double[] rgb = toRGB(v);
		return RGB.rgb2color(rgb, returnBlackForUndefinedRGB);
	}

	@Override
	public double[] fromRGB(double[] rgb) {
		double[] xyz = XYZ.rgb2xyz(rgb);
		return xyz2cat02(xyz);
	}

	@Override
	public double[] fromColor(Color c) {
		double[] rgb = RGB.color2rgb(c);
		return fromRGB(rgb);
	}


    
}
