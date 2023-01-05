package org.homs.houmls.shape;

import org.homs.houmls.shape.impl.Connector;

import java.awt.*;
import java.util.Collection;

public interface Draggable {

    Cursor getTranslationCursor();

    Rectangle getRectangle();

    void translate(double dx, double dy);

    /**
     * Es crida en acabar d'arrossegar el component.
     * Necessari per a engidar les coordenades que han canviat, linkar/deslinkar connectors, etc.
     *
     * @param elements necessari per a {@link Connector}s, per tal
     *                 de poder-se linkar amb altres {@link Shape}s.
     * @param shapes
     */
    void dragHasFinished(Collection<Shape> shapes);
}