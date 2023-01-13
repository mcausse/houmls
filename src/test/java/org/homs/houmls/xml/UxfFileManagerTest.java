package org.homs.houmls.xml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UxfFileManagerTest {

    @Test
    void name() throws Exception {

        // Act
        var d = UxfFileManager.loadFile("houmls.uxf");
        UxfFileManager.writeFile(d, "houmls.houmls");


        var inputXml = ResourceHelper.resourceToString("houmls.uxf");
        var expectedOutputHl7 = ResourceHelper.resourceToString("houmls.houmls");
        assertThat(XmlHelper.normalizeXml(expectedOutputHl7)).isEqualTo(XmlHelper.normalizeXml(inputXml));
    }

}