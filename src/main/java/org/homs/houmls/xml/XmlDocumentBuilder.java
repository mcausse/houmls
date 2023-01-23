package org.homs.houmls.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlDocumentBuilder {

    final Document document;
    final String prefix;

    final Pattern pathPartPattern = Pattern.compile("([^\\[]+)(\\[(\\d+)])?");
    final Pattern attributePattern = Pattern.compile("\\[@([a-zA-Z0-9]+)='([^']*)']");

    public XmlDocumentBuilder(Document document) {
        this.document = document;
        this.prefix = "";
    }

    public XmlDocumentBuilder() throws ParserConfigurationException {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.prefix = "";
    }

    private XmlDocumentBuilder(Document document, String prefix) {
        this.document = document;
        this.prefix = prefix;
    }

    public XmlDocumentBuilder withPrefix(String prefix) {
        return new XmlDocumentBuilder(document, prefix);
    }

    public XmlDocumentBuilder set(String path, Object value) {
        return set(path, null, value);
    }

    public XmlDocumentBuilder set(String path, String attributes, Object value) {

        if (path == null || path.isEmpty()) {
            throw new RuntimeException("the expression is empty");
        }

        final String[] pathParts = (this.prefix + path).split("/");

        Element n = null;
        for (String pathPart : pathParts) {

            // the part should be "ElementName" or "ElementName[n]".
            Matcher m = pathPartPattern.matcher(pathPart);
            if (!m.matches()) {
                throw new RuntimeException(pathPart);
            }

            // parses the part
            final String nodeName = m.group(1);
            final int nodeIndex;
            if (m.group(2) == null || m.group(2).isEmpty()) {
                nodeIndex = 0;
            } else {
                nodeIndex = Integer.parseInt(m.group(3)) - 1;
            }

            // navigates into the current element name (part): if doesn't exist at
            // this moment, we should create it:
            final Element current;
            if (n == null) {
                if (document.getElementsByTagName(nodeName).getLength() == 0) {
                    current = document.createElement(nodeName);
                    document.appendChild(current);
                } else {
                    current = (Element) document.getElementsByTagName(nodeName).item(0);
                }
            } else {
                while (nodeIndex >= n.getElementsByTagName(nodeName).getLength()) {
                    n.appendChild(document.createElement(nodeName));
                }
                current = (Element) n.getElementsByTagName(nodeName).item(nodeIndex);
            }

            n = current;
        }

        if (n == null) {
            throw new RuntimeException("the expression is empty");
        }
        n.appendChild(document.createTextNode(String.valueOf(value)));

        // Processes the attributes values (in the format "[@id='123'][@alias='jou']").
        if (attributes != null) {
            Matcher attributesMatcher = attributePattern.matcher(attributes);
            while (attributesMatcher.find()) {
                String attrName = attributesMatcher.group(1);
                String attrValue = attributesMatcher.group(2);
                n.setAttribute(attrName, attrValue);
            }
        }

        return this;
    }

    public Document getDocument() {
        return document;
    }

    public String getPrefix() {
        return prefix;
    }
}