package org.homs.houmls;

import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;
import org.homs.houmls.shape.impl.Connector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.homs.houmls.LookAndFeel.basicStroke;
import static org.homs.houmls.shape.impl.Connector.SELECTION_BOX_SIZE;

public class Canvas extends JPanel {

    // TODO canviar a List per permetre multi-sel.lecció
    String diagramAttributesText = "Welcome to Houmls, the superb and open-source UML tool.";
    Shape selectedShape = null;
    Draggable draggableUnderMouse = null;

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
        public void mousePressed(MouseEvent mouseEvent) {
            selectedShape = findShapeByMousePosition(mouseEvent.getX(), mouseEvent.getY());
            if (selectedShape == null) {
                editorTextPaneRef.setText(diagramAttributesText);
            } else {
                editorTextPaneRef.setText(selectedShape.getAttributesText());

                // Popup menu
                if (mouseEvent.getButton() == MouseEvent.BUTTON3) {

                    var at = getAffineTransform();
                    final Point2D mousePos;
                    try {
                        mousePos = at.inverseTransform(new Point(mouseEvent.getX(), mouseEvent.getY()), null);
                    } catch (NoninvertibleTransformException e) {
                        e.printStackTrace();
                        return;
                    }

                    JPopupMenu pm = new JPopupMenu();

                    if (!Connector.class.isAssignableFrom(selectedShape.getClass())) {
                        /*
                         * POPUP MENU: BOX
                         */

                        JMenuItem deleteBox = new JMenuItem("remove");
                        pm.add(deleteBox);
                        deleteBox.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                diagram.getShapes().remove(selectedShape);
                                selectedShape = null;
                                repaint();
                            }
                        });

                        JMenuItem toFront = new JMenuItem("to front");
                        pm.add(toFront);
                        toFront.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                diagram.sendToFront(selectedShape);
                                repaint();
                            }
                        });

                        JMenuItem toBack = new JMenuItem("to back");
                        pm.add(toBack);
                        toBack.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                diagram.sendToBack(selectedShape);
                                repaint();
                            }
                        });

                    } else if (Connector.class.isAssignableFrom(selectedShape.getClass())) {

                        /*
                         * POPUP MENU: CONNECTOR
                         */
                        popupMenuForConnector_CreateMiddlePoint(mousePos, pm);
                        popupMenuForConnector_DeleteMiddlePoint(mousePos, pm);

                        JMenuItem toBack = new JMenuItem("delete connector");
                        pm.add(toBack);
                        toBack.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                diagram.getShapes().remove(selectedShape);
                                selectedShape = null;
                                repaint();
                            }
                        });
                    }


                    pm.show(Canvas.this, mouseEvent.getX(), mouseEvent.getY());
                    pm.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                            repaint();
                        }

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {
                            repaint();
                        }
                    });
                }
            }
            repaint();
        }

        private void popupMenuForConnector_CreateMiddlePoint(Point2D mousePos, JPopupMenu pm) {
            JMenuItem toFront = new JMenuItem("add new point");
            pm.add(toFront);
            toFront.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Connector conn = (Connector) selectedShape;

                    List<Point> points = conn.getListOfAbsolutePoints();
                    for (var i = 0; i < points.size(); i++) {
                        Point p = points.get(i);
                        Rectangle pointSelectionBox = new Rectangle(p.x - SELECTION_BOX_SIZE, p.y - SELECTION_BOX_SIZE, SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
                        if (pointSelectionBox.contains(mousePos.getX(), mousePos.getY())) {

                            /*
                             * CREATES A NEW MIDDLE POINT IN THE CONNECTOR
                             */
                            int indexOfClickedPoint = points.indexOf(p);
                            final Point otherPoint;
                            if (indexOfClickedPoint == points.size() - 1) {
                                otherPoint = points.get(points.size() - 2);
                            } else {
                                otherPoint = points.get(indexOfClickedPoint + 1);
                            }
                            Point middlePointToCreate = new Point(
                                    (p.x + otherPoint.x) / 2,
                                    (p.y + otherPoint.y) / 2
                            );

                            if (indexOfClickedPoint >= conn.getMiddlePoints().size()) {
                                conn.getMiddlePoints().add(middlePointToCreate);
                            } else {
                                conn.getMiddlePoints().add(indexOfClickedPoint, middlePointToCreate);
                            }
                            break;
                        }
                    }


                    repaint();
                }
            });
        }

        private void popupMenuForConnector_DeleteMiddlePoint(Point2D mousePos, JPopupMenu pm) {
            JMenuItem toFront = new JMenuItem("delete point");
            pm.add(toFront);
            toFront.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Connector conn = (Connector) selectedShape;

                    List<Point> points = conn.getListOfAbsolutePoints();
                    if (points.size() <= 2) {
                        return;
                    }
                    for (var i = 0; i < points.size(); i++) {
                        Point p = points.get(i);
                        Rectangle pointSelectionBox = new Rectangle(p.x - SELECTION_BOX_SIZE, p.y - SELECTION_BOX_SIZE, SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
                        if (pointSelectionBox.contains(mousePos.getX(), mousePos.getY())) {

                            if (i == 0) {
                                // remove first point
                                conn.getStartPoint().linkedShape = null;
                                conn.getStartPoint().posx = conn.getMiddlePoints().get(0).x;
                                conn.getStartPoint().posy = conn.getMiddlePoints().get(0).y;
                                conn.getMiddlePoints().remove(0);
                            } else if (i == points.size() - 1) {
                                // remove last point
                                conn.getEndPoint().linkedShape = null;
                                conn.getEndPoint().posx = conn.getMiddlePoints().get(conn.getMiddlePoints().size() - 1).x;
                                conn.getEndPoint().posy = conn.getMiddlePoints().get(conn.getMiddlePoints().size() - 1).y;
                                conn.getMiddlePoints().remove(conn.getMiddlePoints().size() - 1);
                            } else {
                                // remove middle point
                                conn.getMiddlePoints().remove(i - 1);
                            }

                            break;
                        }
                    }


                    repaint();
                }
            });
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
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    double translateToX = (e.getX() - lastDragPoint.x) / diagram.zoom;
                    double translateToY = (e.getY() - lastDragPoint.y) / diagram.zoom;
                    diagram.offsetX += translateToX;
                    diagram.offsetY += translateToY;
                } else {
                    // DRAGGA OBJECTE
                    double translateToX = (e.getX() - lastDragPoint.x) / diagram.zoom;
                    double translateToY = (e.getY() - lastDragPoint.y) / diagram.zoom;
                    selectedDraggable.translate(diagram, translateToX, translateToY);
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
                    selectedDraggable.dragHasFinished(diagram);
                }
                repaint();
            }
            lastDragPoint = null;
            selectedDraggable = null;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            var draggable = findDraggableByMousePosition(e.getX(), e.getY());

            if (Canvas.this.draggableUnderMouse != draggable) {
                repaint();
            }
            Canvas.this.draggableUnderMouse = draggable;

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

        if (Canvas.this.draggableUnderMouse != null) {
            var r = Canvas.this.draggableUnderMouse.getRectangle();
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(r.x, r.y, r.width, r.height, 6, 6);
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
                var draggable = connector.findDraggableByPos(Collections.emptyList(), mousePos.getX(), mousePos.getY());
                if (draggable != null) {
                    return draggable;
                }
            }
        }
        var nonConnectorsList = diagram.getShapesBy(shape -> !Connector.class.isAssignableFrom(shape.getClass()));
        for (var nonconnector : nonConnectorsList) {
            if (!Connector.class.isAssignableFrom(nonconnector.getClass())) {
                var draggable = nonconnector.findDraggableByPos(connectorsList, mousePos.getX(), mousePos.getY());
                if (draggable != null) {
                    return draggable;
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
            var draggable = connector.findDraggableByPos(Collections.emptyList(), mousePos.getX(), mousePos.getY());
            if (draggable != null) {
                return connector; // <======================
            }
        }

        var nonConnectorsList = diagram.getShapesBy(shape -> !Connector.class.isAssignableFrom(shape.getClass()));
        for (var nonconnector : nonConnectorsList) {
            var draggable = nonconnector.findDraggableByPos(connectorsList, mousePos.getX(), mousePos.getY());
            if (draggable != null) {
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