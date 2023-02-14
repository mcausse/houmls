package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.FontMetrics;
import org.homs.lechugauml.*;
import org.homs.lechugauml.shape.Draggable;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.homs.lechugauml.LookAndFeel.*;
import static org.homs.lechugauml.shape.impl.connector.Connector.SELECTION_BOX_SIZE;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class Box implements Shape {

    static final int FONT_X_CORRECTION = 5;
    static final int FONT_Y_CORRECTION = 6;
    static final double BOX_MIN_SIZE = GridControl.GRID_SIZE * 2;

    double x;
    double y;
    double width;
    double height;
    String attributesText;
    String text;

    Color backgroundColor = Color.WHITE;
    boolean paintBackground = true;

    int fontSize = LookAndFeel.regularFontSize;
    /**
     * if 0 => no shadow
     */
    int shadowWidth = 0;
    Color shadowColor = DEFAULT_SHADOW_COLOR;

    public Box(int x, int y, int width, int height, String attributesText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setAttributesText(attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new Box((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
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
        this.paintBackground = Boolean.parseBoolean(props.getOrDefault("paintbackground", "true"));

        var fontSizeString = props.getOrDefault("fontsize", String.valueOf(LookAndFeel.regularFontSize));
        try {
            this.fontSize = Integer.parseInt(fontSizeString);
        } catch (NumberFormatException e) {
            //
        }

        var shadowString = props.getOrDefault("shadow", DEFAULT_SHADOW_WIDTH);
        this.shadowColor = PropsParser.getColorByProp(props, "shadowcolor", DEFAULT_SHADOW_COLOR);
        try {
            this.shadowWidth = Integer.parseInt(shadowString);
        } catch (NumberFormatException e) {
            //
        }
    }

    @Override
    public void draw(Graphics g) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;

        drawTheBox((Graphics2D) g);

        g.setColor(Color.BLACK);

        var g2 = (Graphics2D) g;

        var regularFont = LookAndFeel.regularFont(fontSize);
        g.setFont(regularFont);
        int fontHeigth = new FontMetrics(g2).getHeight("aaaAA0");

        String[] textLines = this.text.split("\\n");
        int y = iy;
        for (String line : textLines) {
            if (line.trim().equals("--")) {
                y += FONT_Y_CORRECTION;
                g.drawLine(ix, y, ix + iwidth, y);
            } else {

                boolean alignCentered = false;
                if (line.startsWith(".")) {
                    line = line.substring(1);
                    alignCentered = true;
                }

                final Font lineFont;
                if (line.startsWith("*")) {
                    line = line.substring(1);
                    lineFont = LookAndFeel.regularFontBold(fontSize);
                } else if (line.startsWith("_")) {
                    line = line.substring(1);
                    lineFont = LookAndFeel.regularFontItalic(fontSize);
                } else {
                    lineFont = regularFont;
                }
                final FontMetrics fontMetrics = new FontMetrics(g2);
                int textLineWidthPx = (int) fontMetrics.getWidth(line);
                int boxWidthPx = (int) this.width;

                int alignCorrectionXPx = FONT_X_CORRECTION;
                if (alignCentered) {
                    alignCorrectionXPx = (boxWidthPx - textLineWidthPx) / 2;
                }

                y += fontHeigth;

                var monospaceFont = LookAndFeel.monospaceFont(fontSize);
                List<String> parts = PropsParser.split(line, '`');
                int ax = 0;
                for (int i = 0; i < parts.size(); i++) {
                    String part = parts.get(i);
                    if (i % 2 == 0) {
                        g.setFont(lineFont);
                    } else {
                        g.setFont(monospaceFont);
                    }

                    g.drawString(part, ix + alignCorrectionXPx + ax, y);
                    ax += FontMetrics.getWidth(g2, part);
                }
            }
        }
    }

    protected void drawTheBox(Graphics2D g2) {

        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        /*
         * PINTA BACKGROUND
         */
        g2.setColor(backgroundColor);
        g2.fillRect(ix, iy, iwidth, iheight);

        /*
         * PINTA OMBRA DE LA CAIXA
         */
        if (this.shadowWidth > 0) {
            g2.setColor(shadowColor);
            if (this.shadowWidth == 1) {
                g2.drawLine(ix + iwidth + 1, iy + 1, ix + iwidth + 1, iy + iheight + 1);
                g2.drawLine(ix + 1, iy + iheight + 1, ix + iwidth + 1, iy + iheight + 1);
            } else {
                g2.fillRect(ix + iwidth, iy + this.shadowWidth, this.shadowWidth, iheight);
                g2.fillRect(ix + this.shadowWidth, iy + iheight, iwidth, this.shadowWidth);
            }
        }

        /*
         * PINTA BORDE DE CAIXA
         */
        g2.setColor(Color.BLACK);
        g2.setStroke(basicStroke);
        g2.drawRect(ix, iy, iwidth, iheight);
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
    public void translate(Diagram diagram, double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public void dragHasFinished(Diagram diagram) {
        this.x = GridControl.engrid(this.x);
        this.y = GridControl.engrid(this.y);
    }

    @Override
    public Draggable findDraggableByPos(double mousex, double mousey) {

        // S
        {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(
                    (int) this.x,
                    (int) (this.y + this.height - SELECTION_BOX_SIZE),
                    (int) this.width,
                    SELECTION_BOX_SIZE * 2);
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {

                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        if (Box.this.height + dy >= BOX_MIN_SIZE) {

                            // Busca els connectors linkats a aquest objecte abans de canviar de mides
                            diagram.findConnectorsBy(
                                    c -> c.getStartPoint().linkedShape == Box.this && c.getStartPoint().posy > Box.this.height / 2)
                                    .forEach(c -> c.getStartPoint().posy += dy);

                            diagram.findConnectorsBy(
                                    c -> c.getEndPoint().linkedShape == Box.this && c.getEndPoint().posy > Box.this.height / 2)
                                    .forEach(c -> c.getEndPoint().posy += dy);

                            Box.this.height += dy;
                        }
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {
                        Box.this.x = GridControl.engrid(Box.this.x);
                        Box.this.y = GridControl.engrid(Box.this.y);
                        Box.this.width = GridControl.engrid(Box.this.width);
                        Box.this.height = GridControl.engrid(Box.this.height);

                        diagram.findConnectorsBy(
                                c -> c.getStartPoint().linkedShape == Box.this && c.getStartPoint().posy > Box.this.height / 2)
                                .forEach(c -> c.getStartPoint().engrida());

                        diagram.findConnectorsBy(
                                c -> c.getEndPoint().linkedShape == Box.this && c.getEndPoint().posy > Box.this.height / 2)
                                .forEach(c -> c.getEndPoint().engrida());
                    }
                };
            }
        }
        // E
        {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(
                    (int) (this.x + this.width - SELECTION_BOX_SIZE),
                    (int) (this.y),
                    SELECTION_BOX_SIZE * 2,
                    (int) this.height
            );
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {

                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        if (Box.this.width + dx >= BOX_MIN_SIZE) {

                            // Busca els connectors linkats a aquest objecte abans de canviar de mides
                            diagram.findConnectorsBy(
                                    c -> c.getStartPoint().linkedShape == Box.this && c.getStartPoint().posx > Box.this.width / 2)
                                    .forEach(c -> c.getStartPoint().posx += dx);

                            diagram.findConnectorsBy(
                                    c -> c.getEndPoint().linkedShape == Box.this && c.getEndPoint().posx > Box.this.width / 2)
                                    .forEach(c -> c.getEndPoint().posx += dx);

                            Box.this.width += dx;
                        }
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {
                        Box.this.x = GridControl.engrid(Box.this.x);
                        Box.this.y = GridControl.engrid(Box.this.y);
                        Box.this.width = GridControl.engrid(Box.this.width);
                        Box.this.height = GridControl.engrid(Box.this.height);

                        diagram.findConnectorsBy(
                                c -> c.getStartPoint().linkedShape == Box.this && c.getStartPoint().posx > Box.this.width / 2)
                                .forEach(c -> c.getStartPoint().engrida());

                        diagram.findConnectorsBy(
                                c -> c.getEndPoint().linkedShape == Box.this && c.getEndPoint().posx > Box.this.width / 2)
                                .forEach(c -> c.getEndPoint().engrida());
                    }
                };
            }
        }
        // W
        {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(
                    (int) (this.x - SELECTION_BOX_SIZE),
                    (int) (this.y),
                    SELECTION_BOX_SIZE * 2,
                    (int) this.height
            );
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {

                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        if (Box.this.width - dx >= BOX_MIN_SIZE) {

                            // Busca els connectors linkats a aquest objecte abans de canviar de mides
                            diagram.findConnectorsBy(
                                    c -> c.getStartPoint().linkedShape == Box.this)
                                    .forEach(c -> {
                                        if (c.getStartPoint().posx < Box.this.width / 2) {
                                            // ja es mou amb el posx
                                            // c.getStartPoint().posx += dx;
                                        } else {
                                            c.getStartPoint().posx -= dx;
                                        }
                                    });

                            diagram.findConnectorsBy(
                                    c -> c.getEndPoint().linkedShape == Box.this)
                                    .forEach(c -> {
                                        if (c.getEndPoint().posx < Box.this.width / 2) {
                                            // ja es mou amb el posx
                                            // c.getEndPoint().posx += dx;
                                        } else {
                                            c.getEndPoint().posx -= dx;
                                        }
                                    });

                            Box.this.x += dx;
                            Box.this.width -= dx;
                        }
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {
                        Box.this.x = GridControl.engrid(Box.this.x);
                        Box.this.y = GridControl.engrid(Box.this.y);
                        Box.this.width = GridControl.engrid(Box.this.width);
                        Box.this.height = GridControl.engrid(Box.this.height);

                        diagram.findConnectorsBy(
                                c -> c.getStartPoint().linkedShape == Box.this)// && c.getStartPoint().posx < Box.this.width / 2)
                                .forEach(c -> c.getStartPoint().engrida());

                        diagram.findConnectorsBy(
                                c -> c.getEndPoint().linkedShape == Box.this)// && c.getEndPoint().posx < Box.this.width / 2)
                                .forEach(c -> c.getEndPoint().engrida());
                    }
                };
            }
        }

        // N
        {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(
                    (int) (this.x),
                    (int) (this.y - SELECTION_BOX_SIZE),
                    (int) this.width,
                    SELECTION_BOX_SIZE * 2
            );
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {

                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        if (Box.this.height - dy >= BOX_MIN_SIZE) {

                            // Busca els connectors linkats a aquest objecte abans de canviar de mides
                            diagram.findConnectorsBy(
                                    c -> c.getStartPoint().linkedShape == Box.this)
                                    .forEach(c -> {
                                        if (c.getStartPoint().posy < Box.this.height / 2) {
                                            // ja es mou sol
                                        } else {
                                            c.getStartPoint().posy -= dy;
                                        }
                                    });

                            diagram.findConnectorsBy(
                                    c -> c.getEndPoint().linkedShape == Box.this)
                                    .forEach(c -> {
                                        if (c.getEndPoint().posy < Box.this.height / 2) {
                                            // ja es mou sol
                                        } else {
                                            c.getEndPoint().posy -= dy;
                                        }
                                    });

                            Box.this.y += dy;
                            Box.this.height -= dy;
                        }
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {
                        Box.this.x = GridControl.engrid(Box.this.x);
                        Box.this.y = GridControl.engrid(Box.this.y);
                        Box.this.width = GridControl.engrid(Box.this.width);
                        Box.this.height = GridControl.engrid(Box.this.height);

                        diagram.findConnectorsBy(
                                c -> c.getStartPoint().linkedShape == Box.this)// && c.getStartPoint().posy < Box.this.height / 2)
                                .forEach(c -> c.getStartPoint().engrida());

                        diagram.findConnectorsBy(
                                c -> c.getEndPoint().linkedShape == Box.this)// && c.getEndPoint().posy < Box.this.height / 2)
                                .forEach(c -> c.getEndPoint().engrida());
                    }
                };
            }
        }

        // if (this.x <= mousex && mousex <= this.x + this.width && this.y <= mousey && mousey <= this.y + this.height) {
        if (getRectangle().contains(mousex, mousey)) {
            return this;
        }
        return null;
    }

    @Override
    public void drawSelection(Graphics g) {
        int borderPx = 5;
        var rect = getRectangle();
        rect.grow(borderPx, borderPx);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
    }
}