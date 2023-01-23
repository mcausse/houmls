package org.homs.houmls.shape.impl.box;

import org.homs.houmls.Turtle;
import org.homs.houmls.shape.Shape;
import org.homs.lechugascript.Environment;
import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.parser.ast.Ast;

import java.awt.*;
import java.util.List;

import static org.homs.houmls.LookAndFeel.basicStroke;

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
        g2.setColor(backgroundColor);
        g2.fillRect(ix, iy, iwidth, iheight);

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
            env.def("*graphics*", g2);
            env.def("*turtle*", new Turtle(x, y, 0));
            try {
                this.interpreter.evaluate(asts, env);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
