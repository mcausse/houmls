package org.homs.houmls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
// XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b
public class MainC1 {

    static class LookAndFeel {
        protected static final String regularFontName = Font.SANS_SERIF; // Font.MONOSPACED; //"Courier";
        protected static final int regularFontSize = 12;

        public static final Font regularFont = new Font(regularFontName, Font.PLAIN, regularFontSize);
        public static final Font regularFontBold = new Font(regularFontName, Font.BOLD, regularFontSize);
        public static final Font regularFontItalic = new Font(regularFontName, Font.ITALIC, regularFontSize);
    }

    static class StringMetrics {

        final Font font;
        final FontRenderContext context;

        public StringMetrics(Graphics2D g2) {
            font = g2.getFont();
            context = g2.getFontRenderContext();
        }

        Rectangle2D getBounds(String message) {
            return font.getStringBounds(message, context);
        }

        double getWidth(String message) {
            Rectangle2D bounds = font.getStringBounds(message, context);
            return bounds.getWidth();
        }

        int getHeight(String message) {
            Rectangle2D bounds = font.getStringBounds(message, context);
            return (int) bounds.getHeight();
        }
    }

    public interface Draggable {

        Cursor getTranslationCursor();

        Rectangle getRectangle();

        void translate(double dx, double dy);
    }

    public interface Shape extends Draggable {

        Draggable findTranslatableByPos(double mousex, double mousey);

        void draw(Graphics g, int fontHeigth);
    }

    public static class Arrow implements Shape {

        public static final double DIAMOND_SIZE = 13.0;

        public enum Type {
            DEFAULT, AGGREGATION, COMPOSITION, ARROW, MEMBER_COMMENT,
            //
            // Crow’s Foot Notation
            // https://vertabelo.com/blog/crow-s-foot-notation/
            // http://www2.cs.uregina.ca/~bernatja/crowsfoot.html
            //
            // TODO
//            TO_ONE_OPTIONAL, TO_ONE_MANDATORY,
//            TO_MANY_OPTIONAL, TO_MANY_MANDATORY
        }

        Shape linkedStartShape;
        Type startType;
        double startx, starty;

        Shape linkedEndShape;
        Type endType;
        double endx, endy;

        List<Point> middlePoints;

        public Arrow(Shape linkedStartShape, Type startType, double startx, double starty, Shape linkedEndShape, Type endType, double endx, double endy) {
            this.linkedStartShape = linkedStartShape;
            this.startType = startType;
            this.startx = startx;
            this.starty = starty;
            this.linkedEndShape = linkedEndShape;
            this.endType = endType;
            this.endx = endx;
            this.endy = endy;
            this.middlePoints = new ArrayList<>();
        }

        public List<Point> getMiddlePoints() {
            return middlePoints;
        }

        @Override
        public Draggable findTranslatableByPos(double mousex, double mousey) {

            int BOX_SIZE = (int) (DIAMOND_SIZE * 2);

            /*
             * START
             */
            {
                Supplier<Rectangle> boxSupplier = () -> {
                    Point p = getStartOrEndPoint(linkedStartShape, startx, starty);
                    Rectangle box = new Rectangle((int) (p.getX() - BOX_SIZE), (int) (p.getY() - BOX_SIZE), BOX_SIZE * 2, BOX_SIZE * 2);
                    return box;
                };
                if (boxSupplier.get().contains(mousex, mousey)) {
                    return new Draggable() {
                        @Override
                        public Cursor getTranslationCursor() {
                            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                        }

                        @Override
                        public Rectangle getRectangle() {
                            return boxSupplier.get();
                        }

                        @Override
                        public void translate(double dx, double dy) {
                            startx += dx;
                            starty += dy;
                        }
                    };
                }
            }
            /*
             * END
             */
            {
                Supplier<Rectangle> boxSupplier = () -> {
                    Point p = getStartOrEndPoint(linkedEndShape, endx, endy);
                    Rectangle box = new Rectangle((int) (p.getX() - BOX_SIZE), (int) (p.getY() - BOX_SIZE), BOX_SIZE * 2, BOX_SIZE * 2);
                    return box;
                };
                if (boxSupplier.get().contains(mousex, mousey)) {
                    return new Draggable() {
                        @Override
                        public Cursor getTranslationCursor() {
                            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                        }

                        @Override
                        public Rectangle getRectangle() {
                            return boxSupplier.get();
                        }

                        @Override
                        public void translate(double dx, double dy) {
                            endx += dx;
                            endy += dy;
                        }
                    };
                }
            }

            /*
             * N-MIDDLE POINTS!
             */
            for (var middlePoint : middlePoints) {
                Supplier<Rectangle> boxSupplier = () -> new Rectangle((int) (middlePoint.getX() - BOX_SIZE), (int) (middlePoint.getY() - BOX_SIZE), BOX_SIZE * 2, BOX_SIZE * 2);
                if (boxSupplier.get().contains(mousex, mousey)) {
                    return new Draggable() {
                        @Override
                        public Cursor getTranslationCursor() {
                            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                        }

                        @Override
                        public Rectangle getRectangle() {
                            return boxSupplier.get();
                        }

                        @Override
                        public void translate(double dx, double dy) {
                            middlePoint.translate((int) dx, (int) dy);
                        }
                    };
                }
            }
            return null;
        }

        @Override
        public Cursor getTranslationCursor() {
            return null;
        }

        @Override
        public void translate(double dx, double dy) {
        }

        @Override
        public Rectangle getRectangle() {
            return new Rectangle((int) startx, (int) starty, (int) (endx - startx), (int) (endy - starty));
        }

        List<Point> getListOfAbsolutePoints() {
            List<Point> r = new ArrayList<>();
            r.add(getStartOrEndPoint(linkedStartShape, startx, starty));
            r.addAll(this.middlePoints);
            r.add(getStartOrEndPoint(linkedEndShape, endx, endy));
            return r;
        }

        Point getStartOrEndPoint(Shape linkedStartShape, double startx, double starty) {
            if (linkedStartShape == null) {
                return new Point((int) startx, (int) starty);
            } else {
                var rect = linkedStartShape.getRectangle();
                return new Point((int) (rect.getX() + startx), (int) (rect.getY() + starty));
            }
        }

        @Override
        public void draw(Graphics g, int fontHeigth) {
            Point p = null;
            List<Point> listOfAbsolutePoints = getListOfAbsolutePoints();
            for (var absolutePoint : listOfAbsolutePoints) {
                if (p != null) {
                    g.drawLine((int) p.getX(), (int) p.getY(), (int) absolutePoint.getX(), (int) absolutePoint.getY());
                }
                p = absolutePoint;
            }

            double sqrmod = Math.sqrt(DIAMOND_SIZE * DIAMOND_SIZE + DIAMOND_SIZE * DIAMOND_SIZE);

            {
                Point firstPoint = listOfAbsolutePoints.get(0);
                Point secondPoint = listOfAbsolutePoints.get(1);
                double firstToSecondPointAngle = Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX());
                drawEdgeOfArrow(g, startType, firstPoint, firstToSecondPointAngle, sqrmod);
            }
            {
                Point lastlastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 2);
                Point lastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 1);
                double firstToSecondPointAngle = Math.atan2(lastlastPoint.getY() - lastPoint.getY(), lastlastPoint.getX() - lastPoint.getX());
                drawEdgeOfArrow(g, endType, lastPoint, firstToSecondPointAngle, sqrmod);
            }
        }

        private void drawEdgeOfArrow(Graphics g, Type type, Point firstPoint, double angle, double sqrmod) {
            switch (type) {
                case DEFAULT:
                    break;
                case MEMBER_COMMENT:
                    int MEMBER_COMMENT_BOX_RADIUS = 3;
                    g.fillRoundRect(firstPoint.x - MEMBER_COMMENT_BOX_RADIUS, firstPoint.y - MEMBER_COMMENT_BOX_RADIUS, MEMBER_COMMENT_BOX_RADIUS * 2, MEMBER_COMMENT_BOX_RADIUS * 2, 2, 2);
                    break;
                case AGGREGATION:
                case COMPOSITION: {
                    var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                    turtle.rotate(-45);
                    turtle.walk(DIAMOND_SIZE);
                    turtle.rotate(90);
                    turtle.walk(DIAMOND_SIZE);
                    turtle.rotate(90);
                    turtle.walk(DIAMOND_SIZE);
                    turtle.rotate(90);
                    turtle.walk(DIAMOND_SIZE);
                    if (type == Type.AGGREGATION) {
                        g.setColor(Color.WHITE);
                        turtle.fillPolygon(g);
                        g.setColor(Color.BLACK);
                        turtle.drawPolyline(g);
                    } else if (type == Type.COMPOSITION) {
                        g.setColor(Color.BLACK);
                        turtle.fillPolygon(g);
                    } else {
                        throw new RuntimeException(startType.name());
                    }
                }
                break;
                case ARROW: {
                    var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                    turtle.rotate(-45);
                    turtle.walk(DIAMOND_SIZE);
                    turtle.walk(-DIAMOND_SIZE);
                    turtle.rotate(90);
                    turtle.walk(DIAMOND_SIZE);
                    g.setColor(Color.BLACK);
                    turtle.drawPolyline(g);
                }
                break;

//                case TO_ONE_OPTIONAL:
//                case TO_ONE_MANDATORY: {
//                    var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
//                    turtle.walk(DIAMOND_SIZE*2);
//                    turtle.rotate(90);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.walk(-DIAMOND_SIZE * 2);
//                    turtle.drawPolyline(g);
//                }
//                break;
//                case TO_MANY_OPTIONAL:
//                case TO_MANY_MANDATORY: {
//                    var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.rotate(135);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.walk(-DIAMOND_SIZE);
//                    turtle.rotate(90);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.walk(-DIAMOND_SIZE);
//                    turtle.drawPolyline(g);
//                }
//                break;
                default:
                    throw new RuntimeException(startType.name());
            }
        }
    }

    public static class Clazz implements Shape {

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
            g2.setStroke(new BasicStroke(1));

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
        public Draggable findTranslatableByPos(double mousex, double mousey) {
            // TODO borders
            if (this.x <= mousex && mousex <= this.x + this.width && this.y <= mousey && mousey <= this.y + this.height) {
                return this;
            }
            return null;
        }
    }

    public static class Canvas extends JPanel {

        final OffsetAndZoomListener offsetAndZoomListener;

        class OffsetAndZoomListener extends KeyAdapter implements MouseWheelListener {

            static final int MOUSE_WHEEL_ROTATION_PX_AMOUNT = 50;
            static final double MOUSE_WHEEL_ROTATION_ZOOM_FACTOR = 0.10;

            boolean controlPressed = false;
            boolean shiftPressed = false;

            @Override
            public void keyPressed(KeyEvent e) {
                controlPressed = e.isControlDown();
                shiftPressed = e.isShiftDown();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                controlPressed = e.isControlDown();
                shiftPressed = e.isShiftDown();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (controlPressed) {
                    var deltaZoom = -MOUSE_WHEEL_ROTATION_ZOOM_FACTOR * (double) e.getWheelRotation();
                    if (zoom + deltaZoom > 0.01) {
                        zoom += deltaZoom;
                    }
                } else {
                    if (shiftPressed) {
                        offsetX += (-e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT) / zoom;
                    } else {
                        offsetY += (-e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT) / zoom;
                    }
                }
                repaint();
            }
        }

        class DraggablesListener extends MouseAdapter implements MouseMotionListener {

            Point lastDragPoint = null;
            Draggable selectedDraggable = null;

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint == null) {
                    lastDragPoint = e.getPoint();
                    selectedDraggable = findSelectedTranslatable(e.getX(), e.getY());
                } else if (selectedDraggable != null) {
                    selectedDraggable.translate((e.getX() - lastDragPoint.x) / zoom, (e.getY() - lastDragPoint.y) / zoom);
                    lastDragPoint.x = e.getX();
                    lastDragPoint.y = e.getY();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//                selectedDraggable.dragHasFinished(elements);
                lastDragPoint = null;
                selectedDraggable = null;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                var draggable = findSelectedTranslatable(e.getX(), e.getY());
                if (draggable == null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } else {
                    if (draggable.getTranslationCursor() != null) {
                        setCursor(draggable.getTranslationCursor());
                    }
                }
            }
        }

        double zoom = 1.0;
        double offsetX = 0.0;
        double offsetY = 0.0;

        final List<Shape> elements = new ArrayList<>();

        public Canvas() {
            super(true);
            offsetAndZoomListener = new OffsetAndZoomListener();
            addMouseWheelListener(offsetAndZoomListener);
            addKeyListener(offsetAndZoomListener);
            var draggablesListener = new DraggablesListener();
            addMouseListener(draggablesListener);
            addMouseMotionListener(draggablesListener);
        }

        public void addElement(Shape element) {
            this.elements.add(element);
        }

        public OffsetAndZoomListener getOffsetAndZoomListener() {
            return offsetAndZoomListener;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Dimension dim = getSize();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, dim.width, dim.height);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

            g2.setStroke(new BasicStroke(1));


            AffineTransform at = getAffineTransform();
            g2.setTransform(at);

            g.setFont(LookAndFeel.regularFontBold);
            int fontHeigth = new StringMetrics(g2).getHeight("aaaAA0");

            for (var element : elements) {
                element.draw(g, fontHeigth);
            }
        }

        public AffineTransform getAffineTransform() {
            Dimension dim = getSize();
            var zoomPointX = dim.width / 2;
            var zoomPointY = dim.height / 2;

            AffineTransform at = new AffineTransform();
            at.translate(zoomPointX, zoomPointY);
            at.scale(zoom, zoom);
            at.translate(-zoomPointX, -zoomPointY);
            at.translate(offsetX, offsetY);
            return at;
        }

        public Draggable findSelectedTranslatable(int posx, int posy) {
            var at = getAffineTransform();
            Point2D mousePos = null;
            try {
                mousePos = at.inverseTransform(new Point(posx, posy), null);
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
                return null;
            }
            for (int i = elements.size() - 1; i >= 0; i--) {
                var shape = elements.get(i);
                var translatable = shape.findTranslatableByPos(mousePos.getX(), mousePos.getY());
                if (translatable != null) {
                    return translatable;
                }
            }
            return null;
        }

    }

    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var canvas = new Canvas();
        Clazz class1 = new Clazz(50, 50, 150, 150, "_<<@Component>>\n*MartinCanvas\n---\nvoid paint(Graphics g)\n---");
        Clazz class2 = new Clazz(250, 50, 150, 150, "*11111\n---\n233333\n---");
        canvas.addElement(class1);
        canvas.addElement(class2);
        Arrow arrow = new Arrow(class1, Arrow.Type.AGGREGATION, 0, 0, class2, Arrow.Type.COMPOSITION, 0, 0);
//        Arrow arrow = new Arrow(class1, Arrow.Type.ARROW, 0, 0, class2, Arrow.Type.ARROW, 0, 0);
//        Arrow arrow = new Arrow(class1, Arrow.Type.MEMBER_COMMENT, 0, 0, class2, Arrow.Type.MEMBER_COMMENT, 0, 0);
//        Arrow arrow = new Arrow(class1, Arrow.Type.TO_ONE_OPTIONAL, 0, 0, class2, Arrow.Type.TO_MANY_OPTIONAL, 0, 0);

        arrow.getMiddlePoints().add(new Point(200, 500));
        canvas.addElement(arrow);

        var f = new JFrame("MartinUML (Houmls)");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.addKeyListener(canvas.getOffsetAndZoomListener());

        f.add(canvas);
        {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle frameBounds = env.getMaximumWindowBounds();
            f.setSize(new Dimension(frameBounds.width / 2, frameBounds.height));
        }
        f.setVisible(true);
    }
}
