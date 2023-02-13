package org.homs.lechugauml.shape.impl.connector;

import org.homs.lechugauml.FontMetrics;
import org.homs.lechugauml.*;
import org.homs.lechugauml.shape.Draggable;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.homs.lechugauml.LookAndFeel.basicStroke;
import static org.homs.lechugauml.LookAndFeel.dashedStroke;
import static org.homs.lechugauml.shape.impl.connector.ConnectorType.*;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class Connector implements Shape {

    public static final double DIAMOND_SIZE = 13.0;
    public static final int BOX_EXTRA_LINKABLE_BORDER = GridControl.GRID_SIZE - 2;
    public static final int SELECTION_BOX_SIZE = GridControl.GRID_SIZE * 3 / 4;

    final ConnectorPoint startPoint;
    final ConnectorPoint endPoint;
    final List<DoublePoint> middlePoints;

    String attributesText;

    String text = "";
    Stroke stroke = basicStroke;

    public Connector(double startx, double starty, double endx, double endy, String attributes) {
        this.startPoint = new ConnectorPoint(null, DEFAULT, startx, starty);
        this.endPoint = new ConnectorPoint(null, DEFAULT, endx, endy);
        this.middlePoints = new ArrayList<>();
        setAttributesText(attributes);
    }

    public ConnectorPoint getStartPoint() {
        return startPoint;
    }

    public ConnectorPoint getEndPoint() {
        return endPoint;
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new Connector(
                startPoint.getAbsolutePoint().x + translatex, startPoint.getAbsolutePoint().y + translatey,
                endPoint.getAbsolutePoint().x + translatex, endPoint.getAbsolutePoint().y + translatey,
                attributesText
        );
        middlePoints.forEach(p -> r.middlePoints.add(new DoublePoint(p.x + translatex, p.y + translatey)));
        return r;
    }

    public List<DoublePoint> getMiddlePoints() {
        return middlePoints;
    }

    public void manageLink(List<Shape> elements) {
        startPoint.manageLink(elements);
        endPoint.manageLink(elements);
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

        String lt = props.getOrDefault("lt", "-");
        String m1 = props.getOrDefault("m1", "");
        String m2 = props.getOrDefault("m2", "");

        startPoint.text = m1;
        endPoint.text = m2;

        int lineStyleCharacterPos = Math.max(
                lt.indexOf('-'),
                lt.indexOf('.')
        );
        if (lineStyleCharacterPos >= 0) {
            if (lt.charAt(lineStyleCharacterPos) == '-') {
                this.stroke = basicStroke;
            } else {
                this.stroke = dashedStroke;
            }
            String startStyle = lt.substring(0, lineStyleCharacterPos);
            String endStyle = lt.substring(lineStyleCharacterPos + 1);
            String revEndStyle = PropsParser.reverseArrowStyle(endStyle);

            startPoint.type = ConnectorType.findByCode(startStyle);
            endPoint.type = ConnectorType.findByCode(revEndStyle);
        }
    }

    @Override
    public Draggable findDraggableByPos(double mousex, double mousey) {

        /*
         * START
         */
        {
            if (getPointSelectionBox(startPoint).contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return getPointSelectionBox(startPoint);
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        startPoint.posx += dx;
                        startPoint.posy += dy;
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {

                        // Engrida, excepte si és un comentari de membre, que millor deixar-lo lliure
                        if (startPoint.type != ConnectorType.MEMBER_COMMENT && endPoint.type != ConnectorType.MEMBER_COMMENT) {
                            startPoint.engrida();
                        }

                        startPoint.manageLink(diagram.getShapes());
                    }
                };
            }
        }
        /*
         * END
         */
        {
            if (getPointSelectionBox(endPoint).contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return getPointSelectionBox(endPoint);
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        endPoint.posx += dx;
                        endPoint.posy += dy;
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {

                        // Engrida, excepte si és un comentari de membre, que millor deixar-lo lliure
                        if (startPoint.type != ConnectorType.MEMBER_COMMENT && endPoint.type != ConnectorType.MEMBER_COMMENT) {
                            endPoint.engrida();
                        }

                        endPoint.manageLink(diagram.getShapes());
                    }
                };
            }
        }

        /*
         * N-MIDDLE POINTS!
         */
        for (var middlePoint : middlePoints) {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle((int) middlePoint.x - SELECTION_BOX_SIZE, (int) middlePoint.y - SELECTION_BOX_SIZE,
                    SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
            if (boxSupplier.get().contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                    }

                    @Override
                    public Rectangle getRectangle() {
                        return boxSupplier.get();
                    }

                    @Override
                    public void translate(Diagram diagram, double dx, double dy) {
                        middlePoint.translate(dx, dy);
                    }

                    @Override
                    public void dragHasFinished(Diagram diagram) {
                        middlePoint.setLocation(
                                GridControl.engrid(middlePoint.getX()),
                                GridControl.engrid(middlePoint.getY())
                        );
                    }
                };
            }
        }
        return null;
    }

    protected Rectangle getPointSelectionBox(ConnectorPoint connectorPoint) {
        Point p = connectorPoint.getAbsolutePoint();
        return new Rectangle(p.x - SELECTION_BOX_SIZE, p.y - SELECTION_BOX_SIZE, SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
    }

    @Override
    public Cursor getTranslationCursor() {
        return null;
    }

    @Override
    public void translate(Diagram diagram, double dx, double dy) {
        // Traslladar només els middle-points, i els extrems que no estiguin linkats a una box.
        if (startPoint.linkedShape == null) {
            startPoint.translate(dx, dy);
        }
        if (endPoint.linkedShape == null) {
            endPoint.translate(dx, dy);
        }
        middlePoints.forEach(p -> p.translate(dx, dy));
    }

    @Override
    public void dragHasFinished(Diagram diagram) {
        // Engridar només els middle-points, i els extrems que no estiguin linkats a una box.
        if (startPoint.linkedShape == null) {
            startPoint.engrida();
        }
        if (endPoint.linkedShape == null) {
            endPoint.engrida();
        }
        middlePoints.forEach(p ->
                p.setLocation(
                        GridControl.engrid(p.getX()),
                        GridControl.engrid(p.getY())));
    }

    @Override
    public Rectangle getRectangle() {
        Point startp = startPoint.getAbsolutePoint();
        Point endp = endPoint.getAbsolutePoint();

        int minx = Math.min(startp.x, endp.x);
        int maxx = Math.max(startp.x, endp.x);
        int miny = Math.min(startp.y, endp.y);
        int maxy = Math.max(startp.y, endp.y);

        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }

    public List<Point> getListOfAbsolutePoints() {
        List<Point> r = new ArrayList<>();
        r.add(startPoint.getAbsolutePoint());
        for (var doublePoint : getMiddlePoints()) {
            r.add(new Point((int) doublePoint.x, (int) doublePoint.y));
        }
        r.add(endPoint.getAbsolutePoint());
        return r;
    }

    @Override
    public void draw(Graphics g) {

        ((Graphics2D) g).setStroke(this.stroke);

        /*
         * DRAW THE LINE
         */
        List<Point> listOfAbsolutePoints = getListOfAbsolutePoints();
        g.setColor(Color.BLACK);
        for (var i = 1; i < listOfAbsolutePoints.size(); i++) {
            var p1 = listOfAbsolutePoints.get(i - 1);
            var p2 = listOfAbsolutePoints.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        /*
         * DRAW THE CONNECTOR LABEL
         */
        {
            int middlePoint = listOfAbsolutePoints.size() / 2;
            double x = (listOfAbsolutePoints.get(middlePoint - 1).x + listOfAbsolutePoints.get(middlePoint).x) / 2.0;
            double y = (listOfAbsolutePoints.get(middlePoint - 1).y + listOfAbsolutePoints.get(middlePoint).y) / 2.0;

            g.setFont(LookAndFeel.regularFont());

            final FontMetrics fontMetrics = new FontMetrics((Graphics2D) g);
            Rectangle rect = fontMetrics.getBounds(text).getBounds();

            double angle = Math.atan2(
                    (listOfAbsolutePoints.get(middlePoint - 1).y - listOfAbsolutePoints.get(middlePoint).y),
                    (listOfAbsolutePoints.get(middlePoint - 1).x - listOfAbsolutePoints.get(middlePoint).x));
            var turtle = new Turtle(x, y, angle);
            if (Math.sin(angle) <= 0) {
                turtle.rotate(-90);
            } else {
                turtle.rotate(90);
            }
            turtle.walk(DIAMOND_SIZE);
            Point turtlePos = turtle.getPosition();

            String[] textlines = text.split("\\n");
            for (int i = 0; i < textlines.length; i++) {
                final String line = textlines[i];
                int lineWidth = (int) fontMetrics.getWidth(line);
                g.drawString(
                        line,
                        turtlePos.x - lineWidth / 2,
                        turtlePos.y - (rect.height * textlines.length) / 2 + rect.height * (i + 1));
            }
        }

        ((Graphics2D) g).setStroke(basicStroke);

        /*
         * DRAW THE ARROWS & LABELS
         */
        {
            Point firstPoint = listOfAbsolutePoints.get(0);
            Point secondPoint = listOfAbsolutePoints.get(1);
            double firstToSecondPointAngle = Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX());
            drawEdgeOfArrow(g, startPoint.type, firstPoint, firstToSecondPointAngle, startPoint.text);
        }
        {
            Point lastlastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 2);
            Point lastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 1);
            double firstToSecondPointAngle = Math.atan2(lastlastPoint.getY() - lastPoint.getY(), lastlastPoint.getX() - lastPoint.getX());
            drawEdgeOfArrow(g, endPoint.type, lastPoint, firstToSecondPointAngle, endPoint.text);
        }
    }

    protected void drawEdgeOfArrow(Graphics g, ConnectorType type, Point firstPoint, double angle, String text) {

        //
        // LABEL
        //
        {
            g.setFont(LookAndFeel.regularFont());
            var sm = new FontMetrics((Graphics2D) g);
            Rectangle rect = sm.getBounds(text).getBounds();

            var textTurtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
            textTurtle.walk(DIAMOND_SIZE * 2);
            textTurtle.rotate(90);
            textTurtle.walk(DIAMOND_SIZE);
            Point turtlePos = textTurtle.getPosition();
            g.drawString(text, turtlePos.x - rect.width / 2, turtlePos.y + rect.height / 2 - 4);
        }

        switch (type) {
            case DEFAULT:
                break;
            case MEMBER_COMMENT: {
                int MEMBER_COMMENT_BOX_RADIUS = 3;

                g.setColor(Color.BLACK);
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                turtle.walk(GridControl.GRID_SIZE);
                turtle.drawPolyline(g);

                g.setColor(Color.WHITE);
                g.fillOval(firstPoint.x - MEMBER_COMMENT_BOX_RADIUS, firstPoint.y - MEMBER_COMMENT_BOX_RADIUS,
                        MEMBER_COMMENT_BOX_RADIUS * 2, MEMBER_COMMENT_BOX_RADIUS * 2);
                g.setColor(Color.BLACK);
                g.drawOval(firstPoint.x - MEMBER_COMMENT_BOX_RADIUS, firstPoint.y - MEMBER_COMMENT_BOX_RADIUS,
                        MEMBER_COMMENT_BOX_RADIUS * 2, MEMBER_COMMENT_BOX_RADIUS * 2);
            }
            break;
            case AGGREGATION:
            case COMPOSITION: {
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);

                int degreesRomboide = 10;

                turtle.rotate(-45 + degreesRomboide);
                turtle.walk(DIAMOND_SIZE);
                turtle.rotate(90 - degreesRomboide * 2);
                turtle.walk(DIAMOND_SIZE);
                turtle.rotate(90 + degreesRomboide * 2);
                turtle.walk(DIAMOND_SIZE);
                turtle.rotate(90 - degreesRomboide * 2);
                turtle.walk(DIAMOND_SIZE);

                if (type == ConnectorType.AGGREGATION) {
                    g.setColor(Color.WHITE);
                    turtle.fillPolygon(g);
                    g.setColor(Color.BLACK);
                    turtle.drawPolyline(g);
                } else if (type == ConnectorType.COMPOSITION) {
                    g.setColor(Color.BLACK);
                    turtle.fillPolygon(g);
                } else {
                    throw new RuntimeException(type.name());
                }
            }
            break;
            case INHERITANCE:
            case INHERITANCE_BLACKFILLED: {
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                double arrowSize = DIAMOND_SIZE + 5.0;
                double alpha = 35;
                turtle.rotate(-alpha);
                turtle.walk(arrowSize);
                turtle.rotate(90 + alpha);
                turtle.walk(2.0 * arrowSize * Math.sin(Math.toRadians(alpha)));
                turtle.rotate(90 + alpha);
                turtle.walk(arrowSize);
                if (type == INHERITANCE_BLACKFILLED) {
                    g.setColor(Color.BLACK);
                    turtle.fillPolygon(g);
                } else {
                    g.setColor(Color.WHITE);
                    turtle.fillPolygon(g);
                    g.setColor(Color.BLACK);
                    turtle.drawPolyline(g);
                }
            }
            break;
            case ARROW: {
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                turtle.rotate(-35);
                turtle.walk(DIAMOND_SIZE);
                turtle.walk(-DIAMOND_SIZE);
                turtle.rotate(+35 + 35);
                turtle.walk(DIAMOND_SIZE);
                g.setColor(Color.BLACK);
                turtle.drawPolyline(g);
            }
            break;

            case TO_MANY_OPTIONAL:
            case TO_MANY_MANDATORY:
            case TO_ONE_OPTIONAL:
            case TO_ONE_MANDATORY:
                drawCrowsFootNotation(g, type, firstPoint.getX(), firstPoint.getY(), angle);
                break;

            case INNER_CLASS: {
                var innerClassRadiusPx = 10;
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                turtle.jump(innerClassRadiusPx);
                g.setColor(Color.WHITE);
                turtle.fillCircle(g, innerClassRadiusPx);
                g.setColor(Color.BLACK);
                turtle.drawCircle(g, innerClassRadiusPx);

                int plusSignRAdius = innerClassRadiusPx - 3;
                turtle.walk(plusSignRAdius);
                turtle.walk(-plusSignRAdius);
                turtle.rotate(90);
                turtle.walk(plusSignRAdius);
                turtle.walk(-plusSignRAdius);
                turtle.rotate(90);
                turtle.walk(plusSignRAdius);
                turtle.walk(-plusSignRAdius);
                turtle.rotate(90);
                turtle.walk(plusSignRAdius);
                turtle.walk(-plusSignRAdius);
                turtle.drawPolyline(g);
            }
            break;
            case REQUIRED: {
                var requiredRadiusPx = 6 + GridControl.GRID_SIZE / 2;
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                turtle.walk(-requiredRadiusPx);
                Point p = turtle.getPosition();
                g.drawArc(p.x - requiredRadiusPx,
                        p.y - requiredRadiusPx,
                        requiredRadiusPx * 2,
                        requiredRadiusPx * 2,
                        90 - (int) Math.toDegrees(angle), -180);
            }
            break;
            case PROVIDED: {
                var providedRadiusPx = 6;
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                turtle.walk(providedRadiusPx - GridControl.GRID_SIZE / 2);
                g.setColor(Color.WHITE);
                turtle.fillCircle(g, providedRadiusPx);
                g.setColor(Color.BLACK);
                turtle.drawCircle(g, providedRadiusPx);
            }
            break;
            default:
                throw new RuntimeException(type.name());
        }
    }

    protected void drawCrowsFootNotation(Graphics g, ConnectorType type, double posx, double posy, double angle) {

        double verticalSpace = DIAMOND_SIZE * 2.0 / 3.0;
        int degreesRomboide = 10;
        double degreesRomboideSpace = DIAMOND_SIZE / Math.cos(Math.toRadians(45 - degreesRomboide));
        int CIRCLE_RADIUS = 4;

        g.setColor(Color.BLACK);

        //
        // MULTIPLICITAT
        //
        if (type == TO_ONE_OPTIONAL || type == TO_ONE_MANDATORY) {
            var turtle = new Turtle(posx, posy, angle);
            turtle.walk(DIAMOND_SIZE);
            turtle.rotate(90);
            turtle.walk(verticalSpace);
            turtle.walk(-verticalSpace * 2);
            turtle.drawPolyline(g);
        } else if (type == TO_MANY_OPTIONAL || type == TO_MANY_MANDATORY) {
            var turtle = new Turtle(posx, posy, angle);
            turtle.walk(DIAMOND_SIZE);
            turtle.rotate(180 + 45 - degreesRomboide);
            turtle.walk(degreesRomboideSpace);
            turtle.walk(-degreesRomboideSpace);
            turtle.rotate(-(45 - degreesRomboide) * 2);
            turtle.walk(degreesRomboideSpace);
            turtle.drawPolyline(g);
        }

        //
        // MANDATORY/OPTIONAL
        //
        if (type == TO_ONE_OPTIONAL || type == TO_MANY_OPTIONAL) {
            var turtle = new Turtle(posx, posy, angle);
            turtle.walk(DIAMOND_SIZE + CIRCLE_RADIUS);
            if (type == TO_ONE_OPTIONAL) {
                // bonus per separar la barra del cercle
                turtle.walk(CIRCLE_RADIUS);
            }
            g.setColor(Color.WHITE);
            turtle.fillCircle(g, CIRCLE_RADIUS);
            g.setColor(Color.BLACK);
            turtle.drawCircle(g, CIRCLE_RADIUS);
        } else if (type == TO_ONE_MANDATORY || type == TO_MANY_MANDATORY) {
            var turtle = new Turtle(posx, posy, angle);
            turtle.walk(DIAMOND_SIZE);
            if (type == TO_ONE_MANDATORY) {
                // bonus per separar
                turtle.walk(CIRCLE_RADIUS);
            }
            turtle.rotate(90);
            turtle.walk(verticalSpace);
            turtle.walk(-verticalSpace * 2);
            turtle.drawPolyline(g);
        }
    }

    @Override
    public void drawSelection(Graphics g) {
        int borderPx = SELECTION_BOX_SIZE;
        List<Point> listOfAbsolutePoints = getListOfAbsolutePoints();
        for (var p : listOfAbsolutePoints) {
            g.fillOval(p.x - borderPx, p.y - borderPx, borderPx << 1, borderPx << 1);
        }
    }

}