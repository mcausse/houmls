//package org.homs.houmls;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseMotionListener;
//
//// XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
//// XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b
//public class Main2B {
//
//    public static class Clazz extends JComponent {
//
//        static final int FONT_X_CORRECTION = 5;
//        static final int FONT_Y_CORRECTION = 3;
//
//        int MARGIN_PX = 3;
//
//        String text;
//
//        enum ClassZone {
//            N, E, S, W, CENTER;
//        }
//
//        public ClassZone a(int x, int y) {
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
//                    var p = getLocation();
//                    p.translate(e.getXOnScreen() - lastDraggedPoint.x, e.getYOnScreen() - lastDraggedPoint.y);
//                    setLocation(p);
//                    lastDraggedPoint = new Point(e.getXOnScreen(), e.getYOnScreen());
//                }
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
////                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
////                setLocation(e.getXOnScreen()-150, e.getYOnScreen()-150);
//
//                var zone = a(e.getX(), e.getY());
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
//
//        public Clazz(int x, int y, int width, int heigth, String text) {
//            super();
//            setBounds(x, y, width, heigth);
//            this.text = text;
//            var myListener = new MyListener();
//            addMouseListener(myListener);
//            addMouseMotionListener(myListener);
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//
//            g.setColor(Color.YELLOW);
//            g.fillRect(0, 0, getWidth(), getHeight());
//
//            Graphics2D g2 = (Graphics2D) g;
//            g2.setColor(Color.BLACK);
//            g2.setStroke(new BasicStroke(1));
//            //g2.drawRect(MARGIN_PX, MARGIN_PX, getWidth() - MARGIN_PX * 2, getHeight() - MARGIN_PX * 2);
//            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//
//            int fontHeigth = new MainB.StringMetrics(g2).getHeight("aaa");
//
//            String[] textLines = text.split("\\n");
//            int rownum = 0;
//            for (String line : textLines) {
//                if (line.trim().equals("---")) {
//                    //g.drawLine(MARGIN_PX, MARGIN_PX + (fontHeigth + FONT_Y_CORRECTION) * rownum, getWidth() - MARGIN_PX, (fontHeigth + FONT_Y_CORRECTION) * rownum + MARGIN_PX);
//                    g.drawLine(0, (fontHeigth + FONT_Y_CORRECTION) * rownum - 1, getWidth() - 1, (fontHeigth + FONT_Y_CORRECTION) * rownum - 1);
//                } else {
//                    rownum++;
//                    if (line.startsWith("*")) {
//                        line = line.substring(1);
//                        g.setFont(MainB.LookAndFeel.regularFontBold);
//                    } else {
//                        g.setFont(MainB.LookAndFeel.regularFont);
//                    }
//                    //g.drawString(line, FONT_X_CORRECTION + MARGIN_PX, MARGIN_PX + rownum * (fontHeigth + FONT_Y_CORRECTION) - FONT_Y_CORRECTION);
//                    g.drawString(line, FONT_X_CORRECTION, rownum * (fontHeigth + FONT_Y_CORRECTION) - FONT_Y_CORRECTION);
//                }
//            }
//        }
//    }
//
//    public static class Canvas extends JPanel {
//
//        public Canvas() {
//            super(true);
//            // by doing this, we prevent Swing from resizing
//            // our nice component
//            setLayout(null);
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//
//            Dimension dim = getSize();
//            g.setColor(Color.WHITE);
//            g.fillRect(0, 0, dim.width, dim.height);
//
//            Graphics2D g2 = (Graphics2D) g;
//            g2.setStroke(new BasicStroke(1));
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        JFrame.setDefaultLookAndFeelDecorated(true);
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//
//        var canvas = new Canvas();
//        canvas.add(new Clazz(50, 50, 150, 150, "*jou\n---\njuas\n---"));
//        canvas.add(new Clazz(250, 50, 150, 150, "*11111\n---\n233333\n---"));
//
//        var f = new JFrame("MartinUML (Houmls)");
//        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//        f.add(canvas);
//        {
//            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//            Rectangle frameBounds = env.getMaximumWindowBounds();
//            f.setSize(new Dimension(frameBounds.width / 2, frameBounds.height));
//        }
//        f.setVisible(true);
//    }
//}
