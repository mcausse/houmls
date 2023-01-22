package org.homs.houmls.shape.impl.connector;

import java.awt.geom.Point2D;

public class DoublePoint extends Point2D.Double {

    public DoublePoint(double x, double y) {
        super(x, y);
    }

    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
}