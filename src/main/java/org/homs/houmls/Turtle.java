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
        this.angle += degrees * Math.PI / 180.0;
    }

    public void walk(double distance) {
        this.xs.push(this.xs.peek() + distance * Math.cos(angle));
        this.ys.push(this.ys.peek() + distance * Math.sin(angle));
    }

    public void drawPolyline(Graphics g) {
        int[] xsa = new int[xs.size()];
        int[] ysa = new int[xs.size()];
        for (int i = 0; i < xs.size(); i++) {
            xsa[i] = xs.get(i).intValue();
            ysa[i] = ys.get(i).intValue();
        }
        g.drawPolyline(xsa, ysa, xs.size());
    }

    public void fillPolygon(Graphics g) {
        int[] xsa = new int[xs.size()];
        int[] ysa = new int[xs.size()];
        for (int i = 0; i < xs.size(); i++) {
            xsa[i] = xs.get(i).intValue();
            ysa[i] = ys.get(i).intValue();
        }
        g.fillPolygon(xsa, ysa, xs.size());
    }
}
