package org.homs.houmls;

import org.junit.jupiter.api.Test;

public class ExportAsPngTest {

    final String basePath = "diagrams/";
    final String basePathPrivate = basePath + "private/";

    @Test
    void OrderEntrance() throws Exception {
        ExportAsPng.main(new String[]{basePathPrivate + "OrderEntrance.houmls", "--zoom=3", "--format=png"});
    }

    @Test
    void houmls() throws Exception {
        ExportAsPng.main(new String[]{basePath + "houmls.houmls"});
    }

    @Test
    void welcome() throws Exception {
        ExportAsPng.main(new String[]{basePath + "welcome.houmls", "--zoom=3", "--format=png", "--output=welcome.png", "--grid=false"});
    }

    @Test
    void Anonimizer3() throws Exception {
        ExportAsPng.main(new String[]{basePathPrivate + "Anonimizer3.houmls"});
    }

    @Test
    void BETSampleIdinACKs() throws Exception {
        ExportAsPng.main(new String[]{basePathPrivate + "BET-SampleId-in-ACKs.houmls"});
    }

    @Test
    void CAssert() throws Exception {
        ExportAsPng.main(new String[]{basePath + "CAssert.houmls"});
    }


    @Test
    void kanban() throws Exception {
        ExportAsPng.main(new String[]{basePath + "kanban.houmls", "--output=diagrams/kanban.png"});
    }

    @Test
    void retroBoard() throws Exception {
        ExportAsPng.main(new String[]{basePathPrivate + "retro-board.houmls", "--output=diagrams/private/retro-board.png"});
    }

    @Test
    void houmls_white_paper() throws Exception {
        ExportAsPng.main(new String[]{basePath + "houmls-white-paper.houmls", "--output=diagrams/houmls-white-paper.png"});
    }

    @Test
    void berbis() throws Exception {
        ExportAsPng.main(new String[]{basePathPrivate + "berbis.houmls", "--output=diagrams/private/berbis.png"});
    }

}
