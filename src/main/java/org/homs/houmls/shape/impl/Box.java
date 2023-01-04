package org.homs.houmls.shape.impl;

import org.homs.houmls.GridControl;
import org.homs.houmls.LookAndFeel;
import org.homs.houmls.PropsParser;
import org.homs.houmls.StringMetrics;
import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.homs.houmls.LookAndFeel.BOXES_WITH_SHADOW;
import static org.homs.houmls.LookAndFeel.basicStroke;
import static org.homs.houmls.shape.impl.Connector.SELECTION_BOX_SIZE;

public class Box implements Shape {

    static final int FONT_X_CORRECTION = 5;
    static final int FONT_Y_CORRECTION = 6;

    double x;
    double y;
    double width;
    double height;
    String attributesText;
    String text;

    Color backgroundColor = Color.WHITE;
    int fontSize = LookAndFeel.regularFontSize;

    public Box(int x, int y, int width, int height, String attributesText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setAttributesText(attributesText);
    }

    @Override
    public Shape duplicate() {
        var r = new Box((int) x + DUPLICATE_OFFSET_PX, (int) y + DUPLICATE_OFFSET_PX, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    @Override
    public String getAttributesText() {
        return attributesText;
    }

    @Override
    public void setAttributesText(String attributesText) {

        this.attributesText = attributesText;

        Map<String, String> props = PropsParser.parseProperties(attributesText);
        this.text = props.getOrDefault("", "");

        Color bg = PropsParser.getColorByProp(props, "bg");
        if (bg != null) {
            this.backgroundColor = bg;
        }

        var fontSizeString = props.getOrDefault("fontsize", String.valueOf(LookAndFeel.regularFontSize));
        try {
            this.fontSize = Integer.parseInt(fontSizeString);
        } catch (NumberFormatException e) {
            //
        }
    }

    @Override
    public void draw(Graphics g) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        drawTheBox((Graphics2D) g);

        var g2 = (Graphics2D) g;

        g.setFont(LookAndFeel.regularFont(fontSize));
        int fontHeigth = new StringMetrics(g2).getHeight("aaaAA0");

        String[] textLines = this.text.split("\\n");
        int y = iy;
        for (String line : textLines) {
            if (line.trim().equals("--")) {
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
                    g.setFont(LookAndFeel.regularFontBold(fontSize));
                } else if (line.startsWith("_")) {
                    line = line.substring(1);
                    g.setFont(LookAndFeel.regularFontItalic(fontSize));
                } else {
                    g.setFont(LookAndFeel.regularFont(fontSize));
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

        g2.setColor(backgroundColor);
        g2.fillRect(ix, iy, iwidth, iheight);

        g2.setColor(Color.BLACK);
        g2.setStroke(basicStroke);

        g2.drawRect(ix, iy, iwidth, iheight);

        // ombra fina
        if (BOXES_WITH_SHADOW) {
            g2.drawLine(ix + iwidth + 1, iy + 1, ix + iwidth + 1, iy + iheight + 1);
            g2.drawLine(ix + 1, iy + iheight + 1, ix + iwidth + 1, iy + iheight + 1);
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
//        SELECTION_BOX_SIZE

        // NW
        {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(
                    (int) this.x - SELECTION_BOX_SIZE,
                    (int) this.y - SELECTION_BOX_SIZE,
                    SELECTION_BOX_SIZE * 2,
                    SELECTION_BOX_SIZE * 2);
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(double dx, double dy) {
                        Box.this.x += dx;
                        Box.this.y += dy;
                        Box.this.width -= dx;
                        Box.this.height -= dy;
                    }

                    @Override
                    public void dragHasFinished(List<Shape> elements) {
                    }
                };
            }
        }
        // NE
        {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(
                    (int) (this.x + this.width) - SELECTION_BOX_SIZE,
                    (int) this.y - SELECTION_BOX_SIZE,
                    SELECTION_BOX_SIZE * 2,
                    SELECTION_BOX_SIZE * 2);
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(double dx, double dy) {
                        Box.this.y += dy;
                        Box.this.width += dx;
                        Box.this.height -= dy;
                    }

                    @Override
                    public void dragHasFinished(List<Shape> elements) {
                    }
                };
            }
        }

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
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

}