package org.homs.lechugauml;

import org.homs.lechugauml.shape.Shape;
import org.homs.lechugauml.xml.HoumsFileFormatManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.homs.lechugauml.LookAndFeel.yellowMartin;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class MainC2 {

    public static final String FRAME_TITLE = "Lechuga UML  0.0.2   (╯°o°）╯︵ ┻━┻  -- ";

    public static final String UNNAMED_FILENAME = "Unnamed";

    static final Image frameIcon = Toolkit.getDefaultToolkit().getImage(MainC2.class.getClassLoader().getResource("org/homs/lechugauml/lechuga-uml.png"));

    static final List<Shape> shapesClipboard = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        var f = new JFrame();
        f.setIconImage(frameIcon);
        f.setLayout(new BorderLayout());
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Consumer<String> currentDiagramOnChangeFileNameListener = (fileName) -> f.setTitle(
                FRAME_TITLE + (fileName == null ? UNNAMED_FILENAME : fileName)
        );
        {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle frameBounds = env.getMaximumWindowBounds();
            f.setSize(new Dimension(frameBounds.width, frameBounds.height));
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        f.add(tabbedPane);

        tabbedPane.addChangeListener(e -> {
            DiagramTab diagramTab = (DiagramTab) ((JTabbedPane) e.getSource()).getSelectedComponent();

            SwingUtilities.invokeLater(() -> {
                diagramTab.getCanvas().requestFocus();
                diagramTab.getCanvas().repaint();
                currentDiagramOnChangeFileNameListener.accept(diagramTab.getDiagramName());
            });
        });

        //

        JToolBar toolBar = buildToolBar(f, tabbedPane, currentDiagramOnChangeFileNameListener);
        f.add(toolBar, BorderLayout.NORTH);

        var keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                getActiveCanvas().getOffsetAndZoomListener().keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                getActiveCanvas().getOffsetAndZoomListener().keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                getActiveCanvas().getOffsetAndZoomListener().keyReleased(e);
            }

            public Canvas getActiveCanvas() {
                DiagramTab diagramTab = (DiagramTab) tabbedPane.getSelectedComponent();
                return diagramTab.getCanvas();
            }
        };

        f.addKeyListener(keyListener);
        toolBar.addKeyListener(keyListener);
        Arrays.stream(toolBar.getComponents()).forEach(c -> c.addKeyListener(keyListener));

        f.setVisible(true);

        // TODO
        createNewDiagramTab(currentDiagramOnChangeFileNameListener, tabbedPane);
        loadDiagramIntoNewTab(new File("diagrams/lechugauml-showcase.houmls"), currentDiagramOnChangeFileNameListener, tabbedPane);
    }

    static class DiagramTab extends JSplitPane {

        final Canvas canvas;
        final JPanel lateralBar;

        public DiagramTab(Canvas canvas, JPanel lateralBar) {
            super(JSplitPane.HORIZONTAL_SPLIT, canvas, lateralBar);
            this.canvas = canvas;
            this.lateralBar = lateralBar;
        }

        public String getDiagramShortName() {
            if (canvas.getDiagramName().isPresent()) {
                var name = canvas.getDiagramName().get();
                int pos = Math.max(
                        name.lastIndexOf('/'),
                        name.lastIndexOf('\\')
                );
                return name.substring(pos + 1);
            } else {
                return null;
            }
        }

        public String getDiagramName() {
            if (canvas.getDiagramName().isPresent()) {
                return canvas.getDiagramName().get();
            } else {
                return null;
            }
        }

        public Canvas getCanvas() {
            return canvas;
        }
    }

    static DiagramTab createNewDiagramTab(Consumer<String> currentDiagramOnChangeFileNameListener) {
        var shapeTextEditor = new JTextArea();
        shapeTextEditor.setBackground(yellowMartin);
        JScrollPane scrollShapeTextEditor = new JScrollPane(shapeTextEditor, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        var canvas = new Canvas(shapeTextEditor, shapesClipboard);

        //
        // LATERAL BAR
        //
        var lateralBar = new JPanel();
        lateralBar.setLayout(new BorderLayout());
        lateralBar.add(scrollShapeTextEditor);


        DiagramTab diagramTab = new DiagramTab(canvas, lateralBar);
        currentDiagramOnChangeFileNameListener.accept(diagramTab.getDiagramName());

        lateralBar.addKeyListener(canvas.getOffsetAndZoomListener());
        shapeTextEditor.addKeyListener(canvas.getOffsetAndZoomListener());
        diagramTab.addKeyListener(canvas.getOffsetAndZoomListener());

        return diagramTab;
    }

    static JToolBar buildToolBar(JFrame frame, JTabbedPane tabbedPane, Consumer<String> currentDiagramFileNameConsumer) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        {
            final JButton newButton;
            final JButton openBbutton;
            final JButton saveButton;
            final JButton saveAsButton;
            final JButton centerDiagram;
            final JButton zoomTo1Diagram;

            FileNameExtensionFilter filter = new FileNameExtensionFilter("LechugaUML files (.houmls)", "houmls", "uxf");

            newButton = buildButton("icons/page.png", "New (^N)", "^n", "Control N", KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createNewDiagramTab(currentDiagramFileNameConsumer, tabbedPane);
                }
            });
            // XXX ^O open file
            openBbutton = buildButton("icons/folder_page.png", "Open (^O)", "^o", "Control O", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser(new File("."));
                    fc.setFileFilter(filter);
                    int returnVal = fc.showOpenDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            loadDiagramIntoNewTab(file, currentDiagramFileNameConsumer, tabbedPane);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });
            // XXX ^S save current file
            saveButton = buildButton("icons/disk.png", "Save (^S)", "^s", "Control S", KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DiagramTab diagramTab = (DiagramTab) tabbedPane.getSelectedComponent();
                    Canvas canvas = diagramTab.getCanvas();

                    String fileName = diagramTab.getDiagramName();
                    if (fileName == null) {
                        JFileChooser fc = new JFileChooser(new File("."));
                        fc.setFileFilter(filter);
                        int returnVal = fc.showSaveDialog(frame);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                HoumsFileFormatManager.writeFile(canvas.getDiagram(), file.toString());
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), diagramTab.getDiagramShortName());
                            currentDiagramFileNameConsumer.accept(diagramTab.getDiagramName());
                        }
                    } else {
                        try {
                            HoumsFileFormatManager.writeFile(canvas.getDiagram(), fileName);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            });

            // XXX ^D Save as...
            saveAsButton = buildButton("icons/page_save.png", "Save As... (^D)", "^d", "Control D", KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK),
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            DiagramTab diagramTab = (DiagramTab) tabbedPane.getSelectedComponent();
                            Canvas canvas = diagramTab.getCanvas();

                            JFileChooser fc = new JFileChooser(new File("."));
                            fc.setFileFilter(filter);
                            int returnVal = fc.showSaveDialog(saveButton);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();
                                try {
                                    HoumsFileFormatManager.writeFile(canvas.getDiagram(), file.toString());
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), diagramTab.getDiagramShortName());
                                currentDiagramFileNameConsumer.accept(diagramTab.getDiagramName());
                            }
                        }
                    });


            centerDiagram = buildButton("icons/arrow_out.png", "Zoom to fit", null, null, null,
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            DiagramTab diagramTab = (DiagramTab) tabbedPane.getSelectedComponent();
                            Canvas canvas = diagramTab.getCanvas();

                            canvas.fitZoomToWindow();
                        }
                    });

            zoomTo1Diagram = buildButton("icons/zoom.png", "Zoom to 1:1 & Center", null, null, null,
                    new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            DiagramTab diagramTab = (DiagramTab) tabbedPane.getSelectedComponent();
                            Canvas canvas = diagramTab.getCanvas();

                            canvas.centerDiagram();
                        }
                    });

            toolBar.add(newButton);
            toolBar.add(openBbutton);
            toolBar.add(saveButton);
            toolBar.add(saveAsButton);
            toolBar.addSeparator();
            toolBar.add(centerDiagram);
            toolBar.add(zoomTo1Diagram);
        }
        return toolBar;
    }

    static void createNewDiagramTab(Consumer<String> currentDiagramFileNameConsumer, JTabbedPane tabbedPane) {
        DiagramTab diagramTab = createNewDiagramTab(currentDiagramFileNameConsumer);
        tabbedPane.addTab("New", diagramTab);
        tabbedPane.setSelectedComponent(diagramTab);

        Canvas canvas = diagramTab.getCanvas();
        SwingUtilities.invokeLater(() -> {
            diagramTab.setDividerLocation(0.8);
            SwingUtilities.invokeLater(canvas::centerDiagram);
            SwingUtilities.invokeLater(canvas::requestFocus);
        });
    }

    static void loadDiagramIntoNewTab(File file, Consumer<String> currentDiagramFileNameConsumer, JTabbedPane tabbedPane) throws Exception {
        DiagramTab diagramTab = createNewDiagramTab(currentDiagramFileNameConsumer);

        var canvas = diagramTab.getCanvas();
        Diagram diagram = HoumsFileFormatManager.loadFile(file.toString());
        canvas.setDiagram(diagram);
        currentDiagramFileNameConsumer.accept(diagramTab.getDiagramName());

        tabbedPane.addTab("New", diagramTab);
        tabbedPane.setSelectedComponent(diagramTab);
        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), diagramTab.getDiagramShortName());

        SwingUtilities.invokeLater(() -> {
            diagramTab.setDividerLocation(0.8);
            SwingUtilities.invokeLater(canvas::fitZoomToWindow);
            SwingUtilities.invokeLater(canvas::requestFocus);
        });
    }

    protected static JButton buildButton(String imageName, String toolTipText, String shortCut, String
            actionCommand, KeyStroke keyStroke, Action action) {

        JButton button = new JButton();
        button.setLayout(new GridLayout(2, 1));

        if (keyStroke != null) {
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionCommand);
            button.getActionMap().put(actionCommand, action);
        }
        button.setAction(action);

        URL resource = MainC2.class.getResource(imageName);
        if (resource == null) {
            throw new RuntimeException("file not found: " + imageName);
        }
        Image ico = Toolkit.getDefaultToolkit().getImage(resource);
        button.setIcon(new ImageIcon(ico));
        button.setToolTipText(toolTipText);

        return button;
    }
}
