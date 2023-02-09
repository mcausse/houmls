package org.homs.lechugauml.shape.impl.connector;

import java.awt.geom.Point2D;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class DoublePoint extends Point2D.Double {

    public DoublePoint(double x, double y) {
        super(x, y);
    }

    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
}