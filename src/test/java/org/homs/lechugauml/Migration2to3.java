package org.homs.lechugauml;

import org.homs.lechugauml.xml.HoumlsFileFormatManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Migration2to3 {

    protected static List<File> processDirectory(File folder, Predicate<String> fileNamePredicate) {
        List<File> r = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileNamePredicate.test(fileEntry.getName())) {
                System.out.println(fileEntry.getName());
                r.add(fileEntry);
            }
        }
        return r;
    }

    public static void main(String[] args) throws Exception {

        migrateDir("./diagrams", "./diagrams_v3");
        migrateDir("./diagrams/private", "./diagrams_v3/private");
        migrateDir("./diagrams/private/old", "./diagrams_v3/private/old");
    }

    private static void migrateDir(String sourceDir, String destDir) throws Exception {
        new File(destDir).mkdir();
        var fs = processDirectory(new File(sourceDir), name -> name.endsWith(".houmls") || name.endsWith(".uxf"));
        for (var f : fs) {
            var destFile = new File(destDir + "/" + f.getName().replaceFirst("[.][^.]+$", "") + ".uxf3");
            var d = HoumlsFileFormatManager.loadFile_v2(f.toString());
            HoumlsFileFormatManager.writeFile_v3(d, destFile.toString());
        }
    }
}
