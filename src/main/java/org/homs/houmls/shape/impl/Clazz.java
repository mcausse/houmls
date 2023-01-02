package org.homs.houmls.shape.impl;

import org.homs.houmls.GridControl;
import org.homs.houmls.LookAndFeel;
import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;

import java.awt.*;
import java.util.List;

import static org.homs.houmls.LookAndFeel.basicStroke;

public class Clazz implements org.homs.houmls.shape.Shape {

    @Override
    public int compareTo(org.homs.houmls.shape.Shape o) {
        return -1000;
    }

    static final int FONT_X_CORRECTION = 5;
    static final int FONT_Y_CORRECTION = 6;

//        final int MARGIN_PX = 3;

    double x;
    double y;
    double width;
    double height;
    String text;

//        enum ClassZone {
//            N, E, S, W, CENTER;
//        }
//
//        public ClassZone getClassZone(int x, int y) {
//            if (0 <= x && x <= MARGIN_PX * 2) {
//                return ClassZone.W;
//            }
//            if (getWidth() - MARGIN_PX * 2 <= x && x <= getWidth()) {
//                return ClassZone.E;
//            }
//            if (0 <= y && y <= MARGIN_PX * 2) {
//                return ClassZone.N;
//            }
//            if (getHeight() - MARGIN_PX * 2 <= y && y <= getHeight()) {
//                return ClassZone.S;
//            }
//            return ClassZone.CENTER;
//        }
//
//        class MyListener extends MouseAdapter implements MouseMotionListener {
//            Point lastDraggedPoint = null;
//
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                if (lastDraggedPoint == null) {
//                    lastDraggedPoint = new Point(e.getXOnScreen(), e.getYOnScreen());
////                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                } else {
//                    //setBounds(getX() + e.getX() - lastDraggedPoint.x, getY() + e.getY() - lastDraggedPoint.y, getWidth(), getHeight());
//
//                    var classZone = getClassZone(e.getX(), e.getY());
//                    var deltaX = e.getXOnScreen() - lastDraggedPoint.x;
//                    var deltaY = e.getYOnScreen() - lastDraggedPoint.y;
//                    if (classZone == ClassZone.E) {
//                        setSize(getWidth() + deltaX, getHeight());
//                        //TODO
//                    } else {
//                        var p = getLocation();
//                        p.translate(deltaX, deltaY);
//                        setLocation(p);
//                    }
//                    lastDraggedPoint = new Point(e.getXOnScreen(), e.getYOnScreen());
//                }
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
////                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
////                setLocation(e.getXOnScreen()-150, e.getYOnScreen()-150);
//
//                var zone = getClassZone(e.getX(), e.getY());
//                System.out.println(zone);
//                switch (zone) {
//                    case N:
//                    case S:
//                        setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
//                        break;
//                    case E:
//                    case W:
//                        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
//                        break;
//                    default:
//                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
//                }
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                lastDraggedPoint = null;
////                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
////                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
//            }
//        }

//        public Clazz(int x, int y, int width, int heigth, String text) {
//            super();
////            setBounds(x, y, width, heigth);
//            this.text = text;
////            var myListener = new MyListener();
////            addMouseListener(myListener);
////            addMouseMotionListener(myListener);
//        }

    public Clazz(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
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

        String[] textLines = text.split("\\n");
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
}