package org.motechproject.testing.osgi;

import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.testing.osgi.http.PollingHttpClient;
import org.motechproject.testing.osgi.mvn.MavenArtifact;
import org.motechproject.testing.osgi.mvn.MavenDependencyListParser;
import org.motechproject.testing.osgi.mvn.PomReader;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
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

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.repositories;
import static org.ops4j.pax.exam.CoreOptions.repository;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

/**
 * The base class for OSGi integration tests running with Pax Exam.
 */
public class BasePaxIT {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private PollingHttpClient pollingHttpClient;

    @Configuration
    public Option[] config() throws IOException {
        return options(
                loggingOptions(),

                frameworkOptions(),

                mvnRepositories(),

                testedBundles(),

                dependencies(),

                junitBundles()
        );
    }

    protected Option loggingOptions() {
        return systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value(getDefaultLogLevel());
    }

    protected Option frameworkOptions() {
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

        return composite(options.toArray(new Option[options.size()]));
    }

    protected Option testedBundles() {
        PomReader pom = new PomReader(getPomPath());
        return mavenBundle(pom.getGroupId(), pom.getArtifactId(), pom.getVersion());
    }

    protected Option mvnRepositories() {
        return repositories(
                repository("http://nexus.motechproject.org/content/repositories/public")
        );
    }

    protected Option dependencies() {
        List<MavenArtifact> mavenDependencies = getMavenArtifacts();

        List<MavenArtifactProvisionOption> options = new ArrayList<>();

        Set<String> ignoredDependencies = getIgnoredDependencies();
        Set<String> testDependencies = getTestDependencies();
        Set<String> includedScopes = getRequiredDependencyScopes();

        for (MavenArtifact artifact : mavenDependencies) {
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();

            MavenArtifactProvisionOption mavenOption = mavenBundle(groupId, artifactId, version);

            String artifactStr = artifact.toGroupArtifactString();

            boolean shouldInclude = includedScopes.contains(artifact.getScope()) ||
                                    testDependencies.contains(artifactStr);

            if (shouldInclude && !ignoredDependencies.contains(artifactStr)) {

                if (isFragment(artifact)) {
                    mavenOption = mavenOption.noStart();
                }

                options.add(mavenOption);
            }
        }

        return composite(options.toArray(new Option[options.size()]));
    }

    protected Set<String> getIgnoredDependencies() {
        return new HashSet<>(Arrays.asList(
                "org.apache.felix:org.apache.felix.framework",
                "org.ops4j.pax.swissbox:pax-swissbox-lifecycle",
                "org.ops4j.pax.swissbox:pax-swissbox-tracker",
                "org.ops4j.pax.exam:pax-exam",
                "org.ops4j.pax.swissbox:pax-swissbox-core"
        ));
    }

    protected Set<String> getKnownFragmentBundles() {
        return new HashSet<>(Arrays.asList(
                "org.slf4j:com.springsource.slf4j.log4j",
                "org.hamcrest:com.springsource.org.hamcrest",
                "org.apache.xerces:com.springsource.org.apache.xerces"
        ));
    }

    protected Set<String> getTestDependencies() {
        Set<String> testDependencies = new HashSet<>(Arrays.asList(
                "org.springframework:org.springframework.web",
                "org.apache.commons:com.springsource.org.apache.commons.httpclient",
                "org.apache.httpcomponents:com.springsource.org.apache.httpcomponents.httpcore",
                "org.apache.httpcomponents:com.springsource.org.apache.httpcomponents.httpclient",
                "org.apache.commons:com.springsource.org.apache.commons.codec",
                "org.motechproject:motech-pax-it"
        ));

        if (startHttpServer()) {
            testDependencies.add("org.apache.felix:org.apache.felix.http.jetty");
        }

        return testDependencies;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected PollingHttpClient getHttpClient() {
        if (pollingHttpClient == null) {
            pollingHttpClient = new PollingHttpClient(new DefaultHttpClient(), getHttpTimeoutInSeconds());
        }
        return pollingHttpClient;
    }

    protected String getDefaultLogLevel() {
        return "ERROR";
    }

    protected int getHttpTimeoutInSeconds() {
        return 60;
    }

    protected boolean startHttpServer() {
        return false;
    }

    protected String getPomPath() {
        return getModulePath() + "/pom.xml";
    }

    protected String getDefaultDependenciesListFilename() {
        return getModulePath() + "/target/dependencies.list";
    }

    protected boolean isFragment(MavenArtifact mavenArtifact) {
        return mavenArtifact.getArtifactId().contains("fragment") ||
                getKnownFragmentBundles().contains(mavenArtifact.toGroupArtifactString());
    }

    protected Set<String> getRequiredDependencyScopes() {
        return new HashSet<>(Arrays.asList("compile"));
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
