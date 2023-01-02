package org.homs.houmls.shape;

import java.awt.*;

public interface Shape extends Draggable, Comparable<Shape> {

    Draggable findTranslatableByPos(double mousex, double mousey);

    void draw(Graphics g, int fontHeigth);
}