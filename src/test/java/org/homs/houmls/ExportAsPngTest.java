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

    @Test
    void welcome() throws Exception {
        ExportAsPng.main(new String[]{basePath + "welcome.houmls", "--zoom=3", "--format=png", "--output=welcome.png", "--grid=false"});
    }

    @Test
    void houmls_white_paper() throws Exception {
        ExportAsPng.main(new String[]{basePath + "houmls-white-paper.houmls", "--output=./houmls-white-paper.png"});
    }

}
