package org.homs.houmls;

import org.homs.houmls.xml.UxfFileManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExportAsPng {

    // OrderEntrance.houmls --zoom=5.1 --format=png
    public static void main(String[] args) throws Exception {

        final CmdArgumentsProcessor argsProcessor = new CmdArgumentsProcessor(args);
        argsProcessor.processArgs();

        double zoom = Double.parseDouble(argsProcessor.modifiers.getOrDefault("zoom", "1.0"));
        String outputFileFormat = argsProcessor.modifiers.getOrDefault("format", "png");
        String inputFileName = argsProcessor.files.get(0);
        String outputFileName = inputFileName + "." + outputFileFormat;

        //

        Canvas canvas = new Canvas(new JTextArea());
        canvas.diagram = UxfFileManager.loadFile(inputFileName);
        Rectangle diagramBounds = canvas.diagram.getDiagramBounds();
        diagramBounds.grow(150, 150);

        canvas.diagram.zoom = zoom;
        canvas.diagram.offsetX = (diagramBounds.width * zoom - diagramBounds.width) / 2;
        canvas.diagram.offsetY = (diagramBounds.height * zoom - diagramBounds.height) / 2;

        canvas.setSize((int) (diagramBounds.width * zoom), (int) (diagramBounds.height * zoom));
        BufferedImage bi = new BufferedImage((int) (diagramBounds.width * zoom), (int) (diagramBounds.height * zoom), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();

        canvas.paintComponent(g2);

        ImageIO.write(bi, outputFileFormat.toUpperCase(), new File(outputFileName));
    }
}


