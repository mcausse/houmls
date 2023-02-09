package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.shape.Shape;

import java.awt.*;

public class FloatingText extends Box {

    public FloatingText(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new FloatingText((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    protected void drawTheBox(Graphics2D g2) {
        // just overrides this method, with nothing to do here
    }
}
