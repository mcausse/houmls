package org.homs.houmls;

import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;
import org.homs.houmls.shape.impl.Connector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.homs.houmls.LookAndFeel.basicStroke;

public class Canvas extends JPanel {

    // TODO canviar a List per permetre multi-sel.lecció
    Shape selectedShape = null;
    String diagramAttributesText = "Welcome to Houmls, the superb and open-source UML tool.";

    class ObjectSelectorListener extends MouseAdapter {

        final JTextArea editorTextPaneRef;

        public ObjectSelectorListener(JTextArea editorTextPaneRef) {
            this.editorTextPaneRef = editorTextPaneRef;
            this.editorTextPaneRef.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update(e);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update(e);
                }

                void update(DocumentEvent e) {
                    if (selectedShape == null) {
                        diagramAttributesText = editorTextPaneRef.getText();
                    } else {
                        selectedShape.setAttributesText(editorTextPaneRef.getText());
                        repaint();
                    }
                }
            });
        }

        @Override
        public void mousePressed(MouseEvent e) {
            selectedShape = findShapeByMousePosition(e.getX(), e.getY());
            if (selectedShape == null) {
                editorTextPaneRef.setText(diagramAttributesText);
            } else {
                editorTextPaneRef.setText(selectedShape.getAttributesText());
            }
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
                var shapeToDuplicate = findShapeByMousePosition(e.getX(), e.getY());
                if (shapeToDuplicate != null) {
                    addElementToTop(shapeToDuplicate.duplicate());
                    repaint();
                }
            }
        }
    }

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
                selectedDraggable = findDraggableByMousePosition(e.getX(), e.getY());
            } else if (selectedDraggable != null) {
                double translateToX = (e.getX() - lastDragPoint.x) / zoom;
                double translateToY = (e.getY() - lastDragPoint.y) / zoom;
                selectedDraggable.translate(translateToX, translateToY);
                lastDragPoint.x = e.getX();
                lastDragPoint.y = e.getY();
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (selectedDraggable != null) {
                selectedDraggable.dragHasFinished(elements);
                repaint();
            }
            lastDragPoint = null;
            selectedDraggable = null;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            var draggable = findDraggableByMousePosition(e.getX(), e.getY());
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
    final JTextArea editorTextPaneRef;

    public Canvas(JTextArea editorTextPaneRef) {
        super(true);

        this.editorTextPaneRef = editorTextPaneRef;

        this.offsetAndZoomListener = new OffsetAndZoomListener();
        addMouseWheelListener(offsetAndZoomListener);
        addKeyListener(offsetAndZoomListener);
        var draggablesListener = new DraggablesListener();
        addMouseListener(draggablesListener);
        addMouseMotionListener(draggablesListener);

        var objectSelectorListener = new ObjectSelectorListener(editorTextPaneRef);
        addMouseListener(objectSelectorListener);
    }

    public void addElement(Shape element) {
        this.elements.add(element);
    }

    public void addElementToTop(Shape element) {
        this.elements.add(0, element);
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

        g2.setStroke(basicStroke);

        AffineTransform at = getAffineTransform();
        g2.setTransform(at);

        drawGrid(g);
        g.setFont(LookAndFeel.regularFontBold);
        int fontHeigth = new StringMetrics(g2).getHeight("aaaAA0");

        if (selectedShape != null) {
            g.setColor(Color.CYAN);
            selectedShape.drawSelection(g);
        }

        // aquesta separació assegura que les fletxes mai siguin tapades per cap caixa
        for (var element : elements) {
            if (!Connector.class.isAssignableFrom(element.getClass())) {
                element.draw(g, fontHeigth);
            }
        }
        for (var element : elements) {
            if (Connector.class.isAssignableFrom(element.getClass())) {
                element.draw(g, fontHeigth);
            }
        }
    }

    void drawGrid(Graphics g) {
        int minx = 0;
        int miny = 0;
        int maxx = 0;
        int maxy = 0;
        if (!elements.isEmpty()) {
            minx = Integer.MAX_VALUE;
            miny = Integer.MAX_VALUE;
            maxx = Integer.MIN_VALUE;
            maxy = Integer.MIN_VALUE;
            for (var element : elements) {
                var rect = element.getRectangle();
                if (minx > rect.getX()) {
                    minx = (int) rect.getX();
                }
                if (miny > rect.getY()) {
                    miny = (int) rect.getY();
                }
                if (maxx < rect.getX() + rect.getWidth()) {
                    maxx = (int) (rect.getX() + rect.getWidth());
                }
                if (maxy < rect.getY() + rect.getHeight()) {
                    maxy = (int) (rect.getY() + rect.getHeight());
                }
            }
        }
        g.setColor(GridControl.GRID_COLOR);
        for (int x = minx - 500; x < maxx + 500; x += GridControl.GRID_SIZE) {
            for (int y = miny - 500; y < maxy + 500; y += GridControl.GRID_SIZE) {
                int gx = GridControl.engrid(x);
                int gy = GridControl.engrid(y);
                g.drawLine(gx, gy, gx, gy);
            }
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

    public Draggable findDraggableByMousePosition(int posx, int posy) {
        var at = getAffineTransform();
        final Point2D mousePos;
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

    public Shape findShapeByMousePosition(int posx, int posy) {
        var at = getAffineTransform();
        final Point2D mousePos;
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
                return shape; // <======================
            }
        }
        return null;
    }

}