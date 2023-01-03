package org.homs.houmls;

import java.awt.*;
import java.util.Stack;

public class Turtle {

    final Stack<Double> xs = new Stack<>();
    final Stack<Double> ys = new Stack<>();

    double angle;

    public Turtle(double x, double y, double angle) {
        this.xs.push(x);
        this.ys.push(y);
        this.angle = angle;
    }

    public void rotate(double degrees) {
        this.angle += Math.toRadians(degrees);
    }

    public void setAngle(int degrees) {
        this.angle = Math.toRadians(degrees);
    }

    public void setAngle(double rads) {
        this.angle = rads;
    }

    public void walk(double distance) {
        this.xs.push(this.xs.peek() + distance * Math.cos(angle));
        this.ys.push(this.ys.peek() + distance * Math.sin(angle));
    }

    public void drawPolyline(Graphics g) {
        int[] xsa = new int[xs.size()];
        int[] ysa = new int[xs.size()];
        for (int i = 0; i < xs.size(); i++) {
            xsa[i] = (int) Math.round(xs.get(i));
            ysa[i] = (int) Math.round(ys.get(i));
        }
        g.drawPolyline(xsa, ysa, xs.size());
    }

    public void fillPolygon(Graphics g) {
        int[] xsa = new int[xs.size()];
        int[] ysa = new int[xs.size()];
        for (int i = 0; i < xs.size(); i++) {
            xsa[i] = (int) Math.round(xs.get(i));
            ysa[i] = (int) Math.round(ys.get(i));
        }
        g.fillPolygon(xsa, ysa, xs.size());
    }

    public void drawCircle(Graphics g, int radius) {
        int x = (int) Math.round(this.xs.peek());
        int y = (int) Math.round(this.ys.peek());
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void fillCircle(Graphics g, int radius) {
        int x = (int) Math.round(this.xs.peek());
        int y = (int) Math.round(this.ys.peek());
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public Point getPosition() {
        return new Point(
                (int) Math.round(this.xs.peek()),
                (int) Math.round(this.ys.peek()));
    }

    public static double pitagoras(double a, double b) {
        return Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0));
    }
}
