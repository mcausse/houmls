package org.homs.houmls.shape.impl;

import org.homs.houmls.GridControl;
import org.homs.houmls.Turtle;
import org.homs.houmls.shape.Shape;

import java.awt.*;

import static org.homs.houmls.LookAndFeel.basicStroke;

public class Comment extends Box {

    public static final int COMMENT_FOLD_DIST = GridControl.GRID_SIZE;

    public Comment(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    @Override
    public Shape duplicate() {
        var r = new Comment((int) x + DUPLICATE_OFFSET_PX, (int) y + DUPLICATE_OFFSET_PX, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        g2.setStroke(basicStroke);

        var turtle = new Turtle(ix, iy, 0);
        turtle.walk(iwidth - COMMENT_FOLD_DIST);
        turtle.rotate(90);
        turtle.walk(COMMENT_FOLD_DIST);
        turtle.rotate(-90);
        turtle.walk(COMMENT_FOLD_DIST);
        turtle.rotate(-90 - 45);
        turtle.walk(Turtle.pitagoras(COMMENT_FOLD_DIST, COMMENT_FOLD_DIST));
        turtle.walk(-Turtle.pitagoras(COMMENT_FOLD_DIST, COMMENT_FOLD_DIST));
        turtle.rotate(90 + 45 + 90);
        turtle.walk(height - COMMENT_FOLD_DIST);
        turtle.rotate(90);
        turtle.walk(width);
        turtle.rotate(90);
        turtle.walk(height);

        g2.setColor(backgroundColor);
        turtle.fillPolygon(g2);

        g2.setColor(Color.BLACK);
        turtle.drawPolyline(g2);
    }
}
