package org.homs.houmls.shape;

import java.awt.*;
import java.util.List;

public interface Draggable {

    Cursor getTranslationCursor();

    Rectangle getRectangle();

    void translate(double dx, double dy);

    void dragHasFinished(List<Shape> elements);
}