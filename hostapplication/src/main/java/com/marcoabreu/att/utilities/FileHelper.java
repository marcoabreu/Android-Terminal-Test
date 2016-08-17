package com.marcoabreu.att.utilities;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by AbreuM on 17.08.2016.
 */
public class FileHelper {
    private FileHelper() {
    }

    /**
     * Returns the base directory of this application
     * @return
     */
    public static Path getApplicationPath() {
        try {
            return Paths.get(FileHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
