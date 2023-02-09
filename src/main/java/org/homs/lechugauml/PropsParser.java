package org.homs.lechugauml;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class PropsParser {

    final static Pattern p = Pattern.compile("(\\w+)=(.*)");

    public static Map<String, String> parseProperties(String text) {
        var r = new LinkedHashMap<String, String>();
        var general = new StringBuilder();

        String[] lines = text.split("\\n");
        for (var line : lines) {
            Matcher m = p.matcher(line);
            if (m.matches()) {
                var propName = m.group(1);
                var propValue = m.group(2);
                r.put(propName, propValue);
            } else {
                general.append(line).append("\n");
            }
        }

        r.put("", general.toString());
        return r;
    }

    public static Color getColorByProp(Map<String, String> props, String propName, String defaultColorName) {
        var propValue = props.getOrDefault(propName, defaultColorName);
        return getColorByName(propValue);
    }

    public static Color getColorByProp(Map<String, String> props, String propName) {
        return getColorByProp(props, propName, "white");
    }

    static Color lighter(Color c, float factor) {
        return blend(c, Color.WHITE, factor);
    }

    static Color blend(Color cFrom, Color cTo, float factor) {
        if (factor < 0f || factor > 1f) {
            throw new IllegalArgumentException("factor not between 0 and 1: " + factor);
        }

        int r = cFrom.getRed();
        int g = cFrom.getGreen();
        int b = cFrom.getBlue();

        int mixerr = cTo.getRed();
        int mixerg = cTo.getGreen();
        int mixerb = cTo.getBlue();

        return new Color(
                (int) (r + (mixerr - r) * factor),
                (int) (g + (mixerg - g) * factor),
                (int) (b + (mixerb - b) * factor)
        );
    }

    public static Color getColorByName(String name) {
        if (name.startsWith("#")) {
            try {
                return Color.decode(name);
            } catch (Exception e) {
                return null;
            }
        } else {
            try {
                int lighter = 0;
                while (name.startsWith("l-")) {
                    lighter++;
                    name = name.substring("l-".length());
                }
                var c = (Color) Color.class.getField(name.toUpperCase()).get(null);
                for (int i = 0; i < lighter; i++) {
                    c = lighter(c, 0.4f);
                }
                return c;
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                return Color.WHITE;
            }
        }
    }

    public static String reverseArrowStyle(String source) {
        var rev = new StringBuilder();
        for (int i = source.length() - 1; i >= 0; i--) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    rev.append('>');
                    break;
                case '>':
                    rev.append('<');
                    break;
                case '[':
                    rev.append(']');
                    break;
                case ']':
                    rev.append('[');
                    break;
                case '(':
                    rev.append(')');
                    break;
                case ')':
                    rev.append('(');
                    break;
                default:
                    rev.append(c);
            }
        }
        return rev.toString();
    }

    public static List<String> split(String text, char sep) {
        var r = new ArrayList<String>();
        var s = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c == sep) {
                r.add(s.toString());
                s = new StringBuilder();
            } else {
                s.append(c);
            }
        }

        // the tail
        if (s.length() > 0) {
            r.add(s.toString());
        }

        return r;
    }
}
