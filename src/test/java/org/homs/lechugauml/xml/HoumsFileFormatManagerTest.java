package org.homs.lechugauml.xml;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HoumsFileFormatManagerTest {

    @Test
    void name() throws Exception {

        String basePath = "diagrams/";

        // Act
        var d = HoumsFileFormatManager.loadFile(basePath + "houmls.houmls");
        HoumsFileFormatManager.writeFile(d, basePath + "houmls.houmls.bkp");

        var inputXml = ResourceHelper.resourceToString(basePath + "houmls.houmls.bkp");
        var expectedOutputHl7 = ResourceHelper.resourceToString(basePath + "houmls.houmls");
        assertThat(XmlHelper.normalizeXml(expectedOutputHl7)).isEqualTo(XmlHelper.normalizeXml(inputXml));
    }

}