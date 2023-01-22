package org.homs.houmls.shape.impl.box;

import org.homs.houmls.shape.Shape;

import java.awt.*;

import static org.homs.houmls.LookAndFeel.*;

public class Ellipse extends Box {

    public Ellipse(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new Ellipse((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        g2.setStroke(basicStroke);

        /*
         * PINTA OMBRA
         */
        if (BOXES_WITH_SHADOW) {
            g2.setColor(BOXES_SHADOW_COLOR);
            g2.fillOval(ix + BOXES_SHADOW_WIDTH, iy + BOXES_SHADOW_WIDTH, iwidth, iheight);
        }

        g2.setColor(backgroundColor);
        g2.fillOval(ix, iy, iwidth, iheight);

        g2.setColor(Color.BLACK);
        g2.drawOval(ix, iy, iwidth, iheight);
    }
}
