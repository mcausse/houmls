package org.homs.houmls;

import java.awt.*;

public class GridControl {

    public static boolean drawGrid = true;

    public static final int GRID_SIZE = 10;

    public static final Color GRID_COLOR = Color.GRAY;

    static int roundToMultiple(double v, int m) {
        return Math.round(m * (Math.round(v / m)));
    }

    public static int engrid(int v) {
        return roundToMultiple(v, GRID_SIZE);
    }

    public static int engrid(double v) {
        return roundToMultiple(v, GRID_SIZE);
    }
}