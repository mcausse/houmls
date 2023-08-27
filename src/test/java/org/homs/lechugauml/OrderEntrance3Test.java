package org.homs.lechugauml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

class OrderEntrance3Test {

    @Disabled
    @Test
    void OrderEntrance3() throws Exception {
        Files.copy(
                new File("diagrams/private/OrderEntrance3.houmls").toPath(),
                new File("D:/gitrepos/cachumber/2023/BET/MM-link-ACKs-to-parent-messages/OrderEntrance3.houmls").toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        ExportAsPng.main(new String[]{"diagrams_v3/private/OrderEntrance3.uxf3", "--zoom=3", "--format=png", "--output=diagrams/private/OrderEntrance3.png", "--grid=false"});
        ExportAsPng.main(new String[]{"diagrams_v3/private/OrderEntrance3.uxf3", "--zoom=3", "--format=png", "--output=D:\\gitrepos\\cachumber\\2023\\BET\\MM-link-ACKs-to-parent-messages/OrderEntrance3.png", "--grid=false"});
        ExportAsPng.main(new String[]{"diagrams_v3/private/OrderEntrance3.uxf3", "--zoom=3", "--format=png", "--output=D:\\gitrepos\\diagrams/OrderEntrance3.png", "--grid=false"});
    }
}
