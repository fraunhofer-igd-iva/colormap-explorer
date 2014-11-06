package de.fhg.igd.iva.explorer.plot;

import java.awt.Color;

import com.google.common.base.Function;

import de.fhg.igd.iva.colorspaces.CIELAB;
import de.fhg.igd.iva.colorspaces.XYZ;
import de.fhg.igd.pcolor.CAMLch;
import de.fhg.igd.pcolor.PColor;
import de.fhg.igd.pcolor.colorspace.CS_CAMLch;
import de.fhg.igd.pcolor.colorspace.CS_sRGB;
import de.fhg.igd.pcolor.colorspace.ViewingConditions;
import de.fhg.igd.pcolor.util.ColorTools;

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
     * Creates an Lab {@link CoordinateConverter}
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
                float rgb[] = c.getColorComponents(null);
                double[] lab = XYZ.rgb2xyz(new double[]{ rgb[0], rgb[1], rgb[2] });
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
                double rgb[] = XYZ.xyz2rgb(lab);
                return new float[] { (float)rgb[0], (float)rgb[1], (float)rgb[2] };
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

                double h = t[0] * Math.PI * 2.0;
                double hx = 0.5 + t[1] * 0.5 * Math.cos(h);
                double hy = 0.5 + t[1] * 0.5 * Math.sin(h);

                t[0] = (float) hx;
                t[1] = (float) hy;
                t[2] = t[2];

                return t;
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] arr)
            {
            	double hx = arr[0] - 0.5;
            	double hy = arr[1] - 0.5;
            	double h = Math.atan2(hy, hx);

            	// normalize h
            	h = h / (Math.PI * 2.0);

            	double b = arr[2];
            	double s = Math.sqrt(hx * hx + hy * hy) * 2;

                int rgb = Color.HSBtoRGB((float)h, (float)s, (float)b);
                Color c = new Color(rgb);
                return c.getRGBColorComponents(null);
            }
        };
        return create(colorToCubeCoordinates, cubeCoordinatesToRgbColorComponents);
    }


    /**
     * Creates an HSB {@link CoordinateConverter}
     *
     * @return The {@link CoordinateConverter}
     */
    public static CoordinateConverter createCAM_UCS()
    {
    	final CS_CAMLch cs_cam = new CS_CAMLch(ViewingConditions.sRGB_typical_envirnonment, CS_CAMLch.JMh);
        
    	Function<Color, float[]> colorToCubeCoordinates = new Function<Color, float[]>()
        {
            @Override
            public float[] apply(Color c)
            {
            	PColor pColorTmp = PColor.create(CS_sRGB.instance, c.getColorComponents(new float[3]));
            	return ColorTools.toUCS_Jab((CAMLch) PColor.convert(pColorTmp, cs_cam));
            }
        };
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents = new Function<float[], float[]>()
        {
            @Override
            public float[] apply(float[] arr)
            {
            	// inverse CAM-UCS (manually inverted from the formula given in the papers)
            	double M_ = Math.hypot(arr[1], arr[2]);
            	double h = Math.toDegrees(Math.atan2(arr[2], arr[1]));
            	if (h < 0) {
            		h = h + 360;
            	}
            	double M = (Math.exp(M_*0.0228)-1.0)/0.0228;
            	double j = arr[0];
            	double J = -j/(0.007*j - 100*0.007 - 1.0);
            	CAMLch JMh = (CAMLch) PColor.create(cs_cam, new float[]{(float) J, (float) M, (float) h});
            	return PColor.convert(JMh, CS_sRGB.instance).getComponents();
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
