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
package de.fhg.igd.iva.explorer.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Graph;

import com.google.common.base.Function;

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * A panel that contains a jzy3d chart and can plot a {@link Colormap}.
 */
public class ColormapPlotPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 8067498338566587771L;

    private BoundingBox3d boundingBox = new BoundingBox3d();

    private final Chart chart;
    private final Graph graph;

    private Colormap colormap;
    private ColormapShape colormapShape;
    private Function<float[], float[]> cubeCoordinatesToRgbColorComponents;
    private Function<Color, float[]> colorToCubeCoordinates;

    public ColormapPlotPanel()
    {
        super(new GridLayout(1, 1));

        chart = AWTChartComponentFactory.chart(Quality.Nicest, "awt");
        graph = chart.getScene().getGraph();
        chart.getView().setViewPoint(new Coord3d(Math.toRadians(300), Math.toRadians(22.5), 0));
        float delta = 0.0f;
        setBounds(
            -delta, 1.0f + delta,
            -delta, 1.0f + delta,
            -delta, 1.0f + delta);
        chart.addMouseController();
        add((Component) chart.getCanvas());
        setCoordinateConverter(CoordinateConverters.createRgb());
    }

    void setLabels(String labelX, String labelY, String labelZ)
    {
        chart.getAxeLayout().setXAxeLabel(labelX);
        chart.getAxeLayout().setYAxeLabel(labelY);
        chart.getAxeLayout().setZAxeLabel(labelZ);
    }

    void setLabelDigits(int digits)
    {
    	chart.getAxeLayout().setXTickRenderer(new FixedDecimalTickRenderer(digits));
    	chart.getAxeLayout().setYTickRenderer(new FixedDecimalTickRenderer(digits));
    	chart.getAxeLayout().setZTickRenderer(new FixedDecimalTickRenderer(digits));
    }

    void setBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ)
    {
        boundingBox = new BoundingBox3d(
            (float)minX, (float)maxX,
            (float)minY, (float)maxY,
            (float)minZ, (float)maxZ);
        chart.getView().setBoundManual(boundingBox);
    }

    /**
     * Set the function that converts a color (that was looked up in
     * the {@link Colormap}) to a 3D position in the cube.
     *
     * @param colorToCubeCoordinates The function
     */
    void setColorToCubeCoordinates(Function<Color, float[]> colorToCubeCoordinates)
    {
        this.colorToCubeCoordinates = colorToCubeCoordinates;
        updateShape();
    }

    /**
     * Set the function that maps 3D positions in the cube to RGB color
     * components. This function will solely be used to assign the
     * colors to the jzy3d shape based on the coordinates of the
     * map surface.
     *
     * @param cubeCoordinatesToRgbColorComponents The function
     */
    void setCubeCoordinatesToRgbColorComponents(Function<float[], float[]> cubeCoordinatesToRgbColorComponents)
    {
        this.cubeCoordinatesToRgbColorComponents = cubeCoordinatesToRgbColorComponents;
        updateShape();
    }

    /**
     * Set the {@link CoordinateConverter}, summarizing the conversion functions.
     *
     * @see #setCubeCoordinatesToRgbColorComponentsFunction(Function)
     * @see #setColorToCubeCoordinatesFunction(Function)
     *
     * @param coordinateConverter The {@link CoordinateConverter}
     */
    void setCoordinateConverter(CoordinateConverter coordinateConverter)
    {
        this.cubeCoordinatesToRgbColorComponents = coordinateConverter.getCubeCoordinatesToRgbColorComponents();
        this.colorToCubeCoordinates = coordinateConverter.getColorToCubeCoordinates();
        updateShape();
    }


    public void setColormap(Colormap colormap)
    {
        this.colormap = colormap;
        updateShape();
    }

    private void updateShape()
    {
        if (colormapShape != null)
        {
            graph.remove(colormapShape.getShape());
        }
        if (colormap != null && cubeCoordinatesToRgbColorComponents != null && colorToCubeCoordinates != null)
        {
            colormapShape = new ColormapShape(colormap, cubeCoordinatesToRgbColorComponents, colorToCubeCoordinates);
            graph.add(colormapShape.getShape());
            colormapShape.setColormap(colormap);
        }
        chart.getView().setBoundManual(boundingBox);
    }

}
