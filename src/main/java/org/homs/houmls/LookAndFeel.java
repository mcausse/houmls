package org.homs.houmls;

import java.awt.*;

public class LookAndFeel {

    public static final Color yellowMartin = new Color(0xff, 0xff, 0xda);

    protected static final String regularFontName = Font.SANS_SERIF; // Font.MONOSPACED; //"Courier";
    public static final int regularFontSize = 12;

    public static Font regularFont() {
        return new Font(regularFontName, Font.PLAIN, regularFontSize);
    }

    public static Font regularFont(int size) {
        return new Font(regularFontName, Font.PLAIN, size);
    }

    public static Font regularFontBold(int size) {
        return new Font(regularFontName, Font.BOLD, size);
    }

    public static Font regularFontItalic(int size) {
        return new Font(regularFontName, Font.ITALIC, size);
    }

    public static final Stroke basicStroke = new BasicStroke(1);
    public static final Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[]{5}, 0);


    public static final Boolean BOXES_WITH_SHADOW = true;
}


