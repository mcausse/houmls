package org.homs.houmls.shape;

import java.awt.*;

public interface Shape extends Draggable, Comparable<Shape> {

    String getAttributesText();

    void setAttributesText(String attributesText);

    Draggable findTranslatableByPos(double mousex, double mousey);

    void draw(Graphics g, int fontHeigth);

    void drawSelection(Graphics g);
}