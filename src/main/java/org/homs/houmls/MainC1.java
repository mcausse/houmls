package org.homs.houmls;

import org.homs.houmls.shape.impl.Box;
import org.homs.houmls.shape.impl.Connector;

import javax.swing.*;
import java.awt.*;

import static org.homs.houmls.GridControl.GRID_SIZE;

// XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
// XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b

/**
 * Houmls
 * MartinLet
 * MartinUML
 *
 * <pre>
 *     X nou listener per a seleccionar elements => info al JTextArea
 *     - editar info JTextArea i en component es parseja i s'estila!
 *     - parser de text tipo MarkDown amb aligments
 *
 *     - text a les arrows, cardinalitat (1..2, 0..*) rols, etc...
 *     - box de comment (+ pergamino?)
 *     - duplicar element amb double-click
 *
 *     - crear/esborrar elements + toolbox panel
 *     - crear/eliminar points intermitjos de connectors.
 *
 *     - menú general (operacions de fitxer, about, etc).
 *     - llegir XMLs de UMLet, i poder-los exportar
 *     - pestanyes? bah, no cal.
 *     - exportar a PNG, etc...
 *
 *     - undo
 *     - multisellecció + moure en grup!
 * </pre>
 */
public class MainC1 {


    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var shapeTextEditor = new JTextArea();

        var canvas = new Canvas(shapeTextEditor);
        {

            Box class1 = new Box(GRID_SIZE * 10, GRID_SIZE * 5, GRID_SIZE * 10, GRID_SIZE * 10, "._<<@Component>>\n.*MartinCanvas\n---\n---\nvoid paint(Graphics g)\n---");
            Box class2 = new Box(GRID_SIZE * 25, GRID_SIZE * 5, GRID_SIZE * 10, GRID_SIZE * 10, "*11111\n---\n233333\n---");
            Connector connector1 = new Connector(class1, Connector.Type.AGGREGATION, GRID_SIZE * 10, GRID_SIZE * 3, class2, Connector.Type.ARROW, 0, GRID_SIZE * 3);
            Connector connector2 = new Connector(class1, Connector.Type.COMPOSITION, GRID_SIZE * 10, GRID_SIZE * 6, class2, Connector.Type.ARROW, 0, GRID_SIZE * 6);
//            arrow.getMiddlePoints().add(new Point(150, 100));
            canvas.addElement(connector1);
            canvas.addElement(connector2);
            canvas.addElement(class1);
            canvas.addElement(class2);

        }
        {
            Box target = new Box(GRID_SIZE * 10, GRID_SIZE * 20, GRID_SIZE * 10, GRID_SIZE * 10, "._<<interface>>\n.*Target\n---\n---\noperation()\n---");
            Box adapter = new Box(GRID_SIZE * 10, GRID_SIZE * 35, GRID_SIZE * 10, GRID_SIZE * 10, ".*Adapter\n---\n---\noperation()\n---");
            Connector connector = new Connector(adapter, Connector.Type.DEFAULT, GRID_SIZE * 5, GRID_SIZE * 0, target, Connector.Type.INHERITANCE, GRID_SIZE * 5, GRID_SIZE * 10);
            Connector commentConnector = new Connector(adapter, Connector.Type.MEMBER_COMMENT, GRID_SIZE * 10 - 10, GRID_SIZE * 2 + 2, null, Connector.Type.DEFAULT, GRID_SIZE * 40, GRID_SIZE * 30);
            canvas.addElement(connector);
            canvas.addElement(target);
            canvas.addElement(adapter);
            canvas.addElement(commentConnector);
        }
        {
//            HoumlsConnector houmlsConnector = new HoumlsConnector(
//                    null, HoumlsConnector.Type.TO_ONE_OPTIONAL, GRID_SIZE * 10, GRID_SIZE * 50,
//                    null, HoumlsConnector.Type.TO_MANY_OPTIONAL, GRID_SIZE * 35, GRID_SIZE * 50);
//            HoumlsConnector commentConnector = new HoumlsConnector(
//                    null, HoumlsConnector.Type.TO_ONE_MANDATORY, GRID_SIZE * 10 , GRID_SIZE * 55,
//                    null, HoumlsConnector.Type.TO_MANY_MANDATORY, GRID_SIZE * 35, GRID_SIZE * 55);
//            canvas.addElement(houmlsConnector);
//            canvas.addElement(commentConnector);
        }

        //
        // LATERAL BAR
        //
        var lateralBar = new JPanel();
        lateralBar.setLayout(new BorderLayout());
        var shapesCatalog = new JPanel();


        JSplitPane toolBoxSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shapesCatalog, shapeTextEditor);
        lateralBar.add(toolBoxSplitPane);


        var f = new JFrame("Houmls v0.0");
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
