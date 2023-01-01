//package org.homs.houmls;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.font.FontRenderContext;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.NoninvertibleTransformException;
//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Main8 {
//
//    public interface Shape {
//
//        Rectangle getRectange();
//
//        void draw(Graphics g, int fontHeigth, boolean isSelected);
//    }
//
//    public static class Clas implements Shape {
//
//        public static final int X_CORRECTION = 3;
//        public static final int Y_CORRECTION = -3;
//
//        final Rectangle rectange;
//
//        public Clas(Rectangle rectange) {
//            this.rectange = rectange;
//        }
//
//        public Rectangle getRectange() {
//            return rectange;
//        }
//
//        public void draw(Graphics g, int fontHeigth, boolean isSelected) {
//
//            Graphics2D g2 = (Graphics2D) g;
//            g2.setStroke(new BasicStroke(1));
//            if (isSelected) {
//                g.setColor(Color.RED);
//            } else {
//                g.setColor(Color.BLACK);
//            }
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            var r = getRectange();
//            g.drawRect(r.x, r.y, r.width, r.height);
//
//            drawString(g, fontHeigth, 0, LookAndFeel.regularFontBold, "abc");
//            drawLineSep(g, fontHeigth, 0);
//            drawString(g, fontHeigth, 1, LookAndFeel.regularFont, "xyz");
//            drawLineSep(g, fontHeigth, 1);
//            drawString(g, fontHeigth, 2, LookAndFeel.regularFont, "012345");
//            drawLineSep(g, fontHeigth, 2);
//        }
//
//        void drawString(Graphics g, int fontHeigth, int rowNum, Font font, String message) {
//            var r = getRectange();
//            g.setFont(font);
//            g.drawString(message, r.x + X_CORRECTION, r.y + fontHeigth * (rowNum + 1) + Y_CORRECTION);
//        }
//
//        void drawLineSep(Graphics g, int fontHeigth, int rowNum) {
//            var r = getRectange();
//            g.drawLine(r.x, r.y + fontHeigth * (rowNum + 1), r.x + r.width, r.y + fontHeigth * (rowNum + 1));
//        }
//    }
//
//    static class StringMetrics {
//
//        final Font font;
//        final FontRenderContext context;
//
//        public StringMetrics(Graphics2D g2) {
//            font = g2.getFont();
//            context = g2.getFontRenderContext();
//        }
//
//        Rectangle2D getBounds(String message) {
//            return font.getStringBounds(message, context);
//        }
//
//        double getWidth(String message) {
//            Rectangle2D bounds = getBounds(message);
//            return bounds.getWidth();
//        }
//
//        int getHeight(String message) {
//            Rectangle2D bounds = getBounds(message);
//            return (int) bounds.getHeight();
//        }
//    }
//
//    static class LookAndFeel {
//        protected static final String regularFontName = Font.SANS_SERIF; // Font.MONOSPACED; //"Courier";
//        protected static final int regularFontSize = 12;
//
//        public static final Font regularFont = new Font(regularFontName, Font.PLAIN, regularFontSize);
//        public static final Font regularFontBold = new Font(regularFontName, Font.BOLD, regularFontSize);
//    }
//
//    public static class Canvas extends JPanel implements CanvasStateListener {
//
//        final List<Shape> shapes = new ArrayList<>();
//
//        double offsetX = 0;
//        double offsetY = 0;
//        double zoom = 1.0;
//
//        int posx = 0;
//        int posy = 0;
//
//        public Canvas(MouseAndKeyListener actionsListener) {
//            super(true);
//
//            actionsListener.addCanvasStateListener(this);
//
//            addMouseListener(actionsListener);
//            addMouseMotionListener(actionsListener);
//            addMouseWheelListener(actionsListener);
//        }
//
//        @Override
//        public void onChange(int deltaOffsetX, int deltaOffsetY, double deltaZoom) {
//            if (zoom + deltaZoom > 0.01) {
//                zoom += deltaZoom;
//            }
//            offsetX += deltaOffsetX / zoom;
//            offsetY += deltaOffsetY / zoom;
//            repaint();
//        }
//
//        @Override
//        public void onMove(int x, int y) {
//            this.posx = x;
//            this.posy = y;
//            repaint();
//        }
//
//        public List<Shape> getShapes() {
//            return shapes;
//        }
//
//        public void paint(Graphics g) {
//
//            Dimension dim = getSize();
//            g.setColor(Color.WHITE);
//            g.fillRect(0, 0, dim.width, dim.height);
//
//            Graphics2D g2 = (Graphics2D) g;
//
//            // XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
//            // XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b
//
//            var zoomPointX = dim.width / 2.0;
//            var zoomPointY = dim.height / 2.0;
//
//            AffineTransform at = g2.getTransform();
//            at.translate(zoomPointX, zoomPointY);
//            at.scale(zoom, zoom);
//            at.translate(-zoomPointX, -zoomPointY);
//            at.translate(offsetX, offsetY);
//            g2.setTransform(at);
//
//
//            Point2D mousePos = null;
//            try {
//                mousePos = at.inverseTransform(new Point(posx, posy), null);
//            } catch (NoninvertibleTransformException e) {
//                e.printStackTrace();
//            }
//
//
//            int fontHeigth = new StringMetrics(g2).getHeight("aaa");
//            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            for (var shape : shapes) {
//                boolean isSelected = false;
//                if (mousePos != null) {
//                    isSelected = shape.getRectange().contains(mousePos.getX(), mousePos.getY());
//                    if (isSelected) {
//                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
//                    }
//                }
//                shape.draw(g, fontHeigth, isSelected);
//            }
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        JFrame.setDefaultLookAndFeelDecorated(true);
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//
//        var actionsListener = new MouseAndKeyListener();
//
//        var component = new Canvas(actionsListener);
//
//        // TODO
//        for (var i = 3; i < 15; i++) {
//            for (var j = 3; j < 15; j++) {
//                component.getShapes().add(new Clas(new Rectangle(i * 100, j * 100, 80, 80))); // TODO
//            }
//        }
//        component.repaint();
//
//        var f = new JFrame();
//        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        f.setLayout(new BorderLayout());
//        f.add(component);
//        f.addKeyListener(actionsListener);
//        {
//            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//            Rectangle frameBounds = env.getMaximumWindowBounds();
//            f.setSize(new Dimension(frameBounds.width / 2, frameBounds.height));
//        }
//        f.setVisible(true);
//    }
//}
//
//interface CanvasStateListener {
//    void onChange(int deltaOffsetX, int deltaOffsetY, double deltaZoom);
//
//    void onMove(int x, int y);
//}
//
//class MouseAndKeyListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
//
//    public static final int MOUSE_WHEEL_ROTATION_PX_AMOUNT = 50;
//    public static final double MOUSE_WHEEL_ROTATION_ZOOM_FACTOR = 0.10;
//
//    Point drag = null;
//
//    boolean controlPressed = false;
//    boolean shiftPressed = false;
//
//    final List<CanvasStateListener> canvasStateListeners = new ArrayList<>();
//
//    public void addCanvasStateListener(CanvasStateListener canvasStateListener) {
//        this.canvasStateListeners.add(canvasStateListener);
//    }
//
//    @Override
//    public void keyPressed(KeyEvent e) {
//        controlPressed = e.isControlDown();
//        shiftPressed = e.isShiftDown();
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        controlPressed = e.isControlDown();
//        shiftPressed = e.isShiftDown();
//    }
//
//    @Override
//    public void mouseDragged(MouseEvent e) {
//        if (drag == null) {
//            drag = e.getPoint();
//        } else {
//            canvasStateListeners.forEach(canvasStateListener ->
//                    canvasStateListener.onChange(
//                            e.getX() - drag.x,
//                            e.getY() - drag.y,
//                            0.0));
//            drag.x = e.getX();
//            drag.y = e.getY();
//        }
//        canvasStateListeners.forEach(canvasStateListener ->
//                canvasStateListener.onMove(e.getX(), e.getY()));
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//        if (drag != null) {
//            drag = null;
//        }
//    }
//
//    @Override
//    public void mouseWheelMoved(MouseWheelEvent e) {
//        if (controlPressed) {
//            canvasStateListeners.forEach(canvasStateListener ->
//                    canvasStateListener.onChange(
//                            0,
//                            0,
//                            -MOUSE_WHEEL_ROTATION_ZOOM_FACTOR * (double) e.getWheelRotation()));
//        } else {
//            if (shiftPressed) {
//                canvasStateListeners.forEach(canvasStateListener ->
//                        canvasStateListener.onChange(
//                                -e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT,
//                                0,
//                                0.0));
//            } else {
//                canvasStateListeners.forEach(canvasStateListener ->
//                        canvasStateListener.onChange(
//                                0,
//                                -e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT,
//                                0.0));
//            }
//        }
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        canvasStateListeners.forEach(canvasStateListener ->
//                canvasStateListener.onMove(e.getX(), e.getY()));
//    }
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//
//    }
//
//}