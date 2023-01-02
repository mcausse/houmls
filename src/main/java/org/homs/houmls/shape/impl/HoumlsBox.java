package org.homs.houmls.shape.impl;

import org.homs.houmls.GridControl;
import org.homs.houmls.LookAndFeel;
import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;

import java.awt.*;
import java.util.List;

import static org.homs.houmls.LookAndFeel.basicStroke;

public class HoumlsBox implements Shape {

    static final int FONT_X_CORRECTION = 5;
    static final int FONT_Y_CORRECTION = 6;

    double x;
    double y;
    double width;
    double height;
    String attributesText;

    public HoumlsBox(int x, int y, int width, int height, String attributesText) {
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

        g.setColor(Color.YELLOW);
        g.fillRect(ix, iy, iwidth, iheight);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setStroke(basicStroke);

        g2.drawRect(ix, iy, iwidth, iheight);

        String[] textLines = attributesText.split("\\n");
        int rownum = 0;
        for (String line : textLines) {
            if (line.trim().equals("---")) {
                g.drawLine(ix, iy + (fontHeigth + FONT_Y_CORRECTION) * rownum, ix + iwidth, iy + (fontHeigth + FONT_Y_CORRECTION) * rownum);
            } else {
                rownum++;
                if (line.startsWith("*")) {
                    line = line.substring(1);
                    g.setFont(LookAndFeel.regularFontBold);
                } else if (line.startsWith("_")) {
                    line = line.substring(1);
                    g.setFont(LookAndFeel.regularFontItalic);
                } else {
                    g.setFont(LookAndFeel.regularFont);
                }
                g.drawString(line, ix + FONT_X_CORRECTION, iy + rownum * (fontHeigth + FONT_Y_CORRECTION) - FONT_Y_CORRECTION);
            }
        }
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
        int borderPx=8;
        var rect = getRectangle();
        rect.grow(borderPx, borderPx);
        g.fillRect((int) rect.getX(), (int) rect.getY() , (int) rect.getWidth(), (int) rect.getHeight() );
    }

    @Override
    public int compareTo(Shape o) {
        return -1000;
    }

}