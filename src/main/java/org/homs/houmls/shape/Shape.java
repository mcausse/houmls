package org.homs.houmls.shape;

import org.homs.houmls.GridControl;

import java.awt.*;

public interface Shape extends Draggable, Comparable<Shape> {

    int DUPLICATE_OFFSET_PX = GridControl.GRID_SIZE * 2;

    String getAttributesText();

    void setAttributesText(String attributesText);

    Draggable findTranslatableByPos(double mousex, double mousey);

    void draw(Graphics g, int fontHeigth);

    void drawSelection(Graphics g);

    Shape duplicate();
}