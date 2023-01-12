package org.homs.houmls.xml;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import static org.assertj.core.api.Assertions.assertThat;

class XmlDocumentBuilderTest {

    @Test
    void createEmptyDocument0() throws ParserConfigurationException, XPathExpressionException {

        // Act
        XmlDocumentBuilder xmlBuilder = new XmlDocumentBuilder();

        assertThat(XmlHelper.xmlToString(xmlBuilder.document)).isEqualTo(
                ""
        );
    }

    @Test
    void createTagsInDocument0() throws ParserConfigurationException, XPathExpressionException {

        XmlDocumentBuilder xmlBuilder = new XmlDocumentBuilder();

        // Act
        xmlBuilder.set("CustomerProductInventory", "LIS");

        assertThat(XmlHelper.xmlToString(xmlBuilder.document)).isEqualTo(
                "<CustomerProductInventory>LIS</CustomerProductInventory>"
        );
    }

    @Test
    void createTagsInDocument1() throws ParserConfigurationException, XPathExpressionException {

        XmlDocumentBuilder xmlBuilder = new XmlDocumentBuilder();

        // Act
        xmlBuilder.set("CustomerProductInventory/Hosts/Host/Name", "LIS");
        xmlBuilder.set("CustomerProductInventory/Hosts/Host/Status", "1");

        assertThat(XmlHelper.xmlToString(xmlBuilder.document)).isEqualTo(
                "<CustomerProductInventory>" +
                        "<Hosts>" +
                        "<Host><Name>LIS</Name><Status>1</Status></Host>" +
                        "</Hosts>" +
                        "</CustomerProductInventory>"
        );
    }

    @Test
    void createTagsInDocument2() throws ParserConfigurationException, XPathExpressionException {

        XmlDocumentBuilder xmlBuilder = new XmlDocumentBuilder();

        // Act
        xmlBuilder
                .withPrefix("CustomerProductInventory/Hosts/")
                .set("Host[1]/Name", "[@id='123'][@alias='jou']", "LIS")
                .set("Host[1]/Status", "1")
                .set("Host[2]/Name", "[@id='456'][@alias='aaa']", "VTG")
                .set("Host[2]/Status", "0")
        ;

        assertThat(XmlHelper.xmlToString(xmlBuilder.document)).isEqualTo(
                "<CustomerProductInventory>" +
                        "<Hosts>" +
                        "<Host><Name alias=\"jou\" id=\"123\">LIS</Name><Status>1</Status></Host>" +
                        "<Host><Name alias=\"aaa\" id=\"456\">VTG</Name><Status>0</Status></Host>" +
                        "</Hosts>" +
                        "</CustomerProductInventory>"
        );
    }

}