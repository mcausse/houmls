package org.homs.houmls;

import java.awt.*;

public class LookAndFeel {
    protected static final String regularFontName = Font.SANS_SERIF; // Font.MONOSPACED; //"Courier";
    protected static final int regularFontSize = 12;

    public static final Font regularFont = new Font(regularFontName, Font.PLAIN, regularFontSize);
    public static final Font regularFontBold = new Font(regularFontName, Font.BOLD, regularFontSize);
    public static final Font regularFontItalic = new Font(regularFontName, Font.ITALIC, regularFontSize);

    public static final Stroke basicStroke = new BasicStroke(1);
    public static final Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[]{5}, 0);
}


