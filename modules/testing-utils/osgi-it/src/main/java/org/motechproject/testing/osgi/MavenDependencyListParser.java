package org.motechproject.testing.osgi;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class MavenDependencyListParser {

    private MavenDependencyListParser() {
    }

    public static List<MavenArtifact> parseDependencies(final Resource resource) throws IOException {
         return parseDependencies(new InputStreamReader(resource.getInputStream()));
    }

    public static List<MavenArtifact> parseDependencies(final InputStreamReader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        List<MavenArtifact> artifacts = new ArrayList<MavenArtifact>();
        String line = in.readLine();
        while (line != null) {
            if (isSpecLine(line)) {
                artifacts.add(MavenArtifact.parse(line));
            }
            line = in.readLine();
        }
        return artifacts;
    }

    private static boolean isSpecLine(final String line) {
        if (line.contains("project: MavenProject:")) {
            return false;
        }

        int i = line.indexOf(':');
        if (i > 0) {
            // check for a second colon.
            i = line.indexOf(':', i + 1);
            if (i > 0) {
                // check for a third colon
                return (line.indexOf(':', i + 1) >= 0);
            }
        }
        return false;
    }
}
