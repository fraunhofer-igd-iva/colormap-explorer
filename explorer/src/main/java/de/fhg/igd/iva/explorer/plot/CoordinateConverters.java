package de.fhg.igd.iva.explorer.plot;

import java.awt.Color;
import java.awt.color.ColorSpace;

import com.google.common.base.Function;

import de.fhg.igd.iva.colorspaces.CIELAB;
import de.fhg.igd.pcolor.colorspace.CS_CIELab;

/**
 * Methods to create {@link CoordinateConverter} instances
 */
public class CoordinateConverters
{
    /**
     * Creates an RGB {@link CoordinateConverter}
     *
     * @return The {@link CoordinateConverter}
     */
    public static CoordinateConverter createRgb()
    {
        Function<Color, float[]> colorToCubeCoordinates = new Function<Color, float[]>()
        {
            @Override
            public float[] apply(Color color)
            {
                return color.getRGBColorComponents(null);
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] s)
            {
                return s;
            }
        };
        return create(colorToCubeCoordinates, cubeCoordinatesToRgbColorComponents);
    }


    /**
     * Creates an XYZ {@link CoordinateConverter}
     *
     * @return The {@link CoordinateConverter}
     */
    public static CoordinateConverter createXyz()
    {
        Function<Color, float[]> colorToCubeCoordinates = new Function<Color, float[]>()
        {
            @Override
            public float[] apply(Color c)
            {
                ColorSpace csXyz = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
                float[] result = c.getColorComponents(csXyz, null);
                return result;
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] s)
            {
                return ColorSpace.getInstance(ColorSpace.CS_CIEXYZ).toRGB(s);
            }
        };
        return create(colorToCubeCoordinates, cubeCoordinatesToRgbColorComponents);
    }


    /**
     * Creates an Lab {@link CoordinateConverter}
     *
     * @return The {@link CoordinateConverter}
     */
    public static CoordinateConverter createLab()
    {
        Function<Color, float[]> colorToCubeCoordinates = new Function<Color, float[]>()
        {
            @Override
            public float[] apply(Color c)
            {
                return c.getColorComponents(CS_CIELab.instance, null);
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] s)
            {
                return CS_CIELab.instance.toRGB(s);
            }
        };
        return create(colorToCubeCoordinates, cubeCoordinatesToRgbColorComponents);
    }

    /**
     * Creates an Lab {@link CoordinateConverter}
     *
     * @return The {@link CoordinateConverter}
     */
    public static CoordinateConverter createLabSM()
    {
        Function<Color, float[]> colorToCubeCoordinates = new Function<Color, float[]>()
        {
            @Override
            public float[] apply(Color c)
            {
                float rgb[] = c.getColorComponents(null);
                double[] lab = CIELAB.rgb2lab(new double[]{ rgb[0], rgb[1], rgb[2] });
                float[] labFloat = new float[] { (float)lab[0], (float)lab[1], (float)lab[2] };
                return labFloat;
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] s)
            {
                double lab[] = new double[] { s[0], s[1], s[2] };
                double rgb[] = CIELAB.lab2rgb(lab);
                return new float[] { (float)rgb[0], (float)rgb[1], (float)rgb[2] };
            }
        };
        return create(colorToCubeCoordinates, cubeCoordinatesToRgbColorComponents);
    }


    /**
     * Creates an HSB {@link CoordinateConverter}
     *
     * @return The {@link CoordinateConverter}
     */
    public static CoordinateConverter createHsb()
    {
        Function<Color, float[]> colorToCubeCoordinates = new Function<Color, float[]>()
        {
            @Override
            public float[] apply(Color c)
            {
                float t[] = new float[3];
                c.getRGBColorComponents(t);
                int r = (int)(t[0]*255);
                int g = (int)(t[1]*255);
                int b = (int)(t[2]*255);
                Color.RGBtoHSB(r, g, b, t);
                return t;
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] s)
            {
                int rgb = Color.HSBtoRGB(s[0], s[1], s[2]);
                Color c = new Color(rgb);
                return c.getRGBColorComponents(null);
            }
        };
        return create(colorToCubeCoordinates, cubeCoordinatesToRgbColorComponents);
    }


    private static CoordinateConverter create(
        final Function<Color, float[]> colorToCubeCoordinates,
        final Function<float[], float[]> cubeCoordinatesToRgbColorComponents)
    {
        return new CoordinateConverter()
        {
            @Override
            public Function<Color, float[]> getColorToCubeCoordinates()
            {
                return colorToCubeCoordinates;
            }

            @Override
            public Function<float[], float[]> getCubeCoordinatesToRgbColorComponents()
            {
                return cubeCoordinatesToRgbColorComponents;
            }

        };
    }

    private CoordinateConverters()
    {
    }
}
