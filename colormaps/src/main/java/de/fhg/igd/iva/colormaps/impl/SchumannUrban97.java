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

package de.fhg.igd.iva.colormaps.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import de.fhg.igd.iva.colormaps.AbstractColormap;
import de.fhg.igd.iva.colormaps.AbstractKnownColormap;
import de.fhg.igd.iva.colormaps.ColorSpace;
import de.fhg.igd.iva.colormaps.KnownColormap;

/**
 * Different construction scheme than {@link ConstantBlue}, but de facto identical.
 * <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFsAAABbCAIAAACTVG7OAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8
 * YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAItSURBVHhe7ZJBasRADAT99Hm2D3tIj7wok0pIYtw+7NKidPGAoApvmZ/mMW5h28Zj3IEu749xB5VD
 * 0w5eUoSkCEkR8oZFfn/9k7cqsg6e/k/+EZIiJEVIipAUISlCUoSkCEkRkiLkDYtcJEVIipAUISlCUoSkCEkRkiIkRUiKkBQhL1wkwxnjFubhW0aX
 * 9zHuoHJo2sFLipAUISlCUoSkCEkRkiIkRUiKkBQhKUJShKQISRGSIiRFSIqQFCEpQlKEpAhJEZIiJEVIipAUISlCXrhI5tuozR1o+4dxosv7Nu6g
 * asxpBy9ayljQZZi4qBpz2sGLljIWdBkmLqrGnHbwoqWMBV2GiYuqMacdvGgpY0GXYeKiasxpBy9ayljQZZi4qBpz2sGLljIWdBkmLqrGnHbwoqWM
 * BV2GiYuqMacdvGgpY0GXYeKiasxpBy9ayljQZZi4qBpz2sGLljIWdBkmLqrGnHbwoqWMBV2GiYuqMacdvGgpY0GXYeKiasxph0/2x36A72fQUsaC
 * LsPkFKX884XjSXMIfNItrkXRUsaCLsPkn5Tsc/B08Hz7qsEKF6JoKWNBl2FyilJOkYVSTpGFUk6RhVJOkYVSTpGFUk6RhVJOkYVSPlNEdIULOYSW
 * MhZ0GSanKOWTRcTR4kIOoaWMBV2GySlK+XwRB1rKWNBlmLioGnPawYuWMhZ0GSYuqsacdvCipYwFXYaJi6oxpx28aCljQZdh4qJqZNbZtg9kNAhx
 * 7eG2cgAAAABJRU5ErkJggg=="/>
 */
public class SchumannUrban97 extends NineCornersAnchorColorMapParameterizable implements KnownColormap {

	public SchumannUrban97() {
	    super(new Color[][] {
	         { new Color(0, 252, 128), new Color(128, 252, 128), new Color(248, 252, 128) },
             { new Color(0, 128, 128), new Color(128, 128, 128), new Color(248, 128, 128) },
	         { new Color(0,   0, 128), new Color(128,   0, 128), new Color(248,   0, 128) }});
	}


    @Override
    public String getName() {
        return "Schumann and Urban";
    }

    @Override
    public String getDescription() {
        return "2D RGB color lookup table by Schumann and Urban in a 3x3 grid. Identical with Constant Blue.";
    }

    @Override
    public ColorSpace getColorSpace() {
        return ColorSpace.sRGB;
    }

    @Override
    public List<String> getReferences() {
        return Arrays.asList("SchumannUrban97");
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
