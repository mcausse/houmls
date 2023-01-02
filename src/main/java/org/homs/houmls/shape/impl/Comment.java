package org.homs.houmls.shape.impl;

import org.homs.houmls.Turtle;

import java.awt.*;

import static org.homs.houmls.LookAndFeel.basicStroke;

public class Comment extends Box {

    public Comment(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        g2.setStroke(basicStroke);

        int COMMENT_FOLD_DIST = 20;

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

        g2.setColor(Color.YELLOW);
        turtle.fillPolygon(g2);

        g2.setColor(Color.BLACK);
        turtle.drawPolyline(g2);
//
//        // ombra fina
//        g2.drawLine(ix + iwidth + 1, iy + 1, ix + iwidth + 1, iy + iheight + 1);
//        g2.drawLine(ix + 1, iy + iheight + 1, ix + iwidth + 1, iy + iheight + 1);
    }
}
