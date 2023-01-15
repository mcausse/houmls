package org.homs.houmls;

import org.homs.houmls.xml.HoumsFileFormatManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.homs.houmls.LookAndFeel.yellowMartin;

// XXX https://stackoverflow.com/questions/63583595/java-graphics2d-zoom-on-mouse-location
// XXX https://medium.com/@benjamin.botto/zooming-at-the-mouse-coordinates-with-affine-transformations-86e7312fd50b

/**
 * Houmls
 * MartinLet
 * MartinUML
 * JouLet
 *
 * <pre>
 *     X nou listener per a seleccionar elements => info al JTextArea
 *     X resizar boxes
 *     X editar info JTextArea i en component es parseja i s'estila!
 *     X parser de text tipo MarkDown amb aligments
 *
 *     X text a les arrows, cardinalitat (1..2, 0..*) rols, etc...
 *     X layers?
 *     X box de comment (+ pergamino?)
 *     X duplicar element amb double-click
 *
 *     X crear elements + toolbox panel
 *     X esborrar elements
 *     X crear points intermitjos de connectors.
 *     X eliminar points intermitjos de connectors.
 *     ? punt per a moure la fletcha sencera, deslinkant rollo UMLet
 *
 *     P menú general (operacions de fitxer, about, etc).
 *     X llegir XMLs de UMLet
 *     X guardar XMLs de UMLet
 *     ? pestanyes? bah, no cal.
 *     X exportar a PNG, etc... nou main, amb parsing de parameters variats.... {@see org.homs.houmls.ExportAsPng}
 *
 *      x veure com crear un XML (veure DriverEngine...)
 *
 *      X + cercles, elipses
 *      X + Generics <T>
 *      X + moñeco
 *      X + required --o)-- provided
 *      X + altres tipos de caixes a base de diferents turtles... veure diagrames de activitat
 *      X + caixes amb rounded corners
 *
 *      - undo
 *      X multisellecció + moure en grup!
 *      - copy/cutty/paste!
 *      - accions de teclat
 *
 *      - apastelar colors, paleta...
 *      X admetre RGB a més de noms: UMLet: "bg=#00aa70"
 *      - icones al Popupmenu
 *      - millorar el tema MarkDown
 *
 *      - caixa amb codi turtle: recordar com era en QuickBasic. Demo amb turtle
 *      - millorar turtle a lo QBasic?
 *        http://www.antonis.de/qbebooks/gwbasman/draw.html#:~:text=The%20DRAW%20statement%20combines%20most,valid%20only%20in%20graphics%20mode.
 *      X nou connector amb relleno, que es pugui enganxar a caixa i fer bocadillos!
 *
 *      - llegir "UML Distilled" (Martin Fowler) i apendre UML pràctic d'una puta vegada
 *      X importar els diagrams que tinc en GitLab de Roche
 *
 *
 * </pre>
 */
public class MainC1 {

    public static final String FRAME_TITLE = "Houmls -- ";
    public static final String UNNAMED_FILENAME = "Unnamed";

    static final Image frameIcon = Toolkit.getDefaultToolkit().getImage(MainC1.class.getClassLoader().getResource("org/homs/houmls/houmls.png"));

    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var shapeTextEditor = new JTextArea();
        shapeTextEditor.setBackground(yellowMartin);
        JScrollPane scrollShapeTextEditor = new JScrollPane(shapeTextEditor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        var canvas = new Canvas(shapeTextEditor);

        // TODO
//        canvas.diagram = HoumsFileFormatManager.loadFile("diagrams/welcome.houmls");

        //
        // LATERAL BAR
        //
        var lateralBar = new JPanel();
        lateralBar.setLayout(new BorderLayout());
//        var shapesCatalog = new JPanel();


//        JSplitPane toolBoxSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shapesCatalog, scrollShapeTextEditor);
//        lateralBar.add(toolBoxSplitPane);
        lateralBar.add(scrollShapeTextEditor);


        var f = new JFrame();
        f.setIconImage(frameIcon);
        f.setLayout(new BorderLayout());

        Consumer<String> currentDiagramFileNameConsumer = (fileName) -> f.setTitle(
                FRAME_TITLE + (fileName == null ? UNNAMED_FILENAME : fileName)
        );
        currentDiagramFileNameConsumer.accept(canvas.getDiagramName());

        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.addKeyListener(canvas.getOffsetAndZoomListener());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        {
            final JButton newButton;
            final JButton openBbutton;
            final JButton saveButton;
            final JButton saveAsButton;
            final JButton centerDiagram;

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Houmls files", "houmls", "uxf");

            newButton = buildButton("icons/page.png", "New (^N)", "^n", "Control N", KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK), new AbstractAction() {
                private static final long serialVersionUID = -1337580617687814477L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.diagram.reset();
                    canvas.repaint();
                    currentDiagramFileNameConsumer.accept(canvas.getDiagramName());
                }
            });
            // XXX ^O open file
            openBbutton = buildButton("icons/folder_page.png", "Open (^O)", "^o", "Control O", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), new AbstractAction() {
                private static final long serialVersionUID = -1337580617687814477L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser(new File("."));
                    fc.setFileFilter(filter);
                    int returnVal = fc.showOpenDialog(f);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            Diagram diagram = HoumsFileFormatManager.loadFile(file.toString());
                            canvas.diagram = diagram;
                            canvas.centerDiagram();
                            canvas.repaint();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    currentDiagramFileNameConsumer.accept(canvas.getDiagramName());
                }
            });
            // XXX ^S save current file
            saveButton = buildButton("icons/disk.png", "Save (^S)", "^s", "Control S", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), new AbstractAction() {
                private static final long serialVersionUID = -1337580617687814477L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    String fileName = canvas.getDiagramName();
                    if (fileName == null) {
                        JFileChooser fc = new JFileChooser(new File("."));
                        fc.setFileFilter(filter);
                        int returnVal = fc.showSaveDialog(f);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                HoumsFileFormatManager.writeFile(canvas.diagram, file.toString());
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            HoumsFileFormatManager.writeFile(canvas.diagram, fileName);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    currentDiagramFileNameConsumer.accept(canvas.getDiagramName());
                }
            });

            // XXX ^D Save as...
            saveAsButton = buildButton("icons/page_save.png", "Save As... (^D)", "^d", "Control D", KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK),
                    new AbstractAction() {
                        private static final long serialVersionUID = -1337580617687814477L;

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            JFileChooser fc = new JFileChooser(new File("."));
                            fc.setFileFilter(filter);
                            int returnVal = fc.showSaveDialog(saveButton);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();
                                try {
                                    HoumsFileFormatManager.writeFile(canvas.diagram, file.toString());
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                            currentDiagramFileNameConsumer.accept(canvas.getDiagramName());
                        }
                    });


            centerDiagram = buildButton("icons/arrow_out.png", "Center diagram", null, null, null,
                    new AbstractAction() {
                        private static final long serialVersionUID = -1337580617687814477L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            canvas.centerDiagram();
                        }
                    });

            toolBar.add(newButton);
            toolBar.add(openBbutton);
            toolBar.add(saveButton);
            toolBar.add(saveAsButton);
            toolBar.addSeparator();
            toolBar.add(centerDiagram);
        }

        f.add(toolBar, BorderLayout.NORTH);

        JSplitPane sl = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvas, lateralBar);
        f.add(sl);

        {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle frameBounds = env.getMaximumWindowBounds();
            f.setSize(new Dimension(frameBounds.width, frameBounds.height));
        }

        lateralBar.addKeyListener(canvas.getOffsetAndZoomListener());
//        shapesCatalog.addKeyListener(canvas.getOffsetAndZoomListener());
        shapeTextEditor.addKeyListener(canvas.getOffsetAndZoomListener());
        sl.addKeyListener(canvas.getOffsetAndZoomListener());
//        toolBoxSplitPane.addKeyListener(canvas.getOffsetAndZoomListener());
        toolBar.addKeyListener(canvas.getOffsetAndZoomListener());
        Arrays.stream(toolBar.getComponents()).forEach(c -> c.addKeyListener(canvas.getOffsetAndZoomListener()));

        f.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            sl.setDividerLocation(0.8);
//            toolBoxSplitPane.setDividerLocation(0.5);
        });
    }

    protected static JButton buildButton(String imageName, String toolTipText, String shortCut, String actionCommand, KeyStroke keyStroke, Action action) {

        JButton button = new JButton();
        button.setLayout(new GridLayout(2, 1));

        if (keyStroke != null) {
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionCommand);
            button.getActionMap().put(actionCommand, action);
        }
        button.setAction(action);

        URL resource = MainC1.class.getResource(imageName);
        if (resource == null) {
            throw new RuntimeException("file not found: " + imageName);
        }
        Image ico = Toolkit.getDefaultToolkit().getImage(resource);
        button.setIcon(new ImageIcon(ico));
        button.setToolTipText(toolTipText);

        return button;
    }
}
