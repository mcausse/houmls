package org.homs.houmls;

import java.awt.*;

public class GridControl {

    public static final int GRID_SIZE = 10;

    public static final Color GRID_COLOR = Color.GRAY;

    static int roundToMultiple(double x, int m) {
        return (int) (m * (Math.round(x / m)));
    }

    public static int engrid(int x) {
        return roundToMultiple(x, GRID_SIZE);
    }

    public static int engrid(double x) {
        return roundToMultiple(x, GRID_SIZE);
    }
}