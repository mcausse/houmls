package org.homs.houmls.xml;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UxfFileManagerTest {

    @Test
    void name() throws Exception {

        String basePath = "diagrams/";

        // Act
        var d = UxfFileManager.loadFile(basePath + "houmls.houmls");
        UxfFileManager.writeFile(d, basePath + "houmls.houmls.bkp");

        var inputXml = ResourceHelper.resourceToString(basePath + "houmls.houmls.bkp");
        var expectedOutputHl7 = ResourceHelper.resourceToString(basePath + "houmls.houmls");
        assertThat(XmlHelper.normalizeXml(expectedOutputHl7)).isEqualTo(XmlHelper.normalizeXml(inputXml));
    }

}