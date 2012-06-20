package org.motechproject.server.osgi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JarInformationHandler {
    public static final String JAR_FILE_EXTENSION = ".jar";
                                            
    private String path;

    private List<JarInformation> jarList;
    
    public JarInformationHandler(String path) {
        this.path = path;
    }

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
                // TODO DO NOTHING
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
