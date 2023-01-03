package org.homs.houmls.shape.impl;

import org.homs.houmls.GridControl;
import org.homs.houmls.Turtle;
import org.homs.houmls.shape.Draggable;
import org.homs.houmls.shape.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.homs.houmls.LookAndFeel.basicStroke;
import static org.homs.houmls.shape.impl.Connector.Type.*;

public class Connector implements Shape {

    public static final double DIAMOND_SIZE = 13.0;
    public static final int BOX_EXTRA_LINKABLE_BORDER = GridControl.GRID_SIZE / 2; // 5;
    public static final int SELECTION_BOX_SIZE = 16;


    public enum Type {
        DEFAULT, AGGREGATION, COMPOSITION, ARROW, MEMBER_COMMENT, INHERITANCE,
        //
        // Crow’s Foot Notation
        // https://vertabelo.com/blog/crow-s-foot-notation/
        // http://www2.cs.uregina.ca/~bernatja/crowsfoot.html
        //
        // TODO
        TO_ONE_OPTIONAL, TO_ONE_MANDATORY,
        TO_MANY_OPTIONAL, TO_MANY_MANDATORY
    }

    static class ConnectorPoint {

        public Shape linkedShape;
        public Type type;
        public double posx;
        public double posy;

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
                return new Point((int) (rect.getX() + posx), (int) (rect.getY() + posy));
            }
        }

        public void manageLink(List<Shape> elements) {
            Point p = getAbsolutePoint();
            Shape isLinkedTo = null;
            for (var element : elements) {
                if (element instanceof Connector) {
                    // evita linkar fletxes a altres fletxes!
                    continue;
                }
                var rectangle = element.getRectangle();
                rectangle.grow(BOX_EXTRA_LINKABLE_BORDER, BOX_EXTRA_LINKABLE_BORDER);
                if (rectangle.contains(p.getX(), p.getY())) {
                    isLinkedTo = element;
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

    List<Point> middlePoints;

    String attributesText;

    public Connector(Shape linkedStartShape, Type startType, double startx, double starty, Shape linkedEndShape, Type endType, double endx, double endy) {
        this.startPoint = new ConnectorPoint(linkedStartShape, startType, startx, starty);
        this.endPoint = new ConnectorPoint(linkedEndShape, endType, endx, endy);
        this.middlePoints = new ArrayList<>();
    }

    @Override
    public Shape duplicate() {
        var r = new Connector(
                null, startPoint.type, startPoint.getAbsolutePoint().x + DUPLICATE_OFFSET_PX, startPoint.getAbsolutePoint().y + DUPLICATE_OFFSET_PX,
                null, endPoint.type, endPoint.getAbsolutePoint().x + DUPLICATE_OFFSET_PX, endPoint.getAbsolutePoint().y + DUPLICATE_OFFSET_PX
        );
        r.setAttributesText(attributesText);
        middlePoints.forEach(p -> r.getMiddlePoints().add(new Point(p.x + DUPLICATE_OFFSET_PX, p.y + DUPLICATE_OFFSET_PX)));
        return r;
    }

    public List<Point> getMiddlePoints() {
        return middlePoints;
    }

    @Override
    public String getAttributesText() {
        return attributesText;
    }

    @Override
    public void setAttributesText(String attributesText) {
        this.attributesText = attributesText;
    }

    @Override
    public Draggable findTranslatableByPos(double mousex, double mousey) {


        /*
         * START
         */
        {
            Supplier<Rectangle> boxSupplier = () -> {
                Point p = startPoint.getAbsolutePoint();
                Rectangle box = new Rectangle((int) (p.getX() - SELECTION_BOX_SIZE), (int) (p.getY() - SELECTION_BOX_SIZE), SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
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
                        startPoint.posx += dx;
                        startPoint.posy += dy;
                    }

                    @Override
                    public void dragHasFinished(List<Shape> elements) {

                        // Engrida, excepte si és un comentari de membre, que millor deixar-lo lliure
                        if (startPoint.type != Type.MEMBER_COMMENT && endPoint.type != Type.MEMBER_COMMENT) {
                            startPoint.engrida();
                        }

                        startPoint.manageLink(elements);
                    }
                };
            }
        }
        /*
         * END
         */
        {
            Supplier<Rectangle> boxSupplier = () -> {
                Point p = endPoint.getAbsolutePoint();
                Rectangle box = new Rectangle((int) (p.getX() - SELECTION_BOX_SIZE), (int) (p.getY() - SELECTION_BOX_SIZE), SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
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
                        endPoint.posx += dx;
                        endPoint.posy += dy;
                    }

                    @Override
                    public void dragHasFinished(List<Shape> elements) {

                        // Engrida, excepte si és un comentari de membre, que millor deixar-lo lliure
                        if (startPoint.type != Type.MEMBER_COMMENT && endPoint.type != Type.MEMBER_COMMENT) {
                            endPoint.engrida();
                        }

                        endPoint.manageLink(elements);
                    }
                };
            }
        }

        /*
         * N-MIDDLE POINTS!
         */
        for (var middlePoint : middlePoints) {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle((int) (middlePoint.getX() - SELECTION_BOX_SIZE), (int) (middlePoint.getY() - SELECTION_BOX_SIZE), SELECTION_BOX_SIZE * 2, SELECTION_BOX_SIZE * 2);
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

                    @Override
                    public void dragHasFinished(List<Shape> elements) {
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

    @Override
    public Cursor getTranslationCursor() {
        return null;
    }

    @Override
    public void translate(double dx, double dy) {
    }

    @Override
    public void dragHasFinished(List<Shape> elements) {
    }

    @Override
    public Rectangle getRectangle() {
        Point startp = startPoint.getAbsolutePoint();
        Point endp = endPoint.getAbsolutePoint();

        int minx = (int) Math.min(startp.getX(), endp.getX());
        int maxx = (int) Math.max(startp.getX(), endp.getX());
        int miny = (int) Math.min(startp.getY(), endp.getY());
        int maxy = (int) Math.max(startp.getY(), endp.getY());

        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }

    List<Point> getListOfAbsolutePoints() {
        List<Point> r = new ArrayList<>();
        r.add(startPoint.getAbsolutePoint());
        r.addAll(this.middlePoints);
        r.add(endPoint.getAbsolutePoint());
        return r;
    }

    @Override
    public void draw(Graphics g, int fontHeigth) {

        ((Graphics2D) g).setStroke(basicStroke);

        List<Point> listOfAbsolutePoints = getListOfAbsolutePoints();
        g.setColor(Color.BLACK);
        for (var i = 1; i < listOfAbsolutePoints.size(); i++) {
            var p1 = listOfAbsolutePoints.get(i - 1);
            var p2 = listOfAbsolutePoints.get(i);
            g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
        }

        ((Graphics2D) g).setStroke(basicStroke);

        {
            Point firstPoint = listOfAbsolutePoints.get(0);
            Point secondPoint = listOfAbsolutePoints.get(1);
            double firstToSecondPointAngle = Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX());
            drawEdgeOfArrow(g, startPoint.type, firstPoint, firstToSecondPointAngle);
        }
        {
            Point lastlastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 2);
            Point lastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 1);
            double firstToSecondPointAngle = Math.atan2(lastlastPoint.getY() - lastPoint.getY(), lastlastPoint.getX() - lastPoint.getX());
            drawEdgeOfArrow(g, endPoint.type, lastPoint, firstToSecondPointAngle);
        }
    }

    protected void drawEdgeOfArrow(Graphics g, Type type, Point firstPoint, double angle) {
        switch (type) {
            case DEFAULT:
                break;
            case MEMBER_COMMENT:
                int MEMBER_COMMENT_BOX_RADIUS = 3;
                g.fillRoundRect(firstPoint.x - MEMBER_COMMENT_BOX_RADIUS, firstPoint.y - MEMBER_COMMENT_BOX_RADIUS,
                        MEMBER_COMMENT_BOX_RADIUS * 2, MEMBER_COMMENT_BOX_RADIUS * 2, 2, 2);
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
            case INHERITANCE: {
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                double arrowSize = DIAMOND_SIZE + 5.0;
                double alpha = 35;
                turtle.rotate(-alpha);
                turtle.walk(arrowSize);
                turtle.rotate(90 + alpha);
                turtle.walk(2.0 * arrowSize * Math.sin(Math.toRadians(alpha)));
                turtle.rotate(90 + alpha);
                turtle.walk(arrowSize);
                g.setColor(Color.WHITE);
                turtle.fillPolygon(g);
                g.setColor(Color.BLACK);
                turtle.drawPolyline(g);
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
            g.fillOval((int) p.getX() - borderPx, (int) p.getY() - borderPx, borderPx << 1, borderPx << 1);
        }
    }

    @Override
    public int compareTo(Shape o) {
        return 1000;
    }

}