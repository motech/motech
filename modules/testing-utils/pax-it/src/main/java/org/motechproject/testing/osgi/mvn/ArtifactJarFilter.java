package org.motechproject.testing.osgi.mvn;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A filter that will filter a directory for files ending with the .jar extension.
 */
public class ArtifactJarFilter implements FilenameFilter {
    private final String artifactId;

    public ArtifactJarFilter(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.contains(artifactId) && name.endsWith(".jar");
    }
}
