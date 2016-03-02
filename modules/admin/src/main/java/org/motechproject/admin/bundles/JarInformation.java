package org.motechproject.admin.bundles;


import org.motechproject.server.osgi.util.PlatformConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Holds all important information about JAR.
 */
public class JarInformation {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarInformation.class);

    public static final String BUNDLE_VERSION = "Bundle-Version";

    private static final String EXTRACTION_FAILED = "Extraction failed.";
    private static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    private static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    private static final String IMPLEMENTATION_VENDOR_ID = "Implementation-Vendor-Id";
    private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

    private String path;
    private String filename;
    private String implementationVendorID;
    private String implementationTitle;
    private String implementationVersion;
    private String bundleSymbolicName;
    private String bundleVersion;
    private boolean motechPlatformBundle;
    private PomInformation pomInformation;

    /**
     * Constructor,
     *
     * @param file  the file representation of a jar file or directory containing extracted jar
     * @throws IOException if an I/O error has occurred
     */
    public JarInformation(File file) throws IOException {
        this.pomInformation = new PomInformation();
        readManifestInformation(file);
    }

    /**
     * Reads information from pom file and stores information about repositories and dependencies in this object.
     *
     * @param file  the file representation of a jar file or directory containing extracted jar
     */
    public void readPOMInformation(File file) {
        if (file.isDirectory()) {
            readPOMFromDirectory(file);
        } else {
            readPOMFromJar(file);
        }
    }

    private void readManifestInformation(File file) throws IOException {
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
        jarFile.close();
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
            bundleVersion = attributes.getValue(BUNDLE_VERSION);

            bundleSymbolicName = parseSymbolicName(attributes.getValue(BUNDLE_SYMBOLIC_NAME));

            if (bundleSymbolicName != null) {
                motechPlatformBundle = getBundleSymbolicName().contains(PlatformConstants.PLATFORM_BUNDLE_PREFIX);
            }
        }
    }

    private void readPOMFromDirectory(File file) {
        File pomFile = new File(file, "META-INF/maven/" + bundleSymbolicName.replaceAll("\\.", "/") + "/pom.xml");
        pomInformation.parsePom(pomFile);
    }

    private void readPOMFromJar(File file) {
        try (JarFile jarFile = new JarFile(file, false, JarFile.OPEN_READ)) {
            getPOM(jarFile);
        } catch (IOException e) {
            LOGGER.error("Error while opening POM file", e);
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

    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public boolean isMotechPlatformBundle() {
        return motechPlatformBundle;
    }

    public PomInformation getPomInformation() {
        return pomInformation;
    }

    public void setPomInformation(PomInformation pomInformation) {
        this.pomInformation = pomInformation;
    }

    private String parseSymbolicName(String symbolicNameAttr) {
        String symbolicName = symbolicNameAttr;
        if (symbolicName != null) {
            // symbolic names can end with ;singleton:=true, which is a framework constraint
            // we should trim it from the name
            int indexOfColon = symbolicName.indexOf(';');
            if (indexOfColon >= 0) {
                symbolicName = symbolicName.substring(0, indexOfColon);
            }
            // trailing spaces happen with some bundles
            symbolicName = symbolicName.trim();
        }
        return symbolicName;
    }

    private void getPOM(JarFile jarFile) {
        List<JarEntry> entryList = Collections.list(jarFile.entries());

        for (JarEntry jarEntry : entryList) {
            if (jarEntry.getName().contains("pom.xml")) {
                try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                    pomInformation.parsePom(inputStream);
                } catch (IOException e) {
                    LOGGER.error("Error while opening POM file", e);
                }
            }
        }
    }
}
