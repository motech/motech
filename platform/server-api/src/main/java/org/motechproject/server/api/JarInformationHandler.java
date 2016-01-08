package org.motechproject.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores information about a jar.
 */
public class JarInformationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarInformationHandler.class);

    public static final String JAR_FILE_EXTENSION = ".jar";

    private String path;

    private List<JarInformation> jarList;

    /**
     * Constructor.
     *
     * @param path  the path to the jar file or directory containing extracted jar
     */
    public JarInformationHandler(String path) {
        this.path = path;
    }

    /**
     * Initializes this handler object.
     */
    public void initHandler() {
        extractJarInformationFromPath();
    }

    public List<JarInformation> getJarList() {
        if (jarList == null) {
            jarList = new ArrayList<JarInformation>();
        }
        return jarList;
    }

    public String getPath() {
        return path;
    }

    public void extractJarInformationFromPath() {
        File file = new File(getPath());
        extractJarInformation(file);
    }

    private void extractJarInformation(File file) {
        if (file.isDirectory()) {
            extractJarInformationFromDirectory(file);
        } else if (file.getAbsolutePath().toLowerCase().endsWith(JAR_FILE_EXTENSION)) {
            try {
                getJarList().add(new JarInformation(file));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void extractJarInformationFromDirectory(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return;
        }

        for (File file : directory.listFiles()) {
            extractJarInformation(file);
        }
    }
}
