package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.Turtle;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;

import static org.homs.lechugauml.LookAndFeel.basicStroke;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class TurtleBox extends Box {

    public TurtleBox(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new TurtleBox((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    @Override
    public void draw(Graphics g) {
        drawTheBox((Graphics2D) g);
    }

    protected void drawTheBox(Graphics2D g2) {

        g2.setStroke(basicStroke);
        g2.setColor(Color.BLACK);

        String turtleProgram = getAttributesText();

        Turtle turtle = new Turtle(x, y, 0);

        String[] parts = turtleProgram.split("\\s+");
        int p = 0;
        while (p < parts.length) {
            String command = parts[p++];

            switch (command) {
                case "walk": {
                    String value = parts[p++];
                    turtle.walk(Double.parseDouble(value));
                }
                break;
                case "jump": {
                    String value = parts[p++];
                    turtle.jump(Double.parseDouble(value));
                }
                break;
                case "rotate": {
                    String value = parts[p++];
                    turtle.rotate(Double.parseDouble(value));
                }
                break;
                case "color": {
                    String value = parts[p++];
                    Color bg = PropsParser.getColorByName(value);
                    g2.setColor(bg);
                }
                break;
                case "draw":
                    turtle.drawPolyline(g2);
                    break;
                case "fill":
                    turtle.fillPolygon(g2);
                    break;
                case "reset":
                    turtle.clear();
                    break;
                default:
                    //throw new RuntimeException("unexpected command: " + command);
            }
        }
    }
}
