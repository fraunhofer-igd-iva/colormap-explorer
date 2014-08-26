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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;

import com.google.common.base.Function;

import de.fhg.igd.iva.colormaps.Colormap;

/**
 * A class handling a jzy3d Shape and a {@link Colormap}. <br />
 * <br />
 * The shape of the shape will be determined by the {@link Colormap}
 * and a function that maps a color to a 3D position: For each
 * surface point given as u/v coordinates, the corresponding color
 * will be looked up in the color map. This color will then be
 * converted to a 3D position.
 * <br />
 * The color of the shape will be determined by a function that maps
 * a 3D position to a color.
 */
class ColormapShape
{
    private final int stepsX = 10;
    private final int stepsY = 10;

    private Colormap colormap;
    private final Map<Point, java.awt.Point> pointToGridCoordinates;
    private final Shape shape;

    private final Function<Color, float[]> colorToCubeCoordinates;

    final Function<float[], float[]> cubeCoordinatesToRgbColorComponents;

    ColormapShape(Colormap colormap,
        Function<float[], float[]> cubeCoordinatesToRgbColorComponents,
        Function<Color, float[]> colorToCubeCoordinates)
    {
        this.colormap = colormap;
        this.cubeCoordinatesToRgbColorComponents = cubeCoordinatesToRgbColorComponents;
        this.colorToCubeCoordinates = colorToCubeCoordinates;
        this.pointToGridCoordinates = new HashMap<Point, java.awt.Point>();
        this.shape = createShape();
    }

    void setColormap(Colormap colormap)
    {
        this.colormap = colormap;
        updateShape();
    }

    Shape getShape()
    {
        return shape;
    }

    private Shape createShape()
    {
        List<Polygon> polygons = new ArrayList<Polygon>();
        for (int sx = 0; sx < stepsX; sx++)
        {
            for (int sy = 0; sy < stepsY; sy++)
            {
                polygons.add(createPolygon(sx, sy));
            }
        }
        final Shape shp = new Shape(polygons);

        ColorMapper colorMapper = new ColorMapper()
        {
            @Override
            public org.jzy3d.colors.Color getColor(Coord3d c)
            {
                float rgb[] = cubeCoordinatesToRgbColorComponents.apply(new float[] { c.x, c.y, c.z });
                return new org.jzy3d.colors.Color(rgb[0], rgb[1], rgb[2]);
            }
        };
        shp.setColorMapper(colorMapper);
        shp.setFaceDisplayed(true);
        shp.setWireframeDisplayed(true);
        return shp;
    }

    private Polygon createPolygon(int sx, int sy)
    {

        Polygon polygon = new Polygon();
        polygon.add(createVertex(sx, sy));
        polygon.add(createVertex(sx, sy + 1));
        polygon.add(createVertex(sx + 1, sy + 1));
        polygon.add(createVertex(sx + 1, sy));
        return polygon;
    }

    private Point createVertex(int gx, int gy)
    {
        float coordinates[] = getCubeCoordinatesForGridPoint(gx, gy);
        Point vertex = new Point(new Coord3d(coordinates));
        pointToGridCoordinates.put(vertex, new java.awt.Point(gx, gy));
        return vertex;
    }

    private void updateShape()
    {
        List<AbstractDrawable> polygons = shape.getDrawables();
        for (AbstractDrawable d : polygons)
        {
            if (d instanceof Polygon)
            {
                Polygon p = (Polygon) d;
                for (int i = 0; i < p.size(); i++)
                {
                    Point vertex = p.get(i);
                    Coord3d c = vertex.xyz;
                    java.awt.Point gridCoordinates = pointToGridCoordinates
                        .get(vertex);
                    int gx = gridCoordinates.x;
                    int gy = gridCoordinates.y;
                    float coordinates[] = getCubeCoordinatesForGridPoint(gx, gy);

                    // System.out.println("At "+gridCoordinates+" have "+Arrays.toString(coordinates));

                    c.x = coordinates[0];
                    c.y = coordinates[1];
                    c.z = coordinates[2];
                }
            }
        }
    }

    private float[] getCubeCoordinatesForGridPoint(int gx, int gy)
    {
        double x = (double) gx / stepsX;
        double y = (double) gy / stepsY;
        Color color = colormap.getColor(x, y);
        float coordinates[] = colorToCubeCoordinates.apply(color);
        return coordinates;
    }

}

