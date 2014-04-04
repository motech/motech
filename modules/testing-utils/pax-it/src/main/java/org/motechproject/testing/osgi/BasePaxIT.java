package org.motechproject.testing.osgi;

import org.motechproject.testing.osgi.mvn.MavenArtifact;
import org.motechproject.testing.osgi.mvn.MavenDependencyListParser;
import org.motechproject.testing.osgi.mvn.PomReader;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

/**
 * The base class for OSGi integration tests running with Pax Exam.
 */
public class BasePaxIT {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ProbeBuilder
    public TestProbeBuilder build(TestProbeBuilder builder) {
        return builder;
    }

    @Configuration
    public Option[] config() throws IOException {
        ArrayList<Option> options = new ArrayList<>();

        options.add(systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value(getDefaultLogLevel()));

        options.addAll(getFrameworkOptions());

        options.addAll(getTestedBundles());

        options.addAll(getDependencies());

        options.add(junitBundles());

        return options.toArray(new Option[options.size()]);
    }

    protected List<Option> getFrameworkOptions() {
        List<Option> options = new ArrayList<>();

        try (InputStream in = new FileInputStream("/home/pawel/motech/trunks/motech/motech/platform/server/src/main/resources/osgi.properties")) {
            Properties props = new Properties();
            props.load(in);

            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                options.add(frameworkProperty((String) entry.getKey()).value(entry.getValue()));
            }
        } catch (IOException e) {
            logger.error("Unable to read osgi.properties", e);
        }

        return options;
    }

    protected List<MavenArtifactProvisionOption> getTestedBundles() {
        PomReader pom = new PomReader(getPomPath());
        return Arrays.asList(mavenBundle(pom.getGroupId(), pom.getArtifactId(), pom.getVersion()),
                mavenBundle("org.motechproject", "motech-pax-it", "0.24-SNAPSHOT"));
    }

    protected List<MavenArtifactProvisionOption> getDependencies() {
        List<MavenArtifact> mavenDependencies = getMavenArtifacts();

        List<MavenArtifactProvisionOption> options = new ArrayList<>();

        for (MavenArtifact artifact : mavenDependencies) {
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();

            MavenArtifactProvisionOption mavenOption = mavenBundle(groupId, artifactId, version);

            if (!ignoreBundle(artifact)) {

                if (isFragment(artifact)) {
                    mavenOption = mavenOption.noStart();
                }

                options.add(mavenOption);
            }
        }

        return options;
    }

    protected List<String> getIgnoredDependencies() {
        return new ArrayList<>(Arrays.asList(
                "org.apache.felix:org.apache.felix.framework",
                "org.ops4j.pax.swissbox:pax-swissbox-lifecycle",
                "org.ops4j.pax.swissbox:pax-swissbox-tracker",
                "org.ops4j.pax.exam:pax-exam",
                "org.ops4j.pax.swissbox:pax-swissbox-core"
        ));
    }

    protected List<String> getKnownFragmentBundles() {
        return new ArrayList<>(Arrays.asList(
                "org.slf4j:com.springsource.slf4j.log4j",
                "org.hamcrest:com.springsource.org.hamcrest",
                "org.apache.xerces:com.springsource.org.apache.xerces"
        ));
    }

    protected Logger getLogger() {
        return logger;
    }

    protected String getDefaultLogLevel() {
        return "ERROR";
    }

    protected String getPomPath() {
        return getModulePath() + "/pom.xml";
    }

    protected String getDefaultDependenciesListFilename() {
        return getModulePath() + "/target/dependencies.list";
    }

    protected boolean isFragment(MavenArtifact mavenArtifact) {
        return mavenArtifact.getArtifactId().contains("fragment") ||
                getKnownFragmentBundles().contains(
                        String.format("%s:%s", mavenArtifact.getGroupId(), mavenArtifact.getArtifactId())
                );
    }

    protected boolean ignoreBundle(MavenArtifact artifact) {
        return !dependencyScopes().contains(artifact.getScope()) ||
                getIgnoredDependencies().contains(
                    String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId())
                );
    }

    protected Set<String> dependencyScopes() {
        return new HashSet<>(Arrays.asList("compile"/*, "test"*/));
    }

    private String getModulePath() {
        return getPomPath(URLDecoder.decode(this.getClass().getResource(".").getPath()));
    }

    private String getPomPath(String path) {
        if (path == null) {
            return null;
        }
        if (new File(path + "/pom.xml").exists()) {
            return path;
        }
        return getPomPath(new File(path).getParent());
    }

    private List<MavenArtifact> getMavenArtifacts() {
        try {
            return MavenDependencyListParser.parseDependencies(
                    new FileSystemResource(getDefaultDependenciesListFilename())
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Error loading the dependency list resource", e);
        }
    }
}
