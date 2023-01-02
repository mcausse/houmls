package org.homs.houmls;

import org.homs.houmls.shape.impl.Arrow;
import org.homs.houmls.shape.impl.Clazz;

import javax.swing.*;
import java.awt.*;

import static org.homs.houmls.GridControl.GRID_SIZE;

// XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
// XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b
public class MainC1 {


    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var canvas = new Canvas();
        {

            Clazz class1 = new Clazz(GRID_SIZE * 10, GRID_SIZE * 5, GRID_SIZE * 10, GRID_SIZE * 10, "_<<@Component>>\n*MartinCanvas\n---\nvoid paint(Graphics g)\n---");
            Clazz class2 = new Clazz(GRID_SIZE * 25, GRID_SIZE * 5, GRID_SIZE * 10, GRID_SIZE * 10, "*11111\n---\n233333\n---");
            Arrow arrow1 = new Arrow(class1, Arrow.Type.AGGREGATION, GRID_SIZE * 10, GRID_SIZE * 3, class2, Arrow.Type.ARROW, 0, GRID_SIZE * 3);
            Arrow arrow2 = new Arrow(class1, Arrow.Type.COMPOSITION, GRID_SIZE * 10, GRID_SIZE * 6, class2, Arrow.Type.ARROW, 0, GRID_SIZE * 6);
//            arrow.getMiddlePoints().add(new Point(150, 100));
            canvas.addElement(arrow1);
            canvas.addElement(arrow2);
            canvas.addElement(class1);
            canvas.addElement(class2);

        }
        {
            Clazz target = new Clazz(GRID_SIZE * 10, GRID_SIZE * 20, GRID_SIZE * 10, GRID_SIZE * 10, "_<<interface>>\n*Target\n---\noperation()\n---");
            Clazz adapter = new Clazz(GRID_SIZE * 10, GRID_SIZE * 35, GRID_SIZE * 10, GRID_SIZE * 10, "*Adapter\n---\noperation()\n---");
            Arrow arrow = new Arrow(adapter, Arrow.Type.DEFAULT, GRID_SIZE * 5, GRID_SIZE * 0, target, Arrow.Type.INHERITANCE, GRID_SIZE * 5, GRID_SIZE * 10);
            canvas.addElement(arrow);
            canvas.addElement(target);
            canvas.addElement(adapter);
        }

        //
        // LATERAL BAR
        //
        var lateralBar = new JPanel();
        lateralBar.setLayout(new BorderLayout());
        var shapesCatalog = new JPanel();
        var shapeTextEditor = new JTextArea();


        JSplitPane toolBoxSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shapesCatalog, shapeTextEditor);
        lateralBar.add(toolBoxSplitPane);


        var f = new JFrame("MartinUML (Houmls)");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.addKeyListener(canvas.getOffsetAndZoomListener());

        JSplitPane sl = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, lateralBar);
        f.add(sl);
        {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle frameBounds = env.getMaximumWindowBounds();
            f.setSize(new Dimension(frameBounds.width, frameBounds.height));
        }

        lateralBar.addKeyListener(canvas.getOffsetAndZoomListener());
        shapesCatalog.addKeyListener(canvas.getOffsetAndZoomListener());
        shapeTextEditor.addKeyListener(canvas.getOffsetAndZoomListener());
        sl.addKeyListener(canvas.getOffsetAndZoomListener());
        toolBoxSplitPane.addKeyListener(canvas.getOffsetAndZoomListener());

        f.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            sl.setDividerLocation(0.8);
            toolBoxSplitPane.setDividerLocation(0.5);
        });
    }
}
