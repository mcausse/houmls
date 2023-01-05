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
import java.util.Collection;
import java.util.Collections;
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
                    addShape(shapeToDuplicate.duplicate());
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
                if (diagram.zoom + deltaZoom > 0.01) {
                    diagram.zoom += deltaZoom;
                }
            } else {
                if (shiftPressed) {
                    diagram.offsetX += (-e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT) / diagram.zoom;
                } else {
                    diagram.offsetY += (-e.getWheelRotation() * MOUSE_WHEEL_ROTATION_PX_AMOUNT) / diagram.zoom;
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
            } else {
                if (selectedDraggable == null) {
                    // DRAGGA TOT
                    double translateToX = (e.getX() - lastDragPoint.x) / diagram.zoom;
                    double translateToY = (e.getY() - lastDragPoint.y) / diagram.zoom;
                    diagram.offsetX += translateToX;
                    diagram.offsetY += translateToY;
                } else {
                    // DRAGGA OBJECTE
                    double translateToX = (e.getX() - lastDragPoint.x) / diagram.zoom;
                    double translateToY = (e.getY() - lastDragPoint.y) / diagram.zoom;
                    selectedDraggable.translate(translateToX, translateToY);
                }
                lastDragPoint.x = e.getX();
                lastDragPoint.y = e.getY();
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (lastDragPoint != null) {
                if (selectedDraggable != null) {
                    selectedDraggable.dragHasFinished(diagram.getShapes());
                }
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


    Diagram diagram = new Diagram();

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

    public void addShape(Shape shape) {
        this.diagram.addShape(shape);
    }

    public void addShapes(Collection<Shape> shapes) {
        this.diagram.addShapes(shapes);
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
        g.setFont(LookAndFeel.regularFont());

//        if (selectedShape != null) {
//            g.setColor(Color.CYAN);
//            selectedShape.drawSelection(g);
//        }

        // aquesta separació assegura que les fletxes mai siguin tapades per cap caixa
        for (var element : diagram.getShapes()) {
            if (!Connector.class.isAssignableFrom(element.getClass())) {
                if (element == selectedShape) {
                    g.setColor(Color.CYAN);
                    selectedShape.drawSelection(g);
                }
                element.draw(g);
            }
        }
        for (var element : diagram.getShapes()) {
            if (Connector.class.isAssignableFrom(element.getClass())) {
                if (element == selectedShape) {
                    g.setColor(Color.CYAN);
                    selectedShape.drawSelection(g);
                }
                element.draw(g);
            }
        }
    }

    void drawGrid(Graphics g) {
        Rectangle diagramBounds = diagram.getDiagramBounds();
        g.setColor(GridControl.GRID_COLOR);
        for (int x = diagramBounds.x - 500; x < diagramBounds.x + diagramBounds.width + 500; x += GridControl.GRID_SIZE) {
            for (int y = diagramBounds.y - 500; y < diagramBounds.y + diagramBounds.height + 500; y += GridControl.GRID_SIZE) {
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
        at.scale(diagram.zoom, diagram.zoom);
        at.translate(-zoomPointX, -zoomPointY);
        at.translate(diagram.offsetX, diagram.offsetY);
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

        List<Shape> connectorsList = diagram.getShapesBy(shape -> Connector.class.isAssignableFrom(shape.getClass()));
        for (var connector : connectorsList) {
            if (Connector.class.isAssignableFrom(connector.getClass())) {
                var translatable = connector.findTranslatableByPos(Collections.emptyList(), mousePos.getX(), mousePos.getY());
                if (translatable != null) {
                    return translatable;
                }
            }
        }
        var nonConnectorsList = diagram.getShapesBy(shape -> !Connector.class.isAssignableFrom(shape.getClass()));
        for (var nonconnector : nonConnectorsList) {
            if (!Connector.class.isAssignableFrom(nonconnector.getClass())) {
                var translatable = nonconnector.findTranslatableByPos(connectorsList, mousePos.getX(), mousePos.getY());
                if (translatable != null) {
                    return translatable;
                }
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

        // TODO evitar duplicar els bucles
        List<Shape> connectorsList = diagram.getShapesBy(shape -> Connector.class.isAssignableFrom(shape.getClass()));
        for (var connector : connectorsList) {
            var translatable = connector.findTranslatableByPos(Collections.emptyList(), mousePos.getX(), mousePos.getY());
            if (translatable != null) {
                return connector; // <======================
            }
        }

        var nonConnectorsList = diagram.getShapesBy(shape -> !Connector.class.isAssignableFrom(shape.getClass()));
        for (var nonconnector : nonConnectorsList) {
            var translatable = nonconnector.findTranslatableByPos(connectorsList, mousePos.getX(), mousePos.getY());
            if (translatable != null) {
                return nonconnector; // <======================
            }
        }
//
//
//        for (int i = diagram.shapes.size() - 1; i >= 0; i--) {
//            var shape = diagram.shapes.get(i);
//            if (Connector.class.isAssignableFrom(shape.getClass())) {
//                var translatable = shape.findTranslatableByPos(diagram.shapes, mousePos.getX(), mousePos.getY());
//                if (translatable != null) {
//                    return shape; // <======================
//                }
//            }
//        }
//
//        for (int i = diagram.shapes.size() - 1; i >= 0; i--) {
//            var shape = diagram.shapes.get(i);
//            if (!Connector.class.isAssignableFrom(shape.getClass())) {
//                var translatable = shape.findTranslatableByPos(diagram.shapes, mousePos.getX(), mousePos.getY());
//                if (translatable != null) {
//                    return shape; // <======================
//                }
//            }
//        }
        return null;
    }


}