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

package de.fhg.igd.iva.colormaps;

import java.awt.Color;

import com.google.common.base.Preconditions;

/**
 * A partly (default) implementation of {@link KnownColormap}
 * @author Martin Steiger
 */
public abstract class AbstractColormap implements Colormap {

	protected void checkRanges(double x, double y) {
		Preconditions.checkArgument(0 <= x && x <= 1, "X must in in range [0..1], but is %s", x);
		Preconditions.checkArgument(0 <= y && y <= 1, "Y must in in range [0..1], but is %s", y);
	}

    protected Color interpolate(Color tl, Color tr, Color bl, Color br, double x, double y) {
        checkRanges(x, y);

        double r = interpolate(tl.getRed(), tr.getRed(), bl.getRed(), br.getRed(), x, y);
        double g = interpolate(tl.getGreen(), tr.getGreen(), bl.getGreen(), br.getGreen(), x, y);
        double b = interpolate(tl.getBlue(), tr.getBlue(), bl.getBlue(), br.getBlue(), x, y);

        return new Color((float)r / 255f, (float)g / 255f, (float)b / 255f);
    }

    protected double interpolate(double start, double end, double position) {
        Preconditions.checkArgument(0 <= position && position <= 1, "position must in in range [0..1], but is %s", position);
        return start + (end - start) * position;
    }

    protected double interpolate(double topLeft, double topRight, double bottomLeft, double bottomRight, double posX, double posY) {
        double o = interpolate(topLeft, topRight, posX);
        double u = interpolate(bottomLeft, bottomRight, posX);
        return interpolate(o, u, posY);
    }
}
