package org.homs.lechugauml.shape.impl.connector;

import org.homs.lechugauml.LookAndFeel;
import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class BocadilloConnector extends Connector {

    Color backgroundColor = Color.WHITE;

    public BocadilloConnector(double startx, double starty, double endx, double endy, String attributes) {
        super(startx, starty, endx, endy, attributes);
        setAttributesText(getAttributesText());
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new BocadilloConnector(
                startPoint.getAbsolutePoint().x + translatex, startPoint.getAbsolutePoint().y + translatey,
                endPoint.getAbsolutePoint().x + translatex, endPoint.getAbsolutePoint().y + translatey,
                getAttributesText()
        );
        middlePoints.forEach(p -> r.getMiddlePoints().add(new DoublePoint(p.x + translatex, p.y + translatey)));
        return r;
    }

    @Override
    public String getAttributesText() {
        return attributesText;
    }

    @Override
    public void setAttributesText(String attributesText) {

        super.setAttributesText(attributesText);

        Map<String, String> props = PropsParser.parseProperties(attributesText);

        Color bg = PropsParser.getColorByProp(props, "bg");
        if (bg != null) {
            this.backgroundColor = bg;
        }
    }

    @Override
    public void draw(Graphics g) {

        ((Graphics2D) g).setStroke(LookAndFeel.basicStroke);

        List<Point> listOfAbsolutePoints = getListOfAbsolutePoints();
        int[] xs = new int[listOfAbsolutePoints.size()];
        int[] ys = new int[listOfAbsolutePoints.size()];
        for (var i = 0; i < listOfAbsolutePoints.size(); i++) {
            xs[i] = listOfAbsolutePoints.get(i).x;
            ys[i] = listOfAbsolutePoints.get(i).y;
        }
        g.setColor(backgroundColor);
        g.drawPolygon(xs, ys, xs.length);
        g.fillPolygon(xs, ys, xs.length);

        g.setColor(Color.BLACK);
        for (var i = 1; i < listOfAbsolutePoints.size(); i++) {
            var p1 = listOfAbsolutePoints.get(i - 1);
            var p2 = listOfAbsolutePoints.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
