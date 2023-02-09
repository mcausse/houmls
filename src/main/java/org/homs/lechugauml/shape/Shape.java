package org.homs.lechugauml.shape;

import java.awt.*;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public interface Shape extends Draggable {

    String getAttributesText();

    void setAttributesText(String attributesText);

    Draggable findDraggableByPos(double mousex, double mousey);

    void draw(Graphics g);

    void drawSelection(Graphics g);

    Shape duplicate(int translatex, int translatey);
}