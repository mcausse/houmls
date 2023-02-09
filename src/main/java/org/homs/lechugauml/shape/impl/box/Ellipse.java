package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.Map;

import static org.homs.lechugauml.LookAndFeel.BOXES_SHADOW_COLOR;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class Ellipse extends Box {

    private Stroke stroke = new BasicStroke(1);
    private Color strokeColor = Color.BLACK;

    public Ellipse(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
        setAttributesText(attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new Ellipse((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    @Override
    public void setAttributesText(String attributesText) {
        super.setAttributesText(attributesText);

        try {
            Map<String, String> props = PropsParser.parseProperties(attributesText);
            this.stroke = new BasicStroke(Integer.parseInt(props.getOrDefault("strokewidth", "1")));
            this.strokeColor = PropsParser.getColorByProp(props, "strokecolor", "black");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        g2.setStroke(stroke);

        /*
         * PINTA OMBRA
         */
        if (this.shadowWidth > 0) {
            g2.setColor(BOXES_SHADOW_COLOR);
            g2.fillOval(ix + this.shadowWidth, iy + this.shadowWidth, iwidth, iheight);
        }

        if (paintBackground) {
            g2.setColor(backgroundColor);
            g2.fillOval(ix, iy, iwidth, iheight);
        }

        g2.setColor(strokeColor);
        g2.drawOval(ix, iy, iwidth, iheight);
    }
}
