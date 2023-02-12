package org.homs.lechugauml;

import org.homs.lechugauml.shape.Draggable;
import org.homs.lechugauml.shape.MultiSelectedGroupDraggable;
import org.homs.lechugauml.shape.Shape;
import org.homs.lechugauml.shape.impl.connector.Connector;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static org.homs.lechugauml.LookAndFeel.SHAPE_SELECTED_COLOR;
import static org.homs.lechugauml.LookAndFeel.basicStroke;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class Canvas extends JPanel {

    final List<Shape> selectedShapes = new ArrayList<>();
    final List<Shape> shapesClipboard;

    int mouseCurrentPosX = 0;
    int mouseCurrentPosY = 0;

    class ObjectSelectorListener extends MouseAdapter {

        final JTextArea editorTextPaneRef;

        public ObjectSelectorListener(JTextArea editorTextPaneRef) {
            this.editorTextPaneRef = editorTextPaneRef;
            this.editorTextPaneRef.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update();
                }

                void update() {
                    if (selectedShapes.isEmpty()) {
                        diagram.setDiagramAttributesText(editorTextPaneRef.getText());
                    } else if (selectedShapes.size() == 1) {
                        selectedShapes.get(0).setAttributesText(editorTextPaneRef.getText());
                        repaint();
                    }
                }
            });
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

            boolean controlPressed = offsetAndZoomListener != null && offsetAndZoomListener.controlPressed;

            var shapeUnderMouse = findShapeByMousePosition(mouseEvent.getX(), mouseEvent.getY());
            if (shapeUnderMouse == null) {
                selectedShapes.clear();
                editorTextPaneRef.setText(diagram.getDiagramAttributesText());
            } else {
                if (controlPressed) {
                    /*
                     * si es clicka sobre una Shape amb Control, s'afegeix/esborra
                     * de la llista de sel.lecció múltiple.
                     */
                    if (selectedShapes.contains(shapeUnderMouse)) {
                        selectedShapes.remove(shapeUnderMouse);
                    } else {
                        selectedShapes.add(shapeUnderMouse);
                    }
                } else {
                    /*
                     * Es clicka una Shape sense Control:
                     * - si forma part de la sel.lecció => no fer res, segurament s'ha començat a draggar!
                     * - si no forma partt de les shapes sel.leccionades, reiniciar la multisel.lecció
                     */
                    if (selectedShapes.contains(shapeUnderMouse)) {
                        // es deu començar a draggar
                    } else {
                        selectedShapes.clear();
                        selectedShapes.add(shapeUnderMouse);
                    }
                }

                if (selectedShapes.size() == 1) {
                    editorTextPaneRef.setText(selectedShapes.get(0).getAttributesText());
                }
            }
            editorTextPaneRef.setCaretPosition(0);

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

                JPopupMenu pm = new CanvasPopupMenuBuilder(Canvas.this).buildPopupMenu(selectedShapes, shapeUnderMouse, mousePos);

                repaint();
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

            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            // en clickar al Canvas, no agafa el focus, podent
            // quedar en el JTextArea i causar pastes raros.
            // => ja faig jo el canvi de focus
            if (e.getSource() == Canvas.this) {
                Canvas.this.requestFocus();
            }

            /*
             * handles the double-click: duplicate
             */
            int DUPLICATE_OFFSET_PX = GridControl.GRID_SIZE * 2;
            if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
                var shapeToDuplicate = findShapeByMousePosition(e.getX(), e.getY());
                if (shapeToDuplicate != null) {
                    addShape(shapeToDuplicate.duplicate(DUPLICATE_OFFSET_PX, DUPLICATE_OFFSET_PX));
                    repaint();
                    pushUndoCheckpoint();
                }
            }
        }
    }

    // ^Z
    final Stack<Diagram> undoStack = new Stack<>();

    // ^Z
    public void pushUndoCheckpoint() {
        this.undoStack.push(diagram.clone());
    }

    final OffsetAndZoomListener offsetAndZoomListener;

    class OffsetAndZoomListener extends KeyAdapter implements MouseWheelListener {

        static final int MOUSE_WHEEL_ROTATION_PX_AMOUNT = 75;
        static final double MOUSE_WHEEL_ROTATION_ZOOM_FACTOR = 0.20;

        boolean controlPressed = false;
        boolean shiftPressed = false;

        @Override
        public void keyPressed(KeyEvent e) {
            controlPressed = e.isControlDown();
            shiftPressed = e.isShiftDown();

            //
            // <<issue>> - Copy-paste from text area causes a copy/paste in the diagram.
            //
            // Evita ^Z/^C/^X/^V si ve del component TextArea de propietats de l'objecte.
            //
            if (e.getSource() != editorTextPaneRef) {

                // ^Z
                if (controlPressed && (e.getKeyCode() == 'z' || e.getKeyCode() == 'Z')) {
                    if (undoStack.size() == 1) {
                        diagram = undoStack.peek().clone();
                    } else {
                        diagram = undoStack.pop();
                    }
                    selectedShapes.clear();
                    diagram.manageConnectorLinks();
                    repaint();
                }

                if (controlPressed && (e.getKeyCode() == 'c' || e.getKeyCode() == 'C' || e.getKeyCode() == 'x' || e.getKeyCode() == 'X')) {

                    // ^C - copy selection
                    if (!selectedShapes.isEmpty()) {
                        shapesClipboard.clear();
                        Rectangle rect = null;
                        for (var shape : selectedShapes) {
                            if (rect == null) {
                                rect = shape.getRectangle();
                            } else {
                                rect = rect.union(shape.getRectangle());
                            }
                        }
                        for (var shape : selectedShapes) {
                            var s = shape.duplicate(-rect.x, -rect.y);
                            shapesClipboard.add(s);
                        }
                    }

                    // ^X - cut selection
                    if (e.getKeyCode() == 'x' || e.getKeyCode() == 'X') {
                        for (var shape : selectedShapes) {
                            diagram.getShapes().remove(shape);
                        }
                        repaint();
                        pushUndoCheckpoint();
                    }
                }

                // ^V - paste clipboard
                if (controlPressed && (e.getKeyCode() == 'v' || e.getKeyCode() == 'V')) {
                    final Point2D mousePos;
                    try {
                        var at = getAffineTransform();
                        mousePos = at.inverseTransform(new Point(mouseCurrentPosX, mouseCurrentPosY), null);
                    } catch (NoninvertibleTransformException nite) {
                        throw new RuntimeException(nite);
                    }
                    selectedShapes.clear();
                    for (var shape : shapesClipboard) {
                        var dupShape = shape.duplicate((int) mousePos.getX(), (int) mousePos.getY());
                        diagram.addShape(dupShape);
                        selectedShapes.add(dupShape);
                        diagram.manageConnectorLinks();
                    }
                    repaint();
                    pushUndoCheckpoint();
                }
            }
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

    protected Rectangle selectionBoxRectangle = null;

    class DraggablesListener extends MouseAdapter implements MouseMotionListener {

        Point firstDragPoint = null;
        Point lastDragPoint = null;
        Draggable selectedDraggable = null;

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDragged(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            boolean controlPressed = offsetAndZoomListener != null && offsetAndZoomListener.controlPressed;

            if (lastDragPoint == null) {
                firstDragPoint = e.getPoint();
                lastDragPoint = e.getPoint();

                if (selectedShapes.size() <= 1) {
                    selectedDraggable = findDraggableByMousePosition(e.getX(), e.getY());
                } else {
                    // lo que s'ha de draggar és
                    // tot lo actualment multi-sel.leccionat!
                    selectedDraggable = new MultiSelectedGroupDraggable(selectedShapes);
                }
            } else {
                if (controlPressed) {
                    var at = getAffineTransform();

                    final Point2D firstPoint;
                    final Point2D lastPoint;
                    try {
                        firstPoint = at.inverseTransform(new Point(firstDragPoint.x, firstDragPoint.y), null);
                        lastPoint = at.inverseTransform(new Point(lastDragPoint.x, lastDragPoint.y), null);
                    } catch (NoninvertibleTransformException nite) {
                        throw new RuntimeException(nite);
                    }

                    var x = (int) (firstPoint.getX());
                    var y = (int) (firstPoint.getY());
                    var width = (int) ((lastPoint.getX() - firstPoint.getX()));
                    var height = (int) ((lastPoint.getY() - firstPoint.getY()));

                    // Un Rectangle no pot tenir width/Height negatius, així que per
                    // a poder dibuixar-lo, es fa aquest canvi de cromos:
                    if (width < 0) {
                        width = -width;
                        x -= width;
                    }
                    if (height < 0) {
                        height = -height;
                        y -= height;
                    }
                    selectionBoxRectangle = new Rectangle(x, y, width, height);
                    setAffectedShapesAsSelected();

                } else if (selectedDraggable == null) {
                    // DRAGGA TOT EL DIAGRAM (BÉ, MODIFICA L'OFFSET)
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

        void setAffectedShapesAsSelected() {
            selectedShapes.clear();
            selectedShapes.addAll(diagram.getShapesBy(shape -> {
                var rect = shape.getRectangle();
                rect.grow(1, 1);
                return rect.intersects(selectionBoxRectangle);
            }));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (selectionBoxRectangle != null) {
                selectionBoxRectangle = null;
                repaint();
                pushUndoCheckpoint();
            }
            if (lastDragPoint != null) {
                if (selectedDraggable != null) {
                    selectedDraggable.dragHasFinished(diagram);
                }
                repaint();
                pushUndoCheckpoint();
            }
            firstDragPoint = null;
            lastDragPoint = null;
            selectedDraggable = null;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            var draggable = findDraggableByMousePosition(e.getX(), e.getY());

            mouseCurrentPosX = e.getX();
            mouseCurrentPosY = e.getY();

            if (draggable == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                if (draggable.getTranslationCursor() != null) {
                    setCursor(draggable.getTranslationCursor());
                }
            }
        }

    }

    private Diagram diagram;
    private final JTextArea editorTextPaneRef;

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
        this.undoStack.clear();
        pushUndoCheckpoint();
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public Canvas(JTextArea editorTextPaneRef, List<Shape> shapesClipboard) {
        super(true);

        this.diagram = new Diagram();
        this.editorTextPaneRef = editorTextPaneRef;
        this.shapesClipboard = shapesClipboard;

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

    public OffsetAndZoomListener getOffsetAndZoomListener() {
        return offsetAndZoomListener;
    }

    public void centerDiagram() {
        Dimension canvasSize = getSize();
        Rectangle diagramBounds = diagram.getDiagramBounds();

        Point canvasCenter = new Point(canvasSize.width / 2, canvasSize.height / 2);
        Point diagramCenter = new Point(diagramBounds.x + diagramBounds.width / 2, diagramBounds.y + diagramBounds.height / 2);

        this.diagram.zoom = 1.0;
        this.diagram.offsetX = (canvasCenter.x - diagramCenter.x);
        this.diagram.offsetY = (canvasCenter.y - diagramCenter.y);

        repaint();
    }

    public void fitZoomToWindow() {
        centerDiagram();

        Dimension canvasSize = getSize();
        Rectangle diagramBounds = diagram.getDiagramBounds();
        diagramBounds.grow(200, 200);

        this.diagram.zoom = canvasSize.getWidth() / diagramBounds.getWidth();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dim = getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, dim.width, dim.height);

        Graphics2D g2 = (Graphics2D) g;

        LookAndFeel.setRenderingHints(g2);

        g2.setStroke(basicStroke);

        AffineTransform at = getAffineTransform();
        g2.setTransform(at);

        if (GridControl.drawGrid) {
            drawGrid(g);
        }

        g.setFont(LookAndFeel.regularFont());

        // aquesta separació assegura que les fletxes mai siguin tapades per cap caixa
        for (var element : diagram.getShapes()) {
            if (!Connector.class.isAssignableFrom(element.getClass())) {
                if (selectedShapes.contains(element)) {
                    g.setColor(SHAPE_SELECTED_COLOR);
                    element.drawSelection(g);
                }
                element.draw(g);
            }
        }
        for (var element : diagram.getShapes()) {
            if (Connector.class.isAssignableFrom(element.getClass())) {
                if (selectedShapes.contains(element)) {
                    g.setColor(SHAPE_SELECTED_COLOR);
                    element.drawSelection(g);
                }
                element.draw(g);
            }
        }

        if (selectionBoxRectangle != null) {
            g2.setColor(Color.BLUE);
            g2.setStroke(LookAndFeel.MULTI_SELECTION_STROKE);
            g2.drawRect(selectionBoxRectangle.x, selectionBoxRectangle.y, selectionBoxRectangle.width, selectionBoxRectangle.height);
        }
    }

    void drawGrid(Graphics g) {
        Rectangle diagramBounds = diagram.getDiagramBounds();
        g.setColor(GridControl.GRID_COLOR);
        diagramBounds.grow(500, 500);
        for (int x = diagramBounds.x; x < diagramBounds.x + diagramBounds.width; x += GridControl.GRID_SIZE) {
            for (int y = diagramBounds.y; y < diagramBounds.y + diagramBounds.height; y += GridControl.GRID_SIZE) {
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
                var draggable = connector.findDraggableByPos(mousePos.getX(), mousePos.getY());
                if (draggable != null) {
                    return draggable;
                }
            }
        }
        var nonConnectorsList = diagram.getShapesBy(shape -> !Connector.class.isAssignableFrom(shape.getClass()));
        for (int i = nonConnectorsList.size() - 1; i >= 0; i--) {
            Shape nonconnector = nonConnectorsList.get(i);
            if (!Connector.class.isAssignableFrom(nonconnector.getClass())) {
                var draggable = nonconnector.findDraggableByPos(mousePos.getX(), mousePos.getY());
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

        List<Shape> connectorsList = diagram.getShapesBy(shape -> Connector.class.isAssignableFrom(shape.getClass()));
        for (var connector : connectorsList) {
            var draggable = connector.findDraggableByPos(mousePos.getX(), mousePos.getY());
            if (draggable != null) {
                return connector; // <======================
            }
        }

        var nonConnectorsList = diagram.getShapesBy(shape -> !Connector.class.isAssignableFrom(shape.getClass()));
        for (int i = nonConnectorsList.size() - 1; i >= 0; i--) {
            Shape nonconnector = nonConnectorsList.get(i);
            var draggable = nonconnector.findDraggableByPos(mousePos.getX(), mousePos.getY());
            if (draggable != null) {
                return nonconnector; // <======================
            }
        }

        return null;
    }

    public Optional<String> getDiagramName() {
        return Optional.ofNullable(diagram.getName());
    }

}