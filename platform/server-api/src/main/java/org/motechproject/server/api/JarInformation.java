package org.motechproject.server.api;


import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.motechproject.server.osgi.PlatformConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Holds all important information about JAR.
 */
public class JarInformation {

    private static final Logger LOG = LoggerFactory.getLogger(JarInformation.class);

    public static final String EXTRACTION_FAILED = "Extraction failed.";
    public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    public static final String IMPLEMENTATION_VENDOR_ID = "Implementation-Vendor-Id";
    public static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
    public static final String BUNDLE_VERSION = "Bundle-Version";

    private String path;
    private String filename;
    private String implementationVendorID;
    private String implementationTitle;
    private String implementationVersion;
    private String bundleSymbolicName;
    private String bundleVersion;
    private boolean motechPlatformBundle;
    private List<Dependency> dependencies;
    private List<RemoteRepository> repositories;

    /**
     * Constructor,
     *
     * @param file  the file representation of a jar file or directory containing extracted jar
     * @throws IOException if an I/O error has occurred
     */
    public JarInformation(File file) throws IOException {
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
        StringBuilder pomPath = new StringBuilder();
        pomPath.append("META-INF/maven/").append(bundleSymbolicName.replaceAll("\\.", "/")).append("/pom.xml");
        File pomFile = new File(file, pomPath.toString());
        try (FileInputStream fis = new FileInputStream(pomFile)) {
            parsePOM(fis);
        } catch (Exception e) {
            LOG.error("Error while opening POM file", e);
        }
    }

    private void readPOMFromJar(File file) {
        try (JarFile jarFile = new JarFile(file, false, JarFile.OPEN_READ)) {
            getPOM(jarFile);
        } catch (IOException e) {
            LOG.error("Error while opening POM file", e);
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

    public List<Dependency> getDependencies() {
        return (dependencies == null) ? new LinkedList<Dependency>() : dependencies;
    }

    public List<RemoteRepository> getRepositories() {
        return (repositories == null) ? new LinkedList<RemoteRepository>() : repositories;
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
                    parsePOM(inputStream);
                } catch (IOException e) {
                    LOG.error("Error while opening POM file", e);
                }
            }
        }
    }

    private void parsePOM(InputStream inputStream) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(inputStream);
        } catch (Exception e) {
            LOG.error("Error while reading POM file", e);
        }
        if (dependencies == null) {
            dependencies = new LinkedList<>();
        }
        if (repositories == null) {
            repositories = new LinkedList<>();
        }
        for (org.apache.maven.model.Dependency dependency : model.getDependencies()) {
            if (!"test".equalsIgnoreCase(dependency.getScope())) {
                dependencies.add(new Dependency(new DefaultArtifact(
                    (dependency.getGroupId().contains("${")) ? model.getParent().getGroupId() : dependency.getGroupId(),
                    dependency.getArtifactId(),
                    dependency.getClassifier(),
                    "jar",
                    "[0,)"
                ), JavaScopes.RUNTIME));
            }
        }

        for (Repository remoteRepository : model.getRepositories()) {
            repositories.add(new RemoteRepository(remoteRepository.getId(), "default", remoteRepository.getUrl()));
        }

    }
}
