package main;

import colormaps.Colormap2D;

/**
 * Interface for panels that deal with a given colormap.
 * @author Simon Thum
 */
public interface ColormapPanel {

	/**
	 * @param colormap the colormap for the panel
	 */
	public void setColormap(Colormap2D colormap);
	
}