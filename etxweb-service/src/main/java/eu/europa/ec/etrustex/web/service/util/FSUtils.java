package eu.europa.ec.etrustex.web.service.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FSUtils {

    private FSUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void createMissingParentFolders(Path parent) throws IOException {
        if (!parent.toFile().exists()) {
            createMissingParentFolders(parent.getParent());
            Files.createDirectories(parent);
        }
    }

    /**
     * Method to format bytes in human readable format
     *
     * @param bytes
     *            - the value in bytes
     * @param digits
     *            - number of decimals to be displayed
     * @return human readable format string
     */
    public static String format(double bytes, int digits) {
        String[] dictionary = { "bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
        int index;
        for (index = 0; index < dictionary.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }

        String result = String.format("%." + digits + "f", bytes);
        result = result.endsWith(".00")?result.substring(0, result.indexOf(".")):result;
        return  result + " " + dictionary[index];
    }
}
