package org.motechproject.testing.osgi;

import java.io.File;
import java.io.FilenameFilter;

class ArtifactJarFilter implements FilenameFilter {
    private final String artifactId;

    public ArtifactJarFilter(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.contains(artifactId) && name.endsWith(".jar");
    }
}
