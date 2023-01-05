package org.homs.houmls.shape;

import org.homs.houmls.GridControl;

import java.awt.*;
import java.util.Collection;

public interface Shape extends Draggable {

    int DUPLICATE_OFFSET_PX = GridControl.GRID_SIZE * 2;

    String getAttributesText();

    void setAttributesText(String attributesText);

    /**
     * @param elements the list of {@link Shape}s, needed when a box is resized and we should update
     *                 the linked {@link org.homs.houmls.shape.impl.Connector}s coordinates.
     * @param mousex
     * @param mousey
     * @return
     */
    Draggable findTranslatableByPos(Collection<Shape> elements, double mousex, double mousey);

    void draw(Graphics g);

    void drawSelection(Graphics g);

    Shape duplicate();
}