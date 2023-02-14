package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.Map;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class FloatingText extends Box {

    public FloatingText(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
        setAttributesText(attributesText);
    }

    @Override
    public void setAttributesText(String attributesText) {
        super.setAttributesText(attributesText);

        Map<String, String> props = PropsParser.parseProperties(attributesText);
        paintBackground = Boolean.parseBoolean(props.getOrDefault("paintbackground", "false"));
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new FloatingText((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    protected void drawTheBox(Graphics2D g2) {

        if (paintBackground) {
            g2.setColor(backgroundColor);
            g2.fillRect((int) x, (int) y, (int) width, (int) height);
        }
    }

}
