package org.homs.houmls;

import java.awt.*;
import java.util.Stack;

public class Turtle {

    final Stack<Double> xs = new Stack<>();
    final Stack<Double> ys = new Stack<>();

    double angleInRads;

    public Turtle(double x, double y, double angleInRads) {
        this.xs.push(x);
        this.ys.push(y);
        this.angleInRads = angleInRads;
    }

    public void clear() {
        var x = xs.peek();
        var y = xs.peek();
        this.xs.clear();
        this.ys.clear();
        this.xs.push(x);
        this.ys.push(y);
    }

    public void rotate(double degrees) {
        this.angleInRads += Math.toRadians(degrees);
    }

    public void rotate(int degrees) {
        rotate((double) degrees);
    }

    public void setAngle(int degrees) {
        this.angleInRads = Math.toRadians(degrees);
    }

    public void setAngle(double rads) {
        this.angleInRads = rads;
    }

    public void walk(double distance) {
        this.xs.push(this.xs.peek() + distance * Math.cos(angleInRads));
        this.ys.push(this.ys.peek() + distance * Math.sin(angleInRads));
    }

    public void walk(int distance) {
        walk((double) distance);
    }

    public void jump(double distance) {
        this.xs.push(this.xs.pop() + distance * Math.cos(angleInRads));
        this.ys.push(this.ys.pop() + distance * Math.sin(angleInRads));
    }

    public void jump(int distance) {
        jump((double) distance);
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

    public void drawText(Graphics g, String text) {
        int x = (int) Math.round(this.xs.peek());
        int y = (int) Math.round(this.ys.peek());
        g.drawString(text, x, y);
    }

    public Point getPosition() {
        return new Point(
                (int) Math.round(this.xs.peek()),
                (int) Math.round(this.ys.peek()));
    }

    public void moveTo(int x, int y) {
        this.xs.push((double) x);
        this.ys.push((double) y);
    }

    public static double pitagoras(double a, double b) {
        return Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0));
    }

    public static class PolylineFiller {
        final Stack<Double> xs = new Stack<>();
        final Stack<Double> ys = new Stack<>();

        public void add(int x, int y) {
            xs.push((double) x);
            ys.push((double) y);
        }

        public void add(double x, double y) {
            xs.push(x);
            ys.push(y);
        }

        public void fill(Graphics g) {
            int[] xsa = new int[xs.size()];
            int[] ysa = new int[xs.size()];
            for (int i = 0; i < xs.size(); i++) {
                xsa[i] = (int) Math.round(xs.get(i));
                ysa[i] = (int) Math.round(ys.get(i));
            }
            g.fillPolygon(xsa, ysa, xs.size());
        }

        public void draw(Graphics g) {
            int[] xsa = new int[xs.size()];
            int[] ysa = new int[xs.size()];
            for (int i = 0; i < xs.size(); i++) {
                xsa[i] = (int) Math.round(xs.get(i));
                ysa[i] = (int) Math.round(ys.get(i));
            }
            g.drawPolyline(xsa, ysa, xs.size());
        }
    }

}
