package org.homs.houmls.xml;

import org.homs.houmls.shape.Shape;
import org.homs.houmls.shape.impl.Box;
import org.homs.houmls.shape.impl.Comment;
import org.homs.houmls.shape.impl.Connector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UxfFileLoader {


    public static List<Shape> loadFile() throws Exception {

        List<Shape> r = new ArrayList<>();

//        File xmlFile = new File("houmls.uxf");
        File xmlFile = new File("CAssert.uxf");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        var root = doc.getDocumentElement();

        // 10 => 100%
        //  6 =>  60%
        double zoomLevel = Double.parseDouble(doc.getElementsByTagName("zoom_level").item(0).getTextContent());
//        Function<String, Integer> zoomCorrecter = k -> (int) (Integer.parseInt(k) * 10.0 / zoomLevel);
//        Function<Integer, Integer> zoomCorrecterI = k -> (int) (k * 10.0 / zoomLevel);
        Function<String, Integer> zoomCorrecter = Integer::parseInt;
        Function<Integer, Integer> zoomCorrecterI = k -> k;

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


                String attributes = elementElement.getElementsByTagName("panel_attributes").item(0).getTextContent();
                String additionalAttributes = elementElement.getElementsByTagName("additional_attributes").item(0).getTextContent();

                if (id.equals("UMLNote")) {
                    Comment comment = new Comment(
//                            Integer.parseInt(xval),
//                            Integer.parseInt(yval),
//                            Integer.parseInt(wval),
//                            Integer.parseInt(hval),
                            zoomCorrecter.apply(xval),
                            zoomCorrecter.apply(yval),
                            zoomCorrecter.apply(wval),
                            zoomCorrecter.apply(hval),
                            attributes
                    );
                    r.add(comment);

                } else if (id.equals("UMLClass")) {
                    Box box = new Box(
//                            Integer.parseInt(xval),
//                            Integer.parseInt(yval),
//                            Integer.parseInt(wval),
//                            Integer.parseInt(hval),
                            zoomCorrecter.apply(xval),
                            zoomCorrecter.apply(yval),
                            zoomCorrecter.apply(wval),
                            zoomCorrecter.apply(hval),
                            attributes
                    );
                    r.add(box);
                } else if (id.equals("Relation")) {

                    int x = Integer.parseInt(xval);
                    int y = Integer.parseInt(yval);

                    List<Point> points = new ArrayList<>();

                    String[] parts = additionalAttributes.split(";");
                    int j = 0;
                    while (j < parts.length) {
                        int deltax = (int) Double.parseDouble(parts[j++]);
                        int deltay = (int) Double.parseDouble(parts[j++]);
                        points.add(new Point(x + deltax, y + deltay));
                    }

                    Point firstPoint = points.get(0);
                    Point lastPoint = points.get(points.size() - 1);
                    var connector = new Connector(
                            zoomCorrecterI.apply(firstPoint.x),
                            zoomCorrecterI.apply(firstPoint.y),
                            zoomCorrecterI.apply(lastPoint.x),
                            zoomCorrecterI.apply(lastPoint.y),
                            attributes);
                    for (j = 1; j < points.size() - 1; j++) {
                        connector.getMiddlePoints().add(points.get(j));
                    }
                    r.add(connector);
                }

            }
        }

        //
        // linka
        //
        for (var element : r) {
            if (Connector.class.isAssignableFrom(element.getClass())) {
                ((Connector) element).manageLink(r);
            }
        }

        return r;
    }
}
