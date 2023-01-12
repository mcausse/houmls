package org.homs.houmls;

import org.homs.houmls.xml.UxfFileManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExportAsPng {

    public static void main(String[] args) throws Exception {

        double zoom = 5.4;// TODO

        Canvas canvas = new Canvas(new JTextArea());
        canvas.diagram = UxfFileManager.loadFile("OrderEntrance.uxf");
        Rectangle diagramBounds = canvas.diagram.getDiagramBounds();
        diagramBounds.grow(150, 150);

        canvas.diagram.zoom = zoom;
        canvas.diagram.offsetX = (diagramBounds.width * zoom - diagramBounds.width) / 2;
        canvas.diagram.offsetY = (diagramBounds.height * zoom - diagramBounds.height) / 2;

        canvas.setSize((int) (diagramBounds.width * zoom), (int) (diagramBounds.height * zoom));
        BufferedImage bi = new BufferedImage((int) (diagramBounds.width * zoom), (int) (diagramBounds.height * zoom), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();

        canvas.paintComponent(g2);

        ImageIO.write(bi, "PNG", new File("yourImageName_" + (int) zoom + ".PNG"));
    }
}


