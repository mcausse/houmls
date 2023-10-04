package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.parser.ast.Ast;
import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.Turtle;
import org.homs.lechugauml.shape.Shape;

import java.awt.*;
import java.util.List;

import static org.homs.lechugauml.LookAndFeel.basicStroke;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class LechugaScriptBox extends Box {

    final Interpreter interpreter;
    final Environment env;

    List<Ast> asts;

    public LechugaScriptBox(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
        try {
            this.interpreter = new Interpreter();
            this.env = interpreter.getStdEnvironment();
            setAttributesText(attributesText);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public void setAttributesText(String attributesText) {
        super.setAttributesText(attributesText);
        if (interpreter != null && text != null) {
            try {
                this.asts = interpreter.parse(text, toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new LechugaScriptBox((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    @Override
    public void draw(Graphics g) {
        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        Graphics2D g2 = (Graphics2D) g;
        if (paintBackground) {
            g2.setColor(backgroundColor);
            g2.fillRect(ix, iy, iwidth, iheight);
        }

        g2.setStroke(basicStroke);
        g2.setColor(Color.BLACK);

        if (asts != null) {
            var env = new Environment(this.env);
            env.def("box-bg-color", backgroundColor);
            env.def("box-font-size", fontSize);
            env.def("box-x", ix);
            env.def("box-y", iy);
            env.def("box-width", iwidth);
            env.def("box-height", iheight);
            env.def("*turtle*", new Turtle(x, y, 0));
            env.def("*graphics*", g2);
            env.def("*this*", this);
            try {
                this.interpreter.evaluate(asts, env);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                g2.setColor(Color.MAGENTA);
                g2.fillRect(ix, iy, iwidth, iheight);
            }
        }
    }

    public void setColor(Graphics g, Color c) {
        g.setColor(c);
    }

    public void setColor(Graphics g, String c) {
        g.setColor(PropsParser.getColorByName(c, 0));
    }

    public void fillRect(Graphics g, int x, int y, int w, int h) {
        g.fillRect(x, y, w, h);
    }

    public void drawRect(Graphics g, int x, int y, int w, int h) {
        g.drawRect(x, y, w, h);
    }

    public void fillOval(Graphics g, int x, int y, int w, int h) {
        g.fillOval(x, y, w, h);
    }

    public void drawOval(Graphics g, int x, int y, int w, int h) {
        g.drawOval(x, y, w, h);
    }

    public void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }

    public void drawArc(Graphics g, int x, int y, int w, int h, int startAngle, int arcAngle) {
        g.drawArc(x, y, w, h, startAngle, arcAngle);
    }

    public void drawString(Graphics g, String text, int x, int y) {
        g.drawString(text, x, y);
    }

    public void setFont(Graphics g, Font font) {
        g.setFont(font);
    }
}
