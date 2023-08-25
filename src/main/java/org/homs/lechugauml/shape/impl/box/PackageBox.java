package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.FontMetrics;
import org.homs.lechugauml.LookAndFeel;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;

import static org.homs.lechugauml.LookAndFeel.basicStroke;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class PackageBox extends Box {

    public PackageBox(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new PackageBox((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        g2.setStroke(basicStroke);

        var regularFont = LookAndFeel.regularFont(fontSize);
        g2.setFont(regularFont);
        int fontHeight = FontMetrics.getHeight(g2, "aaaAA0");

        String[] textLines = this.text.split("\\n");

        int packageWidth = 0;
        int packageHeight = 0;
        for (var line : textLines) {
            if (line.trim().equals("--")) {
                packageHeight += FONT_Y_CORRECTION;
                break;
            }
            int lineWidth = FontMetrics.getWidth(g2, line) + FONT_X_CORRECTION * 2;
            if (packageWidth < lineWidth) {
                packageWidth = lineWidth;
            }
            packageHeight += fontHeight;
        }

        /*
         * PINTA OMBRA
         */
        if (shadowWidth > 0 && !LookAndFeel.turbo) {
            g2.setColor(shadowColor);
            g2.fillRect(ix + packageWidth, iy + shadowWidth, shadowWidth, packageHeight);
            g2.fillRect(ix + iwidth, iy + shadowWidth + packageHeight, shadowWidth, iheight - packageHeight);
            g2.fillRect(ix + shadowWidth, iy + iheight, iwidth, shadowWidth);
        }

        if (paintBackground) {
            g2.setColor(backgroundColor);
            g2.fillRect(ix, iy, packageWidth, packageHeight);
            g2.fillRect(ix, iy + packageHeight, iwidth, iheight - packageHeight);
        }

        g2.setColor(Color.BLACK);
        g2.drawRect(ix, iy, packageWidth, packageHeight);
        g2.drawRect(ix, iy + packageHeight, iwidth, iheight - packageHeight);
    }
}
