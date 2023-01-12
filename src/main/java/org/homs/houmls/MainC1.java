package org.homs.houmls;

import org.homs.houmls.xml.UxfFileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

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
 *     - punt per a moure la fletcha sencera, deslinkant rollo UMLet
 *
 *     P menú general (operacions de fitxer, about, etc).
 *     X llegir XMLs de UMLet
 *     X guardar XMLs de UMLet
 *     - pestanyes? bah, no cal.
 *     P exportar a PNG, etc... nou main, amb parsing de parameters variats.... {@see org.homs.houmls.ExportAsPng}
 *
 *     - undo
 *     - multisellecció + moure en grup!
 *
 *
 *      - llegir "UML Distilled" (Martin Fowler) i apendre UML pràctic d'una puta vegada
 *      - importar els diagrams que tinc en GitLab de Roche
 *
 *      x veure com crear un XML (veure DriverEngine...)
 *
 *      X + cercles, elipses
 *      - + Generics <T>
 *      X + moñeco
 *      X + required --o)-- provided
 *      X + altres tipos de caixes a base de diferents turtles... veure diagrames de activitat
 *      X + caixes amb rounded corners
 *
 *      - apastelar colors, o admetre RGB a més de noms, paleta...
 *      - icones al Popupmenu
 *      - millorar el tema MarkDown
 *
 *      - caixa amb codi turtle: recordar com era en QuickBasic. Demo amb turtle
 *
 *      - millorar turtle a lo QBasic?
 *        http://www.antonis.de/qbebooks/gwbasman/draw.html#:~:text=The%20DRAW%20statement%20combines%20most,valid%20only%20in%20graphics%20mode.
 *      - nou connector amb relleno, que es pugui enganxar a caixa i fer bocadillos!
 *
 * </pre>
 */
public class MainC1 {

    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var shapeTextEditor = new JTextArea();
        shapeTextEditor.setBackground(yellowMartin);
        JScrollPane scrollShapeTextEditor = new JScrollPane(shapeTextEditor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        var canvas = new Canvas(shapeTextEditor);

        canvas.diagram = UxfFileManager.loadFile("OrderEntrance.uxf");

        //
        // LATERAL BAR
        //
        var lateralBar = new JPanel();
        lateralBar.setLayout(new BorderLayout());
        var shapesCatalog = new JPanel();


        JSplitPane toolBoxSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shapesCatalog, scrollShapeTextEditor);
        lateralBar.add(toolBoxSplitPane);


        var f = new JFrame("Houmls");
        f.setLayout(new BorderLayout());

        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.addKeyListener(canvas.getOffsetAndZoomListener());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        {
            final JButton newButton;
            final JButton openBbutton;
            final JButton saveButton;
            final JButton saveAsButton;

            newButton = buildButton("icons/page.png", "New (^N)", "^n", "Control N", KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK), new AbstractAction() {
                private static final long serialVersionUID = -1337580617687814477L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.diagram.reset();
                    canvas.repaint();
                }
            });
            // XXX ^O open file
            openBbutton = buildButton("icons/folder_page.png", "Open (^O)", "^o", "Control O", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), new AbstractAction() {
                private static final long serialVersionUID = -1337580617687814477L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser(new File("."));
                    int returnVal = fc.showOpenDialog(f);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            Diagram diagram = UxfFileManager.loadFile(file.toString());
                            canvas.diagram = diagram;
                            canvas.repaint();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            // XXX ^S save current file
            saveButton = buildButton("icons/disk.png", "Save (^S)", "^s", "Control S", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), new AbstractAction() {
                private static final long serialVersionUID = -1337580617687814477L;

                @Override
                public void actionPerformed(ActionEvent e) {
//                    String fileName = getSelectedFilename();
//                    HomsTextEditor currentEditorComponent = (HomsTextEditor) tabbedPane.getSelectedComponent();
//
//                    if (fileName != null) {
//                        TextFileUtils.write(new File(fileName), TextFileUtils.UTF8, currentEditorComponent.getText());
//                        onTextChanges.accept(currentEditorComponent, false);
//                    } else {
                    JFileChooser fc = new JFileChooser(new File("."));
                    int returnVal = fc.showSaveDialog(f);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            UxfFileManager.writeFile(canvas.diagram, file.toString());
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
//                        TextFileUtils.write(file, TextFileUtils.UTF8, currentEditorComponent.getText());
//                        tabbedPane.setToolTipTextAt(tabbedPane.getSelectedIndex(), file.toString());
//                        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
//                        onTextChanges.accept(currentEditorComponent, false);
                    }
//                    }
                }

            });

            // XXX ^D Save as...
            saveAsButton = buildButton("icons/page_save.png", "Save As... (^D)", "^d", "Control D", KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK),
                    new AbstractAction() {
                        private static final long serialVersionUID = -1337580617687814477L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
//                            HomsTextEditor currentEditorComponent = (HomsTextEditor) tabbedPane.getSelectedComponent();
//
                            JFileChooser fc = new JFileChooser(new File("."));
                            int returnVal = fc.showSaveDialog(saveButton);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();
//                                TextFileUtils.write(file, TextFileUtils.UTF8, currentEditorComponent.getText());
//                                tabbedPane.setToolTipTextAt(tabbedPane.getSelectedIndex(), file.toString());
//                                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
//                                onTextChanges.accept(currentEditorComponent, false);
                                try {
                                    UxfFileManager.writeFile(canvas.diagram, file.toString());
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                    });

            toolBar.add(newButton);
            toolBar.add(openBbutton);
            toolBar.add(saveButton);
            toolBar.add(saveAsButton);
            toolBar.addSeparator();
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
        shapesCatalog.addKeyListener(canvas.getOffsetAndZoomListener());
        shapeTextEditor.addKeyListener(canvas.getOffsetAndZoomListener());
        sl.addKeyListener(canvas.getOffsetAndZoomListener());
        toolBoxSplitPane.addKeyListener(canvas.getOffsetAndZoomListener());
        toolBar.addKeyListener(canvas.getOffsetAndZoomListener());
        Arrays.stream(toolBar.getComponents()).forEach(c -> c.addKeyListener(canvas.getOffsetAndZoomListener()));

        f.setVisible(true);
        SwingUtilities.invokeLater(() -> {
            sl.setDividerLocation(0.7);
            toolBoxSplitPane.setDividerLocation(0.5);
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
