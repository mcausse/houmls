package org.homs.houmls;

import org.junit.jupiter.api.Test;

public class ExportAsPngTest {

    @Test
    void name() throws Exception {

        ExportAsPng.main(new String[]{"OrderEntrance.houmls", "--zoom=3", "--format=png"});
        ExportAsPng.main(new String[]{"houmls.uxf"});
        ExportAsPng.main(new String[]{"welcome.houmls", "--zoom=3", "--format=png", "--output=welcome.png"});
    }
}
