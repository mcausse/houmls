package org.homs.houmls.shape;

import java.awt.*;
import java.util.List;

public interface Draggable {

    Cursor getTranslationCursor();

    Rectangle getRectangle();

    void translate(double dx, double dy);

    /**
     * Es crida en acabar d'arrossegar el component.
     *
     * @param elements necessari per a {@link org.homs.houmls.shape.impl.Arrow}s, per tal
     *                 de poder-se linkar amb altres {@link Shape}s.
     */
    void dragHasFinished(List<Shape> elements);
}