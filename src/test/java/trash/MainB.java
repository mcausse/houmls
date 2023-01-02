package trash;

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

public class MainB {

    public interface Translatable {

        Cursor getTranslationCursor();

        Rectangle getRectangle();

        void translate(int dx, int dy);
    }

    public interface Shape extends Translatable {

        Translatable findTranslatableByPos(double x, double y);

        void draw(Graphics g, int fontHeigth, boolean isSelected);
    }

    public static class Arrow implements Shape {

        Shape fromShape;
        Point fromPoint;

        Shape toShape;
        Point toPoint;

        List<Point> fixedPoints;

        int draggableMarginPx = 6;

        public Arrow(Shape fromShape, Point fromPoint, Shape toShape, Point toPoint) {
            this.fromShape = fromShape;
            this.fromPoint = fromPoint;
            this.toShape = toShape;
            this.toPoint = toPoint;
            this.fixedPoints = new ArrayList<>();
        }

        @Override
        public Cursor getTranslationCursor() {
            return null;
        }

        @Override
        public Rectangle getRectangle() {
            Point from;
            if (fromShape == null) {
                from = new Point(fromPoint);
            } else {
                from = new Point(
                        (int) (fromShape.getRectangle().getX() + fromPoint.getX()),
                        (int) (fromShape.getRectangle().getY() + fromPoint.getY()));
            }
            Point to;
            if (toShape == null) {
                to = new Point(toPoint);
            } else {
                to = new Point(
                        (int) (toShape.getRectangle().getX() + toPoint.getX()),
                        (int) (toShape.getRectangle().getY() + toPoint.getY()));
            }
            return new Rectangle((int) from.getX(), (int) from.getY(), (int) (to.getX() - from.getX()), (int) (to.getY() - from.getY()));
        }

        @Override
        public void translate(int dx, int dy) {

        }

        @Override
        public Translatable findTranslatableByPos(double x, double y) {
            Rectangle r = getRectangle();
            if (r.getX() - draggableMarginPx <= x && x <= r.getX() + draggableMarginPx && r.getY() - draggableMarginPx <= y && y <= r.getY() + draggableMarginPx) {
                return new Translatable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return new Rectangle((int) (r.getX() - draggableMarginPx), (int) (r.getY() - draggableMarginPx), draggableMarginPx * 2, draggableMarginPx * 2);
                    }

                    @Override
                    public void translate(int dx, int dy) {
                        fromPoint.x += dx;
                        fromPoint.y += dy;
                    }
                };
            }
            return null;
        }

        @Override
        public void draw(Graphics g, int fontHeigth, boolean isSelected) {
            int diamantSizePx = 25;
            double mod = Math.sqrt(Math.pow(getRectangle().getHeight(), 2) + Math.pow(getRectangle().getWidth(), 2));
            double angle = Math.atan2(getRectangle().getHeight(), getRectangle().getWidth());


            Rectangle r = getRectangle();
            g.drawLine((int) r.getX(), (int) r.getY(), (int) (r.getX() + mod * Math.cos(angle)), (int) (r.getY() + mod * Math.sin(angle)));
            // TODO
            //g.translate();
        }
    }

    public static class Clas implements Shape {

        public static final int FONT_X_CORRECTION = 3;
        public static final int FONT_Y_CORRECTION = -3;

        final Rectangle rectangle;

        public Clas(Rectangle rectange) {
            this.rectangle = rectange;
        }

        @Override
        public Translatable findTranslatableByPos(double x, double y) {

            int draggableMarginPx = 6;

            // NW
            Rectangle nw = new Rectangle(rectangle.x - draggableMarginPx, rectangle.y - draggableMarginPx, draggableMarginPx * 2, draggableMarginPx * 2);
            if (nw.contains(x, y)) {
                return new Translatable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return nw;
                    }

                    @Override
                    public void translate(int dx, int dy) {
                        rectangle.x += dx;
                        rectangle.y += dy;
                        rectangle.width -= dx;
                        rectangle.height -= dy;
                    }
                };
            }
            // NE
            Rectangle ne = new Rectangle(rectangle.x + rectangle.width - draggableMarginPx, rectangle.y - draggableMarginPx, draggableMarginPx * 2, +draggableMarginPx * 2);
            if (ne.contains(x, y)) {
                return new Translatable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return ne;
                    }

                    @Override
                    public void translate(int dx, int dy) {
//                        rectange.x += dx;
                        rectangle.y += dy;
                        rectangle.width += dx;
                        rectangle.height -= dy;
                    }
                };
            }
            // SW
            Rectangle sw = new Rectangle(rectangle.x - draggableMarginPx, rectangle.y + rectangle.height - draggableMarginPx, draggableMarginPx * 2, draggableMarginPx * 2);
            if (sw.contains(x, y)) {
                return new Translatable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return sw;
                    }

                    @Override
                    public void translate(int dx, int dy) {
                        rectangle.x += dx;
//                        rectange.y += dy;
                        rectangle.width -= dx;
                        rectangle.height += dy;
                    }
                };
            }
            // SE
            Rectangle se = new Rectangle(rectangle.x + rectangle.width - draggableMarginPx, rectangle.y + rectangle.height - draggableMarginPx, draggableMarginPx * 2, draggableMarginPx * 2);
            if (se.contains(x, y)) {
                return new Translatable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return se;
                    }

                    @Override
                    public void translate(int dx, int dy) {
//                        rectange.x += dx;
//                        rectange.y += dy;
                        rectangle.width += dx;
                        rectangle.height += dy;
                    }
                };
            }

            if (rectangle.contains(x, y)) {
                return this;
            }
            return null;
        }

        @Override
        public Rectangle getRectangle() {
            return rectangle;
        }

        @Override
        public Cursor getTranslationCursor() {
            return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }

        @Override
        public void translate(int dx, int dy) {
            rectangle.translate(dx, dy);
        }

        public void draw(Graphics g, int fontHeigth, boolean isSelected) {


            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(1));
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RE);

            var r = rectangle;
            if (isSelected) {
                g.setColor(Color.CYAN);
                g.fillRect(r.x, r.y, r.width, r.height);
            }

            g.setColor(Color.WHITE);
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.BLACK);
            g.drawRect(r.x, r.y, r.width, r.height);

            drawString(g, fontHeigth, 0, LookAndFeel.regularFontBold, "abc");
            drawLineSep(g, fontHeigth, 0);
            drawString(g, fontHeigth, 1, LookAndFeel.regularFont, "xyz");
            drawLineSep(g, fontHeigth, 1);
            drawString(g, fontHeigth, 2, LookAndFeel.regularFont, "012345");
            drawLineSep(g, fontHeigth, 2);

//            int shadowWidth = 2;
//            g.setColor(Color.BLACK);
//            g2.fillRect(r.x + r.width, r.y + shadowWidth, shadowWidth, r.height);
//            g2.fillRect(r.x + shadowWidth, r.y + r.height, r.width, shadowWidth);
        }

        void drawString(Graphics g, int fontHeigth, int rowNum, Font font, String message) {
            var r = rectangle;
            g.setFont(font);
            g.drawString(message, r.x + FONT_X_CORRECTION, r.y + fontHeigth * (rowNum + 1) + FONT_Y_CORRECTION);
        }

        void drawLineSep(Graphics g, int fontHeigth, int rowNum) {
            var r = rectangle;
            g.drawLine(r.x, r.y + fontHeigth * (rowNum + 1), r.x + r.width, r.y + fontHeigth * (rowNum + 1));
        }
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
            Rectangle2D bounds = getBounds(message);
            return bounds.getWidth();
        }

        int getHeight(String message) {
            Rectangle2D bounds = getBounds(message);
            return (int) bounds.getHeight();
        }
    }

    static class LookAndFeel {
        protected static final String regularFontName = Font.SANS_SERIF; // Font.MONOSPACED; //"Courier";
        protected static final int regularFontSize = 12;

        public static final Font regularFont = new Font(regularFontName, Font.PLAIN, regularFontSize);
        public static final Font regularFontBold = new Font(regularFontName, Font.BOLD, regularFontSize);
    }

    public static class Canvas extends JPanel implements CanvasStateListener {

        final List<Shape> shapes = new ArrayList<>();

        double offsetX = 0;
        double offsetY = 0;
        double zoom = 1.0;

        int posx = 0;
        int posy = 0;
        Translatable currentTranslatable;

        public Canvas(MouseAndKeyListener actionsListener) {
            super(true);

            actionsListener.setCanvasStateListener(this);

            addMouseListener(actionsListener);
            addMouseMotionListener(actionsListener);
            addMouseWheelListener(actionsListener);
        }

        @Override
        public Translatable findSelectedTranslatable(int posx, int posy) {
            var at = getAffineTransform();
            Point2D mousePos = null;
            try {
                mousePos = at.inverseTransform(new Point(posx, posy), null);
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
                return null;
            }
            for (int i = shapes.size() - 1; i >= 0; i--) {
                var shape = shapes.get(i);
                var translatable = shape.findTranslatableByPos(mousePos.getX(), mousePos.getY());
                if (translatable != null) {
                    return translatable;
                }
            }
            return null;
        }

        @Override
        public void onOffsetChange(int deltaOffsetX, int deltaOffsetY) {
            offsetX += deltaOffsetX / zoom;
            offsetY += deltaOffsetY / zoom;
            repaint();
        }

        @Override
        public void onMouseDrag(Translatable selectedShape, int deltaX, int deltaY) {
            if (selectedShape == null) {
                // MODIFY THE OFFSET OF ALL THE DRAWING
                offsetX += deltaX / zoom;
                offsetY += deltaY / zoom;
            } else {
                // MOVE THE SELECTED ENTITY
                selectedShape.translate((int) Math.round(deltaX / zoom), (int) Math.round(deltaY / zoom));
            }
            repaint();
        }

        @Override
        public void onRezoom(double deltaZoom) {
            if (zoom + deltaZoom > 0.01) {
                zoom += deltaZoom;
            }
            repaint();
        }

        @Override
        public void onMouseMove(int x, int y) {
            this.posx = x;
            this.posy = y;

            var translatable = findSelectedTranslatable(x, y);
            if (translatable == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                setCursor(translatable.getTranslationCursor());
            }
            this.currentTranslatable = translatable;

            repaint();
        }

        public List<Shape> getShapes() {
            return shapes;
        }

        public void paint(Graphics g) {

            Dimension dim = getSize();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, dim.width, dim.height);

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(1));

            // XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
            // XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b

            //AffineTransform at = g2.getTransform();
            AffineTransform at = getAffineTransform();
            g2.setTransform(at);

            Point2D mousePos = null;
            try {
                mousePos = at.inverseTransform(new Point(posx, posy), null);
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }

            int fontHeigth = new StringMetrics(g2).getHeight("aaa");
            for (var shape : shapes) {
                shape.draw(g, fontHeigth, false /*TODO*/);
            }

            if (this.currentTranslatable != null) {
                var selection = this.currentTranslatable.getRectangle();
                g.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(4));
                g.drawRoundRect((int) selection.getX(), (int) selection.getY(), (int) selection.getWidth(), (int) selection.getHeight(), 8, 8);
            }
        }

        AffineTransform getAffineTransform() {
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
    }

    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var actionsListener = new MouseAndKeyListener();

        var component = new Canvas(actionsListener);

        // TODO
        var c1 = new Clas(new Rectangle(0, 0, 80, 80));
        var c2 = new Clas(new Rectangle(200, 0, 80, 80));
        var a = new Arrow(c1, new Point(0, 0), c2, new Point(0, 0));
        component.getShapes().add(c1);
        component.getShapes().add(c2);
        component.getShapes().add(a);

        // TODO
        for (var i = 3; i < 15; i++) {
            for (var j = 3; j < 15; j++) {
                if (i % 2 == 0 && j % 2 == 0)
                    component.getShapes().add(new Clas(new Rectangle(i * 100, j * 100, 80, 80)));
            }
        }
        component.repaint();

        var f = new JFrame("MartinUML (Houmls)");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        f.add(component);
        f.addKeyListener(actionsListener);
        {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle frameBounds = env.getMaximumWindowBounds();
            f.setSize(new Dimension(frameBounds.width / 2, frameBounds.height));
        }
        f.setVisible(true);
    }
}

interface CanvasStateListener {

    MainB.Translatable findSelectedTranslatable(int posx, int posy);

    void onOffsetChange(int deltaOffsetX, int deltaOffsetY);

    void onMouseDrag(MainB.Translatable selectedShape, int deltaX, int deltaY);

    void onRezoom(double deltaZoom);

    void onMouseMove(int x, int y);
}

class MouseAndKeyListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    public static final int MOUSE_WHEEL_ROTATION_PX_AMOUNT = 50;
    public static final double MOUSE_WHEEL_ROTATION_ZOOM_FACTOR = 0.10;

    MainB.Translatable selectedShape = null;
    Point initialDragPoint = null;
    Point lastDragPoint = null;

    boolean controlPressed = false;
    boolean shiftPressed = false;

    CanvasStateListener canvasStateListener;

    public void setCanvasStateListener(CanvasStateListener canvasStateListener) {
        this.canvasStateListener = canvasStateListener;
    }

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
    public void mouseDragged(MouseEvent e) {
        if (initialDragPoint == null) {
            initialDragPoint = e.getPoint();
            lastDragPoint = e.getPoint();
            selectedShape = canvasStateListener.findSelectedTranslatable(e.getX(), e.getY());
        } else {
            canvasStateListener.onMouseDrag(
                    selectedShape,
                    e.getX() - lastDragPoint.x, e.getY() - lastDragPoint.y
            );
            lastDragPoint.x = e.getX();
            lastDragPoint.y = e.getY();
        }
        canvasStateListener.onMouseMove(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (initialDragPoint != null) {
            selectedShape = null;
            initialDragPoint = null;
            lastDragPoint = null;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (controlPressed) {
            canvasStateListener.onRezoom(-MOUSE_WHEEL_ROTATION_ZOOM_FACTOR * (double) e.getWheelRotation());
        } else {
            if (shiftPressed) {
                canvasStateListener.onOffsetChange(
                        -e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT,
                        0);
            } else {
                canvasStateListener.onOffsetChange(
                        0,
                        -e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // if not dragging an object...
        if (selectedShape == null) {
            canvasStateListener.onMouseMove(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

}