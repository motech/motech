package org.motechproject.server.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarInformation {
    public static final String EXTRACTION_FAILED = "Extraction failed.";
    public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    public static final String IMPLEMENTATION_VENDOR_ID = "Implementation-Vendor-Id";

    private String path;
    private String filename;
    private String implementationVendorID;
    private String implementationTitle;
    private String implementationVersion;

    public JarInformation(File file) throws IOException {
        readManifestInformation(file);
    }

    public void readManifestInformation(File file) throws IOException {
        if (file.isDirectory()) {
            readManifestFromDirectory(file);
        } else {
            readManifestFromJar(file);
        }
    }

    private void readManifestFromDirectory(File file) throws IOException {
        File manifestFile = new File(file, "META-INF/MANIFEST.MF");
        FileInputStream fis = new FileInputStream(manifestFile);

        Manifest manifest = new Manifest(fis);
        fis.close();

        getManifestData(file, manifest);
    }

    private void readManifestFromJar(File file) throws IOException {
        JarFile jarFile = new JarFile(file, false, JarFile.OPEN_READ);
        Manifest manifest = jarFile.getManifest();
        getManifestData(file, manifest);
    }

    private void getManifestData(File file, Manifest manifest) {
        path = file.getParent();
        filename = file.getName();

        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            implementationVersion = attributes.getValue(IMPLEMENTATION_VERSION);
            implementationTitle = attributes.getValue(IMPLEMENTATION_TITLE);
            implementationVendorID = attributes.getValue(IMPLEMENTATION_VENDOR_ID);
        }
    }

    public String getImplementationVersion() {
        return (implementationVersion == null) ? EXTRACTION_FAILED : implementationVersion;
    }

    public String getImplementationTitle() {
        return implementationTitle;
    }

    public String getImplementationVendorID() {
        return implementationVendorID;
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

}
