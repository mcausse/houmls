package org.homs.lechugauml.shape.impl.connector;

import org.homs.lechugauml.GridControl;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.List;

import static org.homs.lechugauml.shape.impl.connector.Connector.BOX_EXTRA_LINKABLE_BORDER;

public class ConnectorPoint {

    public Shape linkedShape;
    public ConnectorType type;
    public double posx;
    public double posy;
    public String text = "";

    public ConnectorPoint(Shape linkedShape, ConnectorType type, double posx, double posy) {
        this.linkedShape = linkedShape;
        this.type = type;
        this.posx = posx;
        this.posy = posy;
    }

    /**
     * @return les coordenades absolutes del punt, tenint en compte que el punt en si es pot guardar
     * en (posx, posy) en absoluta si no hi ha link amb un component, o bé en relatives al component linkat.
     */
    public Point getAbsolutePoint() {
        if (linkedShape == null) {
            return new Point((int) posx, (int) posy);
        } else {
            var rect = linkedShape.getRectangle();
            return new Point((int) (rect.x + posx), (int) (rect.y + posy));
        }
    }

    public void manageLink(List<Shape> shapes) {
        Point p = getAbsolutePoint();
        Shape isLinkedTo = null;
        for (int i = shapes.size() - 1; i >= 0; i--) {
            var shape = shapes.get(i);
            if (shape instanceof Connector) {
                // evita linkar fletxes a altres fletxes!
                continue;
            }
            var rectangle = shape.getRectangle();
            rectangle.grow(BOX_EXTRA_LINKABLE_BORDER, BOX_EXTRA_LINKABLE_BORDER);
            if (rectangle.contains(p.getX(), p.getY())) {
                isLinkedTo = shape;
                break;
            }
        }
        // Linka-deslinka
        if (isLinkedTo == null) {
            this.posx = p.getX();
            this.posy = p.getY();
            this.linkedShape = null;
        } else {
            this.posx = p.getX() - isLinkedTo.getRectangle().getX();
            this.posy = p.getY() - isLinkedTo.getRectangle().getY();
            this.linkedShape = isLinkedTo;
        }
    }

    public void translate(double dx, double dy) {
        this.posx += dx;
        this.posy += dy;
    }

    public void engrida() {
        this.posx = GridControl.engrid(this.posx);
        this.posy = GridControl.engrid(this.posy);
    }
}