package org.homs.lechugauml.xml;

import org.homs.lechugauml.Diagram;
import org.homs.lechugauml.shape.Shape;
import org.homs.lechugauml.shape.impl.box.*;
import org.homs.lechugauml.shape.impl.connector.BocadilloConnector;
import org.homs.lechugauml.shape.impl.connector.Connector;
import org.homs.lechugauml.shape.impl.connector.DoublePoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class HoumlsFileFormatManager {

    public static void writeFile_v2(Diagram diagram, String fileName) throws Exception {

        diagram.setName(fileName);

        String xmlRoot = "diagram";

        List<Shape> shapes = diagram.getShapes();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        XmlDocumentBuilder xmlBuilder = new XmlDocumentBuilder(doc);
        xmlBuilder.set(xmlRoot, "[@program='lechuga-uml'][@version='0.0.3']", "");

        xmlBuilder.withPrefix(xmlRoot)
                .set("/zoom_level", "10")
                .set("/help_text", diagram.getDiagramAttributesText())
        ;

        System.out.printf("*** storing %s shapes: %s\n", shapes.size(), fileName);

        int numShape = 1;
        for (var shape : shapes) {
            final String id;
            if (shape.getClass() == Comment.class) {
                id = "UMLNote";
            } else if (shape.getClass() == Box.class) {
                id = "UMLClass";
            } else if (shape.getClass() == Ellipse.class) {
                id = "UMLUseCase";
            } else if (shape.getClass() == Actor.class) {
                id = "UMLActor";
            } else if (shape.getClass() == RoundedBox.class) {
                id = "UMLState";
            } else if (shape.getClass() == Connector.class) {
                id = "Relation";
            } else if (shape.getClass() == BocadilloConnector.class) {
                id = "Bocadillo";
            } else if (shape.getClass() == TurtleBox.class) {
                id = "TurtleBox";
            } else if (shape.getClass() == FloatingText.class) {
                id = "FloatingText";
            } else if (shape.getClass() == LechugaScriptBox.class) {
                id = "LechugaScriptBox";
            } else if (shape.getClass() == ImageBox.class) {
                id = "ImageBox";
            } else if (shape.getClass() == PackageBox.class) {
                id = "UMLPackage";
            } else {
                throw new RuntimeException(shape.getClass().getName());
            }
            var d = xmlBuilder.withPrefix(xmlRoot + "/element[" + numShape + "]")
                    .set("/id", id)
                    .set("/coordinates/x", shape.getRectangle().x)
                    .set("/coordinates/y", shape.getRectangle().y)
                    .set("/coordinates/w", shape.getRectangle().width)
                    .set("/coordinates/h", shape.getRectangle().height)
                    .set("/panel_attributes", shape.getAttributesText());

            if (Connector.class.isAssignableFrom(shape.getClass())) {
                StringJoiner j = new StringJoiner(";");
                for (var middlePoint : ((Connector) shape).getListOfAbsolutePoints()) {
                    j.add(String.valueOf(middlePoint.x - shape.getRectangle().x));
                    j.add(String.valueOf(middlePoint.y - shape.getRectangle().y));
                }
                d.set("/additional_attributes", j.toString());
            } else {
                d.set("/additional_attributes", "");
            }

            numShape++;
        }

        XmlHelper.save(doc, fileName);
    }

    public static void writeFile_v3(Diagram diagram, String fileName) throws Exception {

        diagram.setName(fileName);

        String xmlRoot = "diagram";

        List<Shape> shapes = diagram.getShapes();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        XmlDocumentBuilder xmlBuilder = new XmlDocumentBuilder(doc);
        xmlBuilder.set(xmlRoot, "[@program='lechuga-uml'][@version='0.0.3']", "");

        xmlBuilder.withPrefix(xmlRoot)
                .set("/zoom_level", "10")
                .set("/help_text", diagram.getDiagramAttributesText())
        ;

        System.out.printf("*** storing %s shapes: %s\n", shapes.size(), fileName);

        int numShape = 1;
        for (var shape : shapes) {
            final String id;
            String lechugaId = "";
            if (shape.getClass() == Comment.class) {
                id = "UMLNote";
            } else if (shape.getClass() == Box.class) {
                id = "UMLClass";
            } else if (shape.getClass() == Ellipse.class) {
                id = "UMLUseCase";
            } else if (shape.getClass() == Actor.class) {
                id = "UMLActor";
            } else if (shape.getClass() == RoundedBox.class) {
                id = "UMLState";
            } else if (shape.getClass() == Connector.class) {
                id = "Relation";
            } else if (shape.getClass() == BocadilloConnector.class) {
                id = "Relation";
                lechugaId = "Bocadillo";
            } else if (shape.getClass() == TurtleBox.class) {
                id = "UMLClass";
                lechugaId = "TurtleBox";
            } else if (shape.getClass() == FloatingText.class) {
                id = "Text"; //"FloatingText";
            } else if (shape.getClass() == LechugaScriptBox.class) {
                id = "UMLClass";
                lechugaId = "LechugaScriptBox";
            } else if (shape.getClass() == ImageBox.class) {
                id = "UMLClass";
                lechugaId = "ImageBox";
            } else if (shape.getClass() == PackageBox.class) {
                id = "UMLPackage";
            } else {
                throw new RuntimeException(shape.getClass().getName());
            }

            String attributesText = shape.getAttributesText();

            // TODO migration-only
            var p = Pattern.compile("bg=(([lL]\\-)+)(\\w+)");
            var m = p.matcher(attributesText);
            if (m.find()) {
                String lighterAmount = m.group(1);
                String color = m.group(3);
                attributesText = attributesText.substring(0, m.start())
                        + "bg=" + color + "\n"
                        + "lighter=" + (lighterAmount.length() / 2) + "\n"
                        + attributesText.substring(m.end());
            }

            var d = xmlBuilder.withPrefix(xmlRoot + "/element[" + numShape + "]")
                    .set("/id", id)
                    .set("/lechuga-id", lechugaId)
                    .set("/coordinates/x", shape.getRectangle().x)
                    .set("/coordinates/y", shape.getRectangle().y)
                    .set("/coordinates/w", shape.getRectangle().width)
                    .set("/coordinates/h", shape.getRectangle().height)
                    .set("/panel_attributes", attributesText);

            if (Connector.class.isAssignableFrom(shape.getClass())) {
                StringJoiner j = new StringJoiner(";");
                for (var middlePoint : ((Connector) shape).getListOfAbsolutePoints()) {
                    j.add(String.valueOf(middlePoint.x - shape.getRectangle().x));
                    j.add(String.valueOf(middlePoint.y - shape.getRectangle().y));
                }
                d.set("/additional_attributes", j.toString());
            } else {
                d.set("/additional_attributes", "");
            }

            numShape++;
        }

        XmlHelper.save(doc, fileName);
    }

    public static Diagram loadFile_v3(String fileName) throws Exception {

        Diagram diagram = new Diagram();

        diagram.setName(fileName);

        File xmlFile = new File(fileName);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        if (doc.getElementsByTagName("help_text").getLength() > 0) {
            String generalAttributes = doc.getElementsByTagName("help_text").item(0).getTextContent();
            diagram.setDiagramAttributesText(generalAttributes);
        }

        NodeList elementList = doc.getElementsByTagName("element");
        for (int i = 0; i < elementList.getLength(); i++) {
            Node elementNode = elementList.item(i);
            if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elementElement = (Element) elementNode;

                String id = elementElement.getElementsByTagName("id").item(0).getTextContent();
                String lechugaId = elementElement.getElementsByTagName("lechuga-id").item(0).getTextContent();

                Node coordsNode = elementElement.getElementsByTagName("coordinates").item(0);
                if (elementNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String xval = ((Element) coordsNode).getElementsByTagName("x").item(0).getTextContent();
                String yval = ((Element) coordsNode).getElementsByTagName("y").item(0).getTextContent();
                String wval = ((Element) coordsNode).getElementsByTagName("w").item(0).getTextContent();
                String hval = ((Element) coordsNode).getElementsByTagName("h").item(0).getTextContent();

                int x = Integer.parseInt(xval);
                int y = Integer.parseInt(yval);
                int w = Integer.parseInt(wval);
                int h = Integer.parseInt(hval);

                String attributes = elementElement.getElementsByTagName("panel_attributes").item(0).getTextContent();
                String additionalAttributes = elementElement.getElementsByTagName("additional_attributes").item(0).getTextContent();

                switch (id) {
                    case "UMLNote":
                        Comment comment = new Comment(x, y, w, h, attributes);
                        diagram.addShape(comment);
                        break;
                    case "UMLGeneric":
                    case "UMLClass": {
                        if (lechugaId == null || lechugaId.isEmpty()) {
                            Box box = new Box(x, y, w, h, attributes);
                            diagram.addShape(box);
                        } else if (lechugaId.equals("TurtleBox")) {
                            TurtleBox box = new TurtleBox(x, y, w, h, attributes);
                            diagram.addShape(box);
                        } else if (lechugaId.equals("LechugaScriptBox")) {
                            LechugaScriptBox box = new LechugaScriptBox(x, y, w, h, attributes);
                            diagram.addShape(box);
                        } else if (lechugaId.equals("ImageBox")) {
                            ImageBox box = new ImageBox(x, y, w, h, attributes);
                            diagram.addShape(box);
                        } else {
                            System.out.println("unrecognized element: " + id);
                        }
                        break;
                    }
                    case "UMLUseCase": {
                        Ellipse box = new Ellipse(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLActor": {
                        Actor box = new Actor(x, y, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLState": {
                        RoundedBox box = new RoundedBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "Text": {
                        FloatingText box = new FloatingText(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLPackage": {
                        PackageBox box = new PackageBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }

                    case "Relation":

                        List<DoublePoint> points = new ArrayList<>();

                        String[] parts = additionalAttributes.split(";");
                        int j = 0;
                        while (j < parts.length) {
                            int deltax = (int) Double.parseDouble(parts[j++]);
                            int deltay = (int) Double.parseDouble(parts[j++]);
                            points.add(new DoublePoint(x + deltax, y + deltay));
                        }

                        DoublePoint firstPoint = points.get(0);
                        DoublePoint lastPoint = points.get(points.size() - 1);

                        final Connector connector;
                        if (lechugaId.equals("Bocadillo")) {
                            connector = new BocadilloConnector(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, attributes);
                        } else {
                            connector = new Connector(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, attributes);
                        }
                        for (j = 1; j < points.size() - 1; j++) {
                            connector.getMiddlePoints().add(points.get(j));
                        }

                        diagram.addShape(connector);
                        break;
                    default:
                        System.out.println("unrecognized element: " + id);
                        break;
                }
            }
        }

        //
        // linka
        //
        diagram.manageConnectorLinks();

        System.out.printf("*** loaded %s shapes: %s\n", diagram.getShapes().size(), fileName);

        return diagram;
    }


    public static Diagram loadFile_v2(String fileName) throws Exception {

        Diagram diagram = new Diagram();

        diagram.setName(fileName);

        File xmlFile = new File(fileName);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        if (doc.getElementsByTagName("help_text").getLength() > 0) {
            String generalAttributes = doc.getElementsByTagName("help_text").item(0).getTextContent();
            diagram.setDiagramAttributesText(generalAttributes);
        }

        NodeList elementList = doc.getElementsByTagName("element");
        for (int i = 0; i < elementList.getLength(); i++) {
            Node elementNode = elementList.item(i);
            if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elementElement = (Element) elementNode;

                String id = elementElement.getElementsByTagName("id").item(0).getTextContent();

                Node coordsNode = elementElement.getElementsByTagName("coordinates").item(0);
                if (elementNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String xval = ((Element) coordsNode).getElementsByTagName("x").item(0).getTextContent();
                String yval = ((Element) coordsNode).getElementsByTagName("y").item(0).getTextContent();
                String wval = ((Element) coordsNode).getElementsByTagName("w").item(0).getTextContent();
                String hval = ((Element) coordsNode).getElementsByTagName("h").item(0).getTextContent();

                int x = Integer.parseInt(xval);
                int y = Integer.parseInt(yval);
                int w = Integer.parseInt(wval);
                int h = Integer.parseInt(hval);

                String attributes = elementElement.getElementsByTagName("panel_attributes").item(0).getTextContent();
                String additionalAttributes = elementElement.getElementsByTagName("additional_attributes").item(0).getTextContent();

                switch (id) {
                    case "UMLNote":
                        Comment comment = new Comment(x, y, w, h, attributes);
                        diagram.addShape(comment);
                        break;
                    case "UMLGeneric":
                    case "UMLClass": {
                        Box box = new Box(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLUseCase": {
                        Ellipse box = new Ellipse(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLActor": {
                        Actor box = new Actor(x, y, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLState": {
                        RoundedBox box = new RoundedBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "TurtleBox": {
                        TurtleBox box = new TurtleBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "FloatingText": {
                        FloatingText box = new FloatingText(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "LechugaScriptBox": {
                        LechugaScriptBox box = new LechugaScriptBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "ImageBox": {
                        ImageBox box = new ImageBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }
                    case "UMLPackage": {
                        PackageBox box = new PackageBox(x, y, w, h, attributes);
                        diagram.addShape(box);
                        break;
                    }

                    case "Relation":
                    case "Bocadillo":

                        List<DoublePoint> points = new ArrayList<>();

                        String[] parts = additionalAttributes.split(";");
                        int j = 0;
                        while (j < parts.length) {
                            int deltax = (int) Double.parseDouble(parts[j++]);
                            int deltay = (int) Double.parseDouble(parts[j++]);
                            points.add(new DoublePoint(x + deltax, y + deltay));
                        }

                        DoublePoint firstPoint = points.get(0);
                        DoublePoint lastPoint = points.get(points.size() - 1);

                        final Connector connector;
                        if (id.equals("Relation")) {
                            connector = new Connector(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, attributes);
                        } else if (id.equals("Bocadillo")) {
                            connector = new BocadilloConnector(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y, attributes);
                        } else {
                            throw new RuntimeException();
                        }
                        for (j = 1; j < points.size() - 1; j++) {
                            connector.getMiddlePoints().add(points.get(j));
                        }

                        diagram.addShape(connector);
                        break;
                    default:
                        System.out.println("unrecognized element: " + id);
                        break;
                }
            }
        }

        //
        // linka
        //
        diagram.manageConnectorLinks();

        System.out.printf("*** loaded %s shapes: %s\n", diagram.getShapes().size(), fileName);

        return diagram;
    }

}
