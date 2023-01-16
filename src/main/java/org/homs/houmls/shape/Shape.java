package org.homs.houmls.shape;

import org.homs.houmls.shape.impl.connector.Connector;

import java.awt.*;
import java.util.Collection;

public interface Shape extends Draggable {

    String getAttributesText();

    void setAttributesText(String attributesText);

    /**
     * @param connectors the list of {@link Connector}s, needed when a box
     *                   is resized and we should update the linked coordinates.
     * @param mousex
     * @param mousey
     * @return
     */
    Draggable findDraggableByPos(Collection<Shape> connectors, double mousex, double mousey);

    void draw(Graphics g);

    void drawSelection(Graphics g);

    Shape duplicate(int translatex, int translatey);
}