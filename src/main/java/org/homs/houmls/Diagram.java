package org.homs.houmls;

import org.homs.houmls.shape.Shape;
import org.homs.houmls.shape.impl.Connector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Diagram {

    public double zoom = 1.0;
    public double offsetX = 0.0;
    public double offsetY = 0.0;
    private final List<Shape> shapes = new ArrayList<>();

    public void addShape(Shape element) {
        this.shapes.add(element);
    }

    public void addShapes(Collection<Shape> elements) {
        this.shapes.addAll(elements);
    }

    public List<Shape> getShapesBy(Predicate<? super Shape> predicate) {
        return shapes.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public Collection<Connector> findConnectorsBy(Function<Connector, Boolean> filter) {
        var r = new ArrayList<Connector>();
        for (var shape : shapes) {
            if (Connector.class.isAssignableFrom(shape.getClass())) {
                Connector c = (Connector) shape;
                if (filter.apply((Connector) shape)) {
                    r.add(c);
                }
            }
        }
        return r;
    }

    public Rectangle getDiagramBounds() {
        int minx = 0;
        int miny = 0;
        int maxx = 0;
        int maxy = 0;
        if (!shapes.isEmpty()) {
            minx = Integer.MAX_VALUE;
            miny = Integer.MAX_VALUE;
            maxx = Integer.MIN_VALUE;
            maxy = Integer.MIN_VALUE;
            for (var element : shapes) {
                var rect = element.getRectangle();
                if (minx > rect.getX()) {
                    minx = rect.x;
                }
                if (miny > rect.getY()) {
                    miny = rect.y;
                }
                if (maxx < rect.getX() + rect.getWidth()) {
                    maxx = rect.x + rect.width;
                }
                if (maxy < rect.getY() + rect.getHeight()) {
                    maxy = rect.y + rect.height;
                }
            }
        }
        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }
}