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
import static org.homs.houmls.LookAndFeel.dashedStroke;

public class Arrow implements org.homs.houmls.shape.Shape {

    public static final double DIAMOND_SIZE = 13.0;
    public static final int BOX_EXTRA_LINKABLE_BORDER = 5;

    @Override
    public int compareTo(org.homs.houmls.shape.Shape o) {
        return 1000;
    }

    public enum Type {
        DEFAULT, AGGREGATION, COMPOSITION, ARROW, MEMBER_COMMENT, INHERITANCE
        //
        // Crow’s Foot Notation
        // https://vertabelo.com/blog/crow-s-foot-notation/
        // http://www2.cs.uregina.ca/~bernatja/crowsfoot.html
        //
        // TODO
//            TO_ONE_OPTIONAL, TO_ONE_MANDATORY,
//            TO_MANY_OPTIONAL, TO_MANY_MANDATORY
    }

    org.homs.houmls.shape.Shape linkedStartShape;
    Type startType;
    double startx, starty;

    org.homs.houmls.shape.Shape linkedEndShape;
    Type endType;
    double endx, endy;

    List<Point> middlePoints;

    public Arrow(org.homs.houmls.shape.Shape linkedStartShape, Type startType, double startx, double starty, org.homs.houmls.shape.Shape linkedEndShape, Type endType, double endx, double endy) {
        this.linkedStartShape = linkedStartShape;
        this.startType = startType;
        this.startx = startx;
        this.starty = starty;
        this.linkedEndShape = linkedEndShape;
        this.endType = endType;
        this.endx = endx;
        this.endy = endy;
        this.middlePoints = new ArrayList<>();
    }

    public List<Point> getMiddlePoints() {
        return middlePoints;
    }

    @Override
    public Draggable findTranslatableByPos(double mousex, double mousey) {

        int BOX_SIZE = (int) (DIAMOND_SIZE * 2);

        /*
         * START
         */
        {
            Supplier<Rectangle> boxSupplier = () -> {
                Point p = getAbsolutePoint(linkedStartShape, startx, starty);
                Rectangle box = new Rectangle((int) (p.getX() - BOX_SIZE), (int) (p.getY() - BOX_SIZE), BOX_SIZE * 2, BOX_SIZE * 2);
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
                        startx += dx;
                        starty += dy;
                    }

                    @Override
                    public void dragHasFinished(List<org.homs.houmls.shape.Shape> elements) {
                        var p = getAbsolutePoint(linkedStartShape, startx, starty);
                        org.homs.houmls.shape.Shape isLinkedTo = null;
                        for (var element : elements) {
                            var rectangle = element.getRectangle();
                            rectangle.grow(BOX_EXTRA_LINKABLE_BORDER, BOX_EXTRA_LINKABLE_BORDER);
                            if (rectangle.contains(p.getX(), p.getY())) {
                                isLinkedTo = element;
                                break;
                            }
                        }
                        // Linka-deslinka
                        if (isLinkedTo == null) {
                            startx = p.getX();
                            starty = p.getY();
                            linkedStartShape = null;
                        } else {
                            startx = p.getX() - isLinkedTo.getRectangle().getX();
                            starty = p.getY() - isLinkedTo.getRectangle().getY();
                            linkedStartShape = isLinkedTo;
                        }

                        // Engrida, excepte si és un comentari de membre, que millor deixar-lo lliure
                        if (startType != Type.MEMBER_COMMENT) {
                            startx = GridControl.engrid(startx);
                            starty = GridControl.engrid(starty);
                        }
                    }
                };
            }
        }
        /*
         * END
         */
        {
            Supplier<Rectangle> boxSupplier = () -> {
                Point p = getAbsolutePoint(linkedEndShape, endx, endy);
                Rectangle box = new Rectangle((int) (p.getX() - BOX_SIZE), (int) (p.getY() - BOX_SIZE), BOX_SIZE * 2, BOX_SIZE * 2);
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
                        endx += dx;
                        endy += dy;
                    }

                    @Override
                    public void dragHasFinished(List<org.homs.houmls.shape.Shape> elements) {
                        var p = getAbsolutePoint(linkedEndShape, endx, endy);
                        org.homs.houmls.shape.Shape isLinkedTo = null;
                        for (var element : elements) {
                            var rectangle = element.getRectangle();
                            rectangle.grow(BOX_EXTRA_LINKABLE_BORDER, BOX_EXTRA_LINKABLE_BORDER);
                            if (rectangle.contains(p.getX(), p.getY())) {

                                isLinkedTo = element;
                                break;
                            }
                        }
                        // Linka-deslinka
                        if (isLinkedTo == null) {
                            endx = p.getX();
                            endy = p.getY();
                            linkedEndShape = null;
                        } else {
                            endx = p.getX() - isLinkedTo.getRectangle().getX();
                            endy = p.getY() - isLinkedTo.getRectangle().getY();
                            linkedEndShape = isLinkedTo;
                        }

                        // Engrida, excepte si és un comentari de membre, que millor deixar-lo lliure
                        if (startType != Type.MEMBER_COMMENT) {
                            endx = GridControl.engrid(endx);
                            endy = GridControl.engrid(endy);
                        }
                    }
                };
            }
        }

        /*
         * N-MIDDLE POINTS!
         */
        for (var middlePoint : middlePoints) {
            Supplier<Rectangle> boxSupplier = () -> new Rectangle((int) (middlePoint.getX() - BOX_SIZE), (int) (middlePoint.getY() - BOX_SIZE), BOX_SIZE * 2, BOX_SIZE * 2);
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
                    public void dragHasFinished(List<org.homs.houmls.shape.Shape> elements) {
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
    public void dragHasFinished(List<org.homs.houmls.shape.Shape> elements) {
    }

    @Override
    public Rectangle getRectangle() {
        return new Rectangle((int) startx, (int) starty, (int) (endx - startx), (int) (endy - starty));
    }

    List<Point> getListOfAbsolutePoints() {
        List<Point> r = new ArrayList<>();
        r.add(getAbsolutePoint(linkedStartShape, startx, starty));
        r.addAll(this.middlePoints);
        r.add(getAbsolutePoint(linkedEndShape, endx, endy));
        return r;
    }

    Point getAbsolutePoint(Shape linkedStartShape, double startx, double starty) {
        if (linkedStartShape == null) {
            return new Point((int) startx, (int) starty);
        } else {
            var rect = linkedStartShape.getRectangle();
            return new Point((int) (rect.getX() + startx), (int) (rect.getY() + starty));
        }
    }

    @Override
    public void draw(Graphics g, int fontHeigth) {

        ((Graphics2D) g).setStroke(dashedStroke);

        Point p = null;
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
            drawEdgeOfArrow(g, startType, firstPoint, firstToSecondPointAngle);
        }
        {
            Point lastlastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 2);
            Point lastPoint = listOfAbsolutePoints.get(listOfAbsolutePoints.size() - 1);
            double firstToSecondPointAngle = Math.atan2(lastlastPoint.getY() - lastPoint.getY(), lastlastPoint.getX() - lastPoint.getX());
            drawEdgeOfArrow(g, endType, lastPoint, firstToSecondPointAngle);
        }
    }

    private void drawEdgeOfArrow(Graphics g, Type type, Point firstPoint, double angle) {
        switch (type) {
            case DEFAULT:
                break;
            case MEMBER_COMMENT:
                int MEMBER_COMMENT_BOX_RADIUS = 3;
                g.fillRoundRect(firstPoint.x - MEMBER_COMMENT_BOX_RADIUS, firstPoint.y - MEMBER_COMMENT_BOX_RADIUS, MEMBER_COMMENT_BOX_RADIUS * 2, MEMBER_COMMENT_BOX_RADIUS * 2, 2, 2);
                break;
            case AGGREGATION:
            case COMPOSITION: {
                var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
                turtle.rotate(-45);
                turtle.walk(DIAMOND_SIZE);
                turtle.rotate(90);
                turtle.walk(DIAMOND_SIZE);
                turtle.rotate(90);
                turtle.walk(DIAMOND_SIZE);
                turtle.rotate(90);
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
                    throw new RuntimeException(startType.name());
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
                turtle.rotate(-45);
                turtle.walk(DIAMOND_SIZE);
                turtle.walk(-DIAMOND_SIZE);
                turtle.rotate(90);
                turtle.walk(DIAMOND_SIZE);
                g.setColor(Color.BLACK);
                turtle.drawPolyline(g);
            }
            break;

//                case TO_ONE_OPTIONAL:
//                case TO_ONE_MANDATORY: {
//                    var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
//                    turtle.walk(DIAMOND_SIZE*2);
//                    turtle.rotate(90);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.walk(-DIAMOND_SIZE * 2);
//                    turtle.drawPolyline(g);
//                }
//                break;
//                case TO_MANY_OPTIONAL:
//                case TO_MANY_MANDATORY: {
//                    var turtle = new Turtle(firstPoint.getX(), firstPoint.getY(), angle);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.rotate(135);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.walk(-DIAMOND_SIZE);
//                    turtle.rotate(90);
//                    turtle.walk(DIAMOND_SIZE);
//                    turtle.walk(-DIAMOND_SIZE);
//                    turtle.drawPolyline(g);
//                }
//                break;
            default:
                throw new RuntimeException(startType.name());
        }
    }
}