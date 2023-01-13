package org.homs.houmls;

import java.awt.*;

public class GridControl {

    public static final int GRID_SIZE = 10;

    public static final Color GRID_COLOR = Color.GRAY;

    public static int engrid(int c) {
        return c - c % GRID_SIZE;
    }

    public static int engrid(double c) {
        int cc = ((int) Math.round(c));
        int part = cc % GRID_SIZE;
        if (part <= GRID_SIZE / 2) {
            return cc - part;
        } else {
            return cc - part + GRID_SIZE;
        }
    }
}