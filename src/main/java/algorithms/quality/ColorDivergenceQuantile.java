package algorithms.quality;

import java.util.Random;

import algorithms.MedianDivergenceComputer;
import algorithms.sampling.EvenDistributedDistancePoints;
import colormaps.Colormap2D;

/**
 * Represents the color appearance divergence ratio as a quality measure. Higher ratio means
 * a higher change of color appearence for a given color map distance.
 * Higher median values should correlate to better color exploitation.
 * 
 * Note: Without caching, as is now, this is pretty expensive.
 * 
 * @author Simon Thum
 */
public class ColorDivergenceQuantile implements ColormapQuality {
	
	double quantile;
	
	public ColorDivergenceQuantile(double quantile) {
		super();
		this.quantile = quantile;
	}

	private String quantileName() {
		if (quantile == 0.5)
			return "median ";
		else return String.format("%.0f%%-quantile of ", quantile);
	}

	@Override
	public double getQuality(Colormap2D colormap2d) {
		MedianDivergenceComputer comp = MedianDivergenceComputer.fromSamplingStrategy(colormap2d, new EvenDistributedDistancePoints(new Random(123), 10000));
		return comp.getQuantile(quantile);
	}

	@Override
	public String getName() {
		return quantileName() + " color appearance divergence ratio";
	}

	@Override
	public String getDescription() {
		return "Color appearance divergence ratio; higher ratio means " +
		"higher visual change of color appearence for a given color map distance.";
	}

}
