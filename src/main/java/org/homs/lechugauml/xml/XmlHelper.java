package org.homs.lechugauml.xml;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlHelper {

    public static void save(Document document, String outputFileName) {

        try (FileOutputStream output = new FileOutputStream(outputFileName)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // pretty print XML
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            // hide the standalone="no"
            document.setXmlStandalone(true);

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);

            transformer.transform(source, result);
        } catch (IOException | TransformerException e) {
            throw new RuntimeException("Failed to successfully create XML file in this directory and name: " + outputFileName, e);
        }
    }

    public static Document xmlToDoc(String xml) {
        try {
            return builder().parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String xmlToString(Document document) {
        try {
            var source = new DOMSource(document);
            var writer = new StringWriter();
            var result = new StreamResult(writer);
            transformer().transform(source, result);
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder builder() throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    private static Transformer transformer() throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        return transformer;
    }

    public static String formatXML(String input) {
        return prettyFormat(input, "2");
    }

    private static String prettyFormat(String input, String indent) {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", indent);
            transformer.transform(xmlInput, new StreamResult(stringWriter));

            return stringWriter.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String normalizeXml(String xml) {
        return xml
                // Normalize the XML
                .replaceAll(">\\s+<", "><")
                // Normalize the HL7 break lines
                .replaceAll("[\\n\\r]+", "\n")
                .trim();
    }
}
