package colormaps.impl;

import static java.lang.Math.hypot;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;

import colormaps.Colormap2D;

import com.google.common.base.Function;

import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CIELab;

/**
 * Guo-style Lab 2D color spaces, based on functions that reflect the choices in
 * the paper.
 * @author simon
 */
public abstract class GuoLabStyle extends PColorBased {

	/**
	 * the lightness function, defined based on distance from the mid point 0..sqrt 2
	 */
	Function<Float, Float> lightness_function;
	
	public static Function<Float, Float> cone(final float factor) {
		return new Function<Float, Float>() {
			@Override
			public Float apply(Float input) {
				return 100.0f-(input*factor);
			}
		};
	}
	
	public static Function<Float, Float> pseudo_gaussian(float sigma) {
		final float sigmasquare = sigma * sigma;
		return new Function<Float, Float>() {
			@Override
			public Float apply(Float input) {
				return (float) (100*Math.exp(-((input*input)/2*sigmasquare)));
			}
		};
	}
	
	
	/**
	 * The chroma function, defined based on x and y as in {@link Colormap2D#getColor(float, float)}
	 */
	public Function<Map.Entry<Float, Float>, Map.Entry<Float, Float>> chroma_function;

	public static Function<Map.Entry<Float, Float>, Map.Entry<Float, Float>> linear_ab(
			final float x_offset,
			final float x_factor,
			final float y_offset,
			final float y_factor) {
		return new Function<Map.Entry<Float,Float>, Map.Entry<Float,Float>>() {
			@Override
			public Entry<Float, Float> apply(Entry<Float, Float> input) {
				return new AbstractMap.SimpleEntry<>(
						x_offset + input.getKey() * x_factor,
						y_offset + input.getValue() * y_factor);
			}
		};
	}
	
	@Override
	public PColor getPColor(float x, float y) {
		// normalize to -1..1
		x *= 2; x -= 1;
		y *= 2; y -= 1;
		float dist = (float) hypot(x, y);
		float L = lightness_function.apply(dist);
		Map.Entry<Float, Float> ab = chroma_function.apply(new AbstractMap.SimpleEntry<>(x, y));
		return PColor.create(CS_CIELab.instance, new float[]{L, ab.getKey(), ab.getValue()});
	}
	
	public String toString() {
		return getName();
	}

}
