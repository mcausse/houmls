package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.GridControl;
import org.homs.lechugauml.Turtle;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;

import static org.homs.lechugauml.LookAndFeel.basicStroke;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class Comment extends Box {

    public static final int COMMENT_FOLD_DIST = GridControl.GRID_SIZE;

    public Comment(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new Comment((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;

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
