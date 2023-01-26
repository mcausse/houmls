package org.homs.houmls.shape.impl.box;

import org.homs.houmls.shape.Shape;

import java.awt.*;

import static org.homs.houmls.LookAndFeel.BOXES_SHADOW_COLOR;
import static org.homs.houmls.LookAndFeel.basicStroke;

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
        if (this.shadowWidth > 0) {
            g2.setColor(BOXES_SHADOW_COLOR);
            g2.fillOval(ix + this.shadowWidth, iy + this.shadowWidth, iwidth, iheight);
        }

        g2.setColor(backgroundColor);
        g2.fillOval(ix, iy, iwidth, iheight);

        g2.setColor(Color.BLACK);
        g2.drawOval(ix, iy, iwidth, iheight);
    }
}
