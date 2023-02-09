package org.homs.lechugauml;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class FontMetrics {

    final Font font;
    final FontRenderContext context;

    public FontMetrics(Graphics2D g2) {
        font = g2.getFont();
        context = g2.getFontRenderContext();
    }

    public Rectangle2D getBounds(String message) {
        return font.getStringBounds(message, context);
    }

    public double getWidth(String message) {
        Rectangle2D bounds = font.getStringBounds(message, context);
        return bounds.getWidth();
    }

    public int getHeight(String message) {
        Rectangle2D bounds = font.getStringBounds(message, context);
        return (int) bounds.getHeight();
    }

    public static int getWidth(Graphics2D g2, String text) {
        Font font = g2.getFont();
        Rectangle2D bounds = font.getStringBounds(text, g2.getFontRenderContext());
        return (int) bounds.getWidth();
    }

    public static int getHeight(Graphics2D g2, String text) {
        Font font = g2.getFont();
        Rectangle2D bounds = font.getStringBounds(text, g2.getFontRenderContext());
        return (int) bounds.getHeight();
    }
}