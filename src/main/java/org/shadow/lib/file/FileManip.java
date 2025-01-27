package org.shadow.lib.file;

import java.io.File;
import java.nio.file.Files;

public class FileManip {

    public static boolean DeleteIfExists(String path) {
        File file = new File(path);
        if (file.exists()) return file.delete();
        return false;
    }

}
