package org.homs.lechugauml.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceHelper {

    private ResourceHelper() {
    }

    /**
     * Converts a classpath-relative file name into the full path file name.
     *
     * @param classPathFileName "{@code dako-link-driver-0.0.0.0/Outbound/ProcessACK_XMLToHL7.js}"
     * @return "{@code /D/gitrepos/ventana-connect-oc-driver-engine/target/classes/dako-link-driver-0.0.0.0/Outbound/ProcessOrder.js}"
     */
    public static String getFullFilenameFromClasspath(String classPathFileName) {
        return ClassLoader.getSystemResource(classPathFileName).getFile();
    }

    public static String resourceToString(String resourceName) {
        try (FileInputStream inputStream = new FileInputStream(resourceName)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String classLoaderResourceToString(String resourceName) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new RuntimeException("problem loading a resource from classpath: " + resourceName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
