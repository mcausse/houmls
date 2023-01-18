package org.homs.houmls;

import org.homs.houmls.xml.HoumsFileFormatManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExportAsPng {

    public static void main(String[] args) throws Exception {

        final CmdArgumentsProcessor argsProcessor = new CmdArgumentsProcessor(args);
        argsProcessor.processArgs();

        String inputFileName = argsProcessor.files.get(0);
        double zoom = Double.parseDouble(argsProcessor.modifiers.getOrDefault("zoom", "3.0"));
        String outputFileFormat = argsProcessor.modifiers.getOrDefault("format", "png");
        String outputFileName = argsProcessor.modifiers.getOrDefault("output", inputFileName + "." + outputFileFormat);

        //
        System.out.print("Exporting: " + String.join(" ", args) + "...");

        Canvas canvas = new Canvas(new JTextArea());
        canvas.setDiagram(HoumsFileFormatManager.loadFile(inputFileName));
        Rectangle diagramBounds = canvas.getDiagram().getDiagramBounds();
        diagramBounds.grow(100, 100);

        canvas.setSize((int) (diagramBounds.width * zoom), (int) (diagramBounds.height * zoom));
        canvas.centerDiagram();
        canvas.getDiagram().zoom = zoom;

        BufferedImage bi = new BufferedImage((int) (diagramBounds.width * zoom), (int) (diagramBounds.height * zoom), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();

        canvas.paintComponent(g2);

        ImageIO.write(bi, outputFileFormat.toUpperCase(), new File(outputFileName));

        System.out.println(" OK");
    }
}


