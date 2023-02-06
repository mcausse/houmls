package org.homs.houmls;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ExportAsPngTest {

    final String basePath = "diagrams/";
    final String basePathPrivate = basePath + "private/";

    protected List<File> processDirectory(File folder, Predicate<String> fileNamePredicate) {
        List<File> r = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                r.addAll(processDirectory(fileEntry, fileNamePredicate));
            } else {
                if (fileNamePredicate.test(fileEntry.getName())) {
                    System.out.println(fileEntry.getName());
                    r.add(fileEntry);
                }
            }
        }
        return r;
    }

    @Test
    void name() throws Exception {
        var fs = processDirectory(new File("."), name -> name.endsWith(".houmls"));
        for (var f : fs) {
            ExportAsPng.main(new String[]{
                    f.toString(),
                    "--zoom=2",
                    "--format=png",
                    "--output=" + f.toString() + ".png"
            });
        }
    }

//    @Test
//    void OrderEntrance() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "OrderEntrance.houmls", "--zoom=3", "--format=png"});
//    }
//
//    @Test
//    void houmls() throws Exception {
//        ExportAsPng.main(new String[]{basePath + "houmls.houmls"});
//    }

    @Test
    void welcome() throws Exception {
        ExportAsPng.main(new String[]{basePath + "welcome.houmls", "--zoom=3", "--format=png", "--output=welcome.png", "--grid=false"});
    }
    @Test
    void houmls_white_paper() throws Exception {
        ExportAsPng.main(new String[]{basePath + "houmls-white-paper.houmls", "--output=./houmls-white-paper.png"});
    }

//    @Test
//    void Anonimizer3() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "Anonimizer3.houmls"});
//    }
//
//    @Test
//    void BETSampleIdinACKs() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "BET-SampleId-in-ACKs.houmls"});
//    }
//
//    @Test
//    void CAssert() throws Exception {
//        ExportAsPng.main(new String[]{basePath + "CAssert.houmls"});
//    }
//
//
//    @Test
//    void kanban() throws Exception {
//        ExportAsPng.main(new String[]{basePath + "kanban.houmls", "--output=diagrams/kanban.png"});
//    }
//
//    @Test
//    void retroBoard() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "retro-board.houmls", "--output=diagrams/private/retro-board.png"});
//    }
//
//    @Test
//    void houmls_white_paper() throws Exception {
//        ExportAsPng.main(new String[]{basePath + "houmls-white-paper.houmls", "--output=diagrams/houmls-white-paper.png"});
//    }
//
//    @Test
//    void berbis() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "berbis.houmls", "--output=diagrams/private/berbis.png"});
//    }
//    @Test
//    void iris_globals() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "IRIS-2022-globals.houmls", "--output=D:\\gitrepos\\cachumber\\BET\\Iris2022-kills/iris-globals.png"});
//    }
//    @Test
//    void new_installer_process_api() throws Exception {
//        ExportAsPng.main(new String[]{basePathPrivate + "new-installer2.houmls", "--output=D:\\gitrepos\\labhub-installer-poc\\process-api.png"});
//    }


}
