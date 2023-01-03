package org.homs.houmls;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class StringMetrics {

    final Font font;
    final FontRenderContext context;

    public StringMetrics(Graphics2D g2) {
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
}