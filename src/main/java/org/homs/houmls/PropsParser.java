package org.homs.houmls;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropsParser {

    public static Map<String, String> parseProperties(String text) {
        var r = new LinkedHashMap<String, String>();
        var general = new StringBuilder();

        String[] lines = text.split("\\n");
        for (var line : lines) {
            var pos = line.indexOf("=");
            if (pos < 1 || line.trim().indexOf(' ') > 0) {
                general.append(line).append("\n");
            } else {
                var propName = line.substring(0, pos);
                var propValue = line.substring(pos + 1);
                r.put(propName, propValue);
            }
        }

        r.put("", general.toString());
        return r;
    }

    public static Color getColorByProp(Map<String, String> props, String propName) {
        var propValue = props.getOrDefault(propName, "white");
        return getColorByName(propValue);
    }

    public static Color getColorByName(String name) {
        try {
            return (Color) Color.class.getField(name.toUpperCase()).get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return null;
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
}
