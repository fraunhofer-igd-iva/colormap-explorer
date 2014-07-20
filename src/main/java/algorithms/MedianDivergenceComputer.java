package algorithms;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import main.MismatchScatterplotPanel;
import algorithms.sampling.SamplingStrategy;
import colormaps.Colormap2D;

import com.google.common.collect.Lists;

/**
 * Computes the median divergence of a color map and related quantiles.
 * 
 * @author Simon Thum
 */
public class MedianDivergenceComputer {
	
	private Colormap2D _colormap;
	
	private List<Point2D> _points;

	private double[] _ratios;
	
	private MedianDivergenceComputer(Colormap2D _colormap, List<Point2D> _points) {
		super();
		this._colormap = _colormap;
		this._points = _points;
	}

	public static MedianDivergenceComputer fromSamplingStrategy(Colormap2D colormap, SamplingStrategy strategy) {
		List<Point2D> points = Lists.newArrayList(strategy.getPoints());
		MedianDivergenceComputer that = new MedianDivergenceComputer(colormap, points);
		that.deriveMedianColormapToJNDRatio();
		return that;
	}
	
	public static MedianDivergenceComputer fromPoints(Colormap2D colormap, List<Point2D> points) {
		MedianDivergenceComputer that = new MedianDivergenceComputer(colormap, points);
		that.deriveMedianColormapToJNDRatio();
		return that;
	}
	
	public double getQuantile(double p) {
		return _ratios[(int) (_ratios.length * p)];
	}
	
	private void deriveMedianColormapToJNDRatio() {
		int len = _points.size()/2;
		double[] ratios = new double[len];
		Iterator<Point2D> points = _points.iterator();
		
		for (int i = 0; i < len; i++)
		{
			Point2D p1 = points.next();
			Point2D p2 = points.next();
	
			float dist = (float) p1.distance(p2);
			
			Color colorA = _colormap.getColor(p1.getX(), p1.getY());
			Color colorB = _colormap.getColor(p2.getX(), p2.getY());
			
			// roughly 0-100
			double cdist = MismatchScatterplotPanel.colorDiff(colorA, colorB);
			
			double ratio = cdist / dist;
			ratios[i] = ratio;
		}
		Arrays.sort(ratios);
		_ratios = ratios;
	}
	
}
