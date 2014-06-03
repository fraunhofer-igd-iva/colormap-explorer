package colorSpaces;

import java.awt.Color;

/**
 * Do not re-use or distribute. Only for research and teaching purpose.
 * <p>
 * Copyright (c) 2014, University of Konstanz
 * </p>
 * @author Sebastian Mittelstaedt
 *
 */

public abstract class AbstractColorSpace {
	
	public abstract double[] toRGB(double[] v);
	public abstract Color toColor(double[] v, boolean returnBlackForUndefinedRGB);
	public abstract double[] fromRGB(double[] rgb);
	public abstract double[] fromColor(Color c);

}
