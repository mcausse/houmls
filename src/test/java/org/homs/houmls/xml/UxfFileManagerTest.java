package org.homs.houmls.xml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO
@Disabled
class UxfFileManagerTest {

    @Test
    void name() throws Exception {

        var f = UxfFileManager.loadFile("OrderEntrance.uxf");
        UxfFileManager.writeFile(f.getShapes(), "OrderEntrance.uxf2");
        var f2 = UxfFileManager.loadFile("OrderEntrance.uxf2");


        var inputXml = ResourceHelper.resourceToString("OrderEntrance.uxf");
        var expectedOutputHl7 = ResourceHelper.resourceToString("OrderEntrance.uxf2");

        // TODO
        assertThat(XmlHelper.normalizeXml(expectedOutputHl7)).isEqualTo(XmlHelper.normalizeXml(inputXml));
    }

}