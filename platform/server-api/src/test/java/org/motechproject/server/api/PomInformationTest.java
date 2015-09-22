package org.motechproject.server.api;

import org.apache.maven.model.Parent;
import org.junit.Test;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PomInformationTest {
    private PomInformation pomInformation;

    @Test
    public void shouldParsePomFile() {
        Properties properties = new Properties();
        properties.put("test.properties", "test");
        properties.put("modules.root.dir", "${basedir}/../..");
        // Because we use <version> tag in our tested pom, the parsing method should add this as property
        properties.put("project.version", "0-27-SNAPSHOT");

        Dependency dependency = new Dependency(new DefaultArtifact(
                "org.motechproject",
                "motech-osgi-platform",
                "",
                "jar",
                "${project.version}"
        ), JavaScopes.RUNTIME);

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("pom.xml");
        pomInformation = new PomInformation();
        pomInformation.parsePom(inputStream);

        assertEquals(properties, pomInformation.getProperties());

        Parent parentFromParsing = pomInformation.getParent();
        assertEquals("0.27-SNAPSHOT", parentFromParsing.getVersion());
        assertEquals("motech", parentFromParsing.getArtifactId());
        assertEquals("org.motechproject", parentFromParsing.getGroupId());

        assertTrue(pomInformation.getDependencies().contains(dependency));
    }

    @Test
    public void shouldParseParentPomFile() {
        Properties properties = new Properties();
        properties.put("test.properties", "testParent");
        // Because we use <version> tag in our tested pom, the parsing method should add this as property
        properties.put("project.version", "0-27-SNAPSHOT");

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("parentPom.xml");
        pomInformation = new PomInformation();
        pomInformation.parseParentPom(inputStream);

        PomInformation parentPom = pomInformation.getParentPomInformation();
        assertEquals(properties, parentPom.getProperties());

        Parent parentFromParsing = parentPom.getParent();
        assertEquals("0.27-SNAPSHOT", parentFromParsing.getVersion());
        assertEquals("motech", parentFromParsing.getArtifactId());
        assertEquals("org.motechproject", parentFromParsing.getGroupId());
    }
}
