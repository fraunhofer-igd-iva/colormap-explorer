package de.fhg.igd.iva.explorer.plot;

import java.awt.Color;

import com.google.common.base.Function;

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * Interface summarizing the functions for the conversion between
 * cube coordinates and colors, and vice versa
 */
public interface CoordinateConverter
{
    /**
     * The function that converts a color (that was looked up in
     * the {@link Colormap}) to a 3D position in the cube.
     *
     * @return The function
     */
    Function<Color, float[]> getColorToCubeCoordinates();

    /**
     * The function that maps 3D positions in the cube to RGB color
     * components for coloring the plot surface
     *
     * @return The function
     */
    Function<float[], float[]> getCubeCoordinatesToRgbColorComponents();

}
