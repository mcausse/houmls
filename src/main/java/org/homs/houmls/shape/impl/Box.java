package org.homs.houmls.shape.impl;

import org.homs.houmls.GridControl;
import org.homs.houmls.LookAndFeel;
import org.homs.houmls.StringMetrics;
import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;

import java.awt.*;
import java.util.List;

import static org.homs.houmls.LookAndFeel.basicStroke;

public class Box implements Shape {

    static final int FONT_X_CORRECTION = 5;
    static final int FONT_Y_CORRECTION = 6;

    double x;
    double y;
    double width;
    double height;
    String attributesText;

    public Box(int x, int y, int width, int height, String attributesText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.attributesText = attributesText;
    }

    @Override
    public String getAttributesText() {
        return attributesText;
    }

    @Override
    public void setAttributesText(String attributesText) {
        this.attributesText = attributesText;
    }

    @Override
    public void draw(Graphics g, int fontHeigth) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        drawTheBox((Graphics2D) g);

        var g2 = (Graphics2D) g;

        String[] textLines = attributesText.split("\\n");
        int y = iy;
        for (String line : textLines) {
            if (line.trim().equals("---")) {
                y += FONT_Y_CORRECTION;
                g.drawLine(ix, y, ix + iwidth, y);
            } else {

                int alignCorrectionXPx = FONT_X_CORRECTION;
                if (line.startsWith(".")) {
                    line = line.substring(1);
                    int textLineWidthPx = (int) new StringMetrics(g2).getWidth(line);
                    int boxWidthPx = (int) this.width;
                    alignCorrectionXPx += boxWidthPx / 2 - textLineWidthPx / 2;
                }
                if (line.startsWith("*")) {
                    line = line.substring(1);
                    g.setFont(LookAndFeel.regularFontBold);
                } else if (line.startsWith("_")) {
                    line = line.substring(1);
                    g.setFont(LookAndFeel.regularFontItalic);
                } else {
                    g.setFont(LookAndFeel.regularFont);
                }
                y += fontHeigth;
                g.drawString(line, ix + alignCorrectionXPx, y);
            }
        }
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        g2.setColor(Color.YELLOW);
        g2.fillRect(ix, iy, iwidth, iheight);

        g2.setColor(Color.BLACK);
        g2.setStroke(basicStroke);

        g2.drawRect(ix, iy, iwidth, iheight);

        // ombra fina
        g2.drawLine(ix + iwidth + 1, iy + 1, ix + iwidth + 1, iy + iheight + 1);
        g2.drawLine(ix + 1, iy + iheight + 1, ix + iwidth + 1, iy + iheight + 1);
    }

    @Override
    public Cursor getTranslationCursor() {
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    @Override
    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public void dragHasFinished(List<Shape> elements) {
        this.x = GridControl.engrid(this.x);
        this.y = GridControl.engrid(this.y);
    }

    @Override
    public Draggable findTranslatableByPos(double mousex, double mousey) {
        // TODO borders
        if (this.x <= mousex && mousex <= this.x + this.width && this.y <= mousey && mousey <= this.y + this.height) {
            return this;
        }
        return null;
    }

    @Override
    public void drawSelection(Graphics g) {
        int borderPx = 8;
        var rect = getRectangle();
        rect.grow(borderPx, borderPx);
        g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    @Override
    public int compareTo(Shape o) {
        return -1000;
    }

}