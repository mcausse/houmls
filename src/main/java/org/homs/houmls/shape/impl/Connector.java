package org.homs.houmls.shape.impl;

import org.homs.houmls.*;
import org.homs.houmls.FontMetrics;
import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.homs.houmls.LookAndFeel.basicStroke;
import static org.homs.houmls.LookAndFeel.dashedStroke;
import static org.homs.houmls.shape.impl.Connector.Type.*;

public class Connector implements Shape {

    public static final double DIAMOND_SIZE = 13.0;
    public static final int BOX_EXTRA_LINKABLE_BORDER = GridControl.GRID_SIZE - 2;
    public static final int SELECTION_BOX_SIZE = GridControl.GRID_SIZE * 3 / 4;

    public enum Type {
        DEFAULT(""),
        ARROW("<"),
        INHERITANCE("<<"),
        INHERITANCE_BLACKFILLED("<<<"),
        AGGREGATION("<<<<"),
        COMPOSITION("<<<<<"),
        MEMBER_COMMENT("m"),
        //
        // Crow’s Foot Notation
        // https://vertabelo.com/blog/crow-s-foot-notation/
        // http://www2.cs.uregina.ca/~bernatja/crowsfoot.html
        //
        TO_ONE_OPTIONAL("|o"), TO_ONE_MANDATORY("||"),
        TO_MANY_OPTIONAL(">o"), TO_MANY_MANDATORY(">|"),

        REQUIRED(")"), PROVIDED("o"),

        INNER_CLASS("+");

        private final String code;

        Type(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Type findByCode(String code) {
            for (Type type : Type.values()) {
                if (code.equals(type.getCode())) {
                    return type;
                }
            }
            return DEFAULT; //TODO throw?
        }
    }

    public static class ConnectorPoint {

        public Shape linkedShape;
        public Type type;
        public double posx;
        public double posy;

        public String text = "";

        public ConnectorPoint(Shape linkedShape, Type type, double posx, double posy) {
            this.linkedShape = linkedShape;
            this.type = type;
            this.posx = posx;
            this.posy = posy;
        }

        /**
         * @return les coordenades absolutes del punt, tenint en compte que el punt en si es pot guardar
         * en (posx, posy) en absoluta si no hi ha link amb un component, o bé en relatives al component linkat.
         */
        public Point getAbsolutePoint() {
            if (linkedShape == null) {
                return new Point((int) posx, (int) posy);
            } else {
                var rect = linkedShape.getRectangle();
                return new Point((int) (rect.x + posx), (int) (rect.y + posy));
            }
        }

        public void manageLink(List<Shape> shapes) {
            Point p = getAbsolutePoint();
            Shape isLinkedTo = null;
            for (int i = shapes.size() - 1; i >= 0; i--) {
                var shape = shapes.get(i);
                if (shape instanceof Connector) {
                    // evita linkar fletxes a altres fletxes!
                    continue;
                }
                var rectangle = shape.getRectangle();
                rectangle.grow(BOX_EXTRA_LINKABLE_BORDER, BOX_EXTRA_LINKABLE_BORDER);
                if (rectangle.contains(p.getX(), p.getY())) {
                    isLinkedTo = shape;
                    break;
                }
            }
            // Linka-deslinka
            if (isLinkedTo == null) {
                this.posx = p.getX();
                this.posy = p.getY();
                this.linkedShape = null;
            } else {
                this.posx = p.getX() - isLinkedTo.getRectangle().getX();
                this.posy = p.getY() - isLinkedTo.getRectangle().getY();
                this.linkedShape = isLinkedTo;
            }
        }

        public void engrida() {
            this.posx = GridControl.engrid(this.posx);
            this.posy = GridControl.engrid(this.posy);
        }
    }

    final ConnectorPoint startPoint;
    final ConnectorPoint endPoint;

    final List<Point> middlePoints;

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
    public Shape duplicate() {
        var r = new Connector(
                startPoint.getAbsolutePoint().x + DUPLICATE_OFFSET_PX, startPoint.getAbsolutePoint().y + DUPLICATE_OFFSET_PX,
                endPoint.getAbsolutePoint().x + DUPLICATE_OFFSET_PX, endPoint.getAbsolutePoint().y + DUPLICATE_OFFSET_PX,
                attributesText
        );
        middlePoints.forEach(p -> r.getMiddlePoints().add(new Point(p.x + DUPLICATE_OFFSET_PX, p.y + DUPLICATE_OFFSET_PX)));
        return r;
    }

    public List<Point> getMiddlePoints() {
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

            startPoint.type = Type.findByCode(startStyle);
            endPoint.type = Type.findByCode(revEndStyle);
        }
    }

    @Override
    public Draggable findDraggableByPos(Collection<Shape> connectors, double mousex, double mousey) {

        /*
         * START
         */
        {
//            Supplier<Rectangle> boxSupplier = () -> getPointSelectionBox(startPoint);

            if (getPointSelectionBox(startPoint).contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
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
                        if (startPoint.type != Type.MEMBER_COMMENT && endPoint.type != Type.MEMBER_COMMENT) {
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
//            Supplier<Rectangle> boxSupplier = () -> getPointSelectionBox(endPoint);

            if (getPointSelectionBox(endPoint).contains(mousex, mousey)) {
                return new Draggable() {
                    @Override
                    public Cursor getTranslationCursor() {
                        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
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
                        if (startPoint.type != Type.MEMBER_COMMENT && endPoint.type != Type.MEMBER_COMMENT) {
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
            Supplier<Rectangle> boxSupplier = () -> new Rectangle(middlePoint.x - SELECTION_BOX_SIZE, middlePoint.y - SELECTION_BOX_SIZE,
                    SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
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
                    public void translate(Diagram diagram, double dx, double dy) {
                        middlePoint.translate((int) dx, (int) dy);
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
        Rectangle box = new Rectangle(p.x - SELECTION_BOX_SIZE, p.y - SELECTION_BOX_SIZE, SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
        return box;
    }

    @Override
    public Cursor getTranslationCursor() {
        return null;
    }

    @Override
    public void translate(Diagram diagram, double dx, double dy) {
    }

    @Override
    public void dragHasFinished(Diagram diagram) {
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
        r.addAll(this.middlePoints);
        r.add(endPoint.getAbsolutePoint());
        return r;
    }

    @Override
    public void draw(Graphics g) {

        ((Graphics2D) g).setStroke(this.stroke);

        List<Point> listOfAbsolutePoints = getListOfAbsolutePoints();
        g.setColor(Color.BLACK);
        for (var i = 1; i < listOfAbsolutePoints.size(); i++) {
            var p1 = listOfAbsolutePoints.get(i - 1);
            var p2 = listOfAbsolutePoints.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        //
        // LABEL
        //
        {
            double x = (listOfAbsolutePoints.get(0).x + listOfAbsolutePoints.get(1).x) / 2.0;
            double y = (listOfAbsolutePoints.get(0).y + listOfAbsolutePoints.get(1).y) / 2.0;
            double angle = Math.atan2(y, x);

            g.setFont(LookAndFeel.regularFont());
            var sm = new FontMetrics((Graphics2D) g);
            Rectangle rect = sm.getBounds(text).getBounds();


            var turtle = new Turtle(x, y, angle);
            turtle.rotate(-90);
            turtle.walk(DIAMOND_SIZE * 2); // TODO posicionar depenent de "angle"!

            Point turtlePos = turtle.getPosition();
            g.drawString(text, turtlePos.x - rect.width / 2, turtlePos.y + rect.height / 2 - 4);
        }

        ((Graphics2D) g).setStroke(basicStroke);

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

    protected void drawEdgeOfArrow(Graphics g, Type type, Point firstPoint, double angle, String text) {

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

                if (type == Type.AGGREGATION) {
                    g.setColor(Color.WHITE);
                    turtle.fillPolygon(g);
                    g.setColor(Color.BLACK);
                    turtle.drawPolyline(g);
                } else if (type == Type.COMPOSITION) {
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
                var requiredRadiusPx = 6;
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
                turtle.walk(providedRadiusPx);
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

    public void drawCrowsFootNotation(Graphics g, Type type, double posx, double posy, double angle) {

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