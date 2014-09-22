package org.motechproject.testing.osgi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.motechproject.testing.osgi.http.PollingHttpClient;
import org.motechproject.testing.osgi.mvn.ArtifactJarFilter;
import org.motechproject.testing.osgi.mvn.MavenArtifact;
import org.motechproject.testing.osgi.mvn.MavenDependencyListParser;
import org.motechproject.testing.osgi.mvn.PomReader;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import static org.ops4j.pax.exam.CoreOptions.bundle;
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
 * Classes running with Pax have to extend this class, have a dependencies list file created
 * with the dependencies maven plugin, and specify to run with {@link org.ops4j.pax.exam.junit.PaxExam}
 * as the Junit runner.
 */
public class BasePaxIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_BLUEPRINT_TIMEOUT = 60000; // ms
    private static final int DEFAULT_EXAM_TIMEOUT = 30000; // ms
    private static final int DEFAULT_HTTP_TIMEOUT = 60; // s

    public static final String IGNORE_BUNDLE_LOG_CONFIGS_OPTION = "org.motechproject.logging.ignoreBundles";
    public static final String TESTED_BUNDLE_SYMBOLIC_NAME_OPTION = "org.motechproject.testing.osgi.TestedSymbolicName";
    public static final String FAKE_MODULE_STARTUP_EVENT_OPTION = "org.motechproject.testing.osgi.FakeStartupModulesEvent";
    public static final String BLUEPRINT_WAITTIME_ENV_VAR_NAME = "org.motechproject.blueprint.dependencies.waittime";
    public static final String LOCAL_REPO_MAVEN_SYSTEM_PROPERTY = "maven.repo.local";
    public static final String LOCAL_REPO_PAX_OPTION = "org.ops4j.pax.url.mvn.localRepository";

    public static final String OSGI_PROPERTIES_FILE = "osgi.properties";
    public static final String MOTECH_REPO = "motech-repo";
    public static final String MOTECH_NEXUS_URL = "http://nexus.motechproject.org/content/repositories/public";
    public static final String MOTECH_PLATFORM_BUNDLE = "org.motechproject:motech-osgi-platform";

    public static final String MOTECH_ADMIN_USERNAME = "motech";
    public static final String MOTECH_ADMIN_PASSWORD = "motech";

    private static PollingHttpClient pollingHttpClient;

    @Configuration
    public Option[] config() throws IOException {

        return options(
                controlOptions(),

                systemOptions(),

                frameworkOptions(),

                mvnRepositories(),

                testedBundles(),

                dependencies(),

                junitBundles()
        );
    }

    protected Option systemOptions() {
        List<Option> options = new ArrayList<>();

        options.add(systemProperty(IGNORE_BUNDLE_LOG_CONFIGS_OPTION).value("true"));

        // PAX doesn't support maven local repo option out of the box,
        // so if it is not default, it have to be set manually
        String localRepo = System.getProperty(LOCAL_REPO_MAVEN_SYSTEM_PROPERTY);
        if (localRepo != null) {
            options.add(systemProperty(LOCAL_REPO_PAX_OPTION).value(localRepo));
        }

        return composite(options.toArray(new Option[options.size()]));
    }

    protected Option frameworkOptions() {
        List<Option> options = new ArrayList<>();

        try (InputStream in = new ClassPathResource(OSGI_PROPERTIES_FILE).getInputStream()) {
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

    protected Option testedBundles() throws IOException {
        PomReader pom = new PomReader(getPomPath());
        File targetDir = new File(getModulePath() + "/target");
        File[] files = targetDir.listFiles(new ArtifactJarFilter(pom.getArtifactId()));

        if (ArrayUtils.isEmpty(files)) {
            throw new IllegalStateException("No bundle to test");
        } else {
            List<Option> options = new ArrayList<>();

            for (File file : files) {
                options.add(bundle(FileUtils.toURLs(new File[]{file})[0].toString()).noStart());

                // We want to register the symbolic name as a system property, so the container will now it has to wait
                // for this bundle before starting the tests.
                String symbolicName = getSymbolicNameFromJarFile(file);
                if (StringUtils.isNotBlank(symbolicName)) {
                    options.add(systemProperty(TESTED_BUNDLE_SYMBOLIC_NAME_OPTION).value(symbolicName));
                }
            }

            return composite(options.toArray(new Option[options.size()]));
        }
    }

    protected Option mvnRepositories() {
        return repositories(
                repository(MOTECH_NEXUS_URL).id(MOTECH_REPO)
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

                // we only start the platform bundle
                if (!MOTECH_PLATFORM_BUNDLE.equals(artifactStr)) {
                    mavenOption = mavenOption.noStart();
                }

                options.add(mavenOption);
            }
        }

        return composite(options.toArray(new Option[options.size()]));
    }

    protected Option controlOptions() {
        return composite(
                systemProperty(FAKE_MODULE_STARTUP_EVENT_OPTION).
                        value(String.valueOf(shouldFakeModuleStartupEvent())),
                systemProperty(BLUEPRINT_WAITTIME_ENV_VAR_NAME).
                        value(String.valueOf(getBlueprintDependencyWaitTimeInMillis())),
                systemProperty(org.ops4j.pax.exam.Constants.EXAM_SERVICE_TIMEOUT_KEY).
                        value(String.valueOf(getExamDependencyWaitTimeInMillis()))
        );
    }

    protected Set<String> getIgnoredDependencies() {
        return new HashSet<>(Arrays.asList(
                "org.apache.felix:org.apache.felix.framework",
                "org.ops4j.pax.swissbox:pax-swissbox-lifecycle",
                "org.ops4j.pax.swissbox:pax-swissbox-tracker",
                "org.ops4j.pax.exam:pax-exam",
                "org.ops4j.pax.swissbox:pax-swissbox-core",
                "org.apache.commons:com.springsource.org.apache.commons.logging",
                "org.slf4j:com.springsource.slf4j.api" // we ignore slf4j, since it gets added anyway instead of pax logging
        ));
    }

    protected Set<String> getTestDependencies() {
        Set<String> testDependencies = new HashSet<>(Arrays.asList(
                "org.springframework:org.springframework.web",
                "org.apache.commons:com.springsource.org.apache.commons.httpclient",
                "org.apache.httpcomponents:com.springsource.org.apache.httpcomponents.httpcore",
                "org.apache.httpcomponents:com.springsource.org.apache.httpcomponents.httpclient",
                "org.apache.commons:com.springsource.org.apache.commons.codec",
                "org.apache.commons:com.springsource.org.apache.commons.io",
                "org.apache.commons:com.springsource.org.apache.commons.lang",
                "org.motechproject:motech-osgi-platform",
                "org.motechproject:motech-pax-it"
        ));

        if (startHttpServer()) {
            testDependencies.add("org.apache.felix:org.apache.felix.http.jetty");
        }

        testDependencies.addAll(getAdditionalTestDependencies());

        return testDependencies;
    }

    protected Collection<String> getAdditionalTestDependencies() {
        return Collections.emptyList();
    }

    protected Logger getLogger() {
        return logger;
    }

    protected static PollingHttpClient getHttpClient() {
        if (pollingHttpClient == null) {
            pollingHttpClient = new PollingHttpClient(new DefaultHttpClient(), getHttpTimeoutInSeconds());
        }

        return pollingHttpClient;
    }

    protected static int getHttpTimeoutInSeconds() {
        return DEFAULT_HTTP_TIMEOUT;
    }

    protected int getBlueprintDependencyWaitTimeInMillis() {
        return DEFAULT_BLUEPRINT_TIMEOUT;
    }

    protected int getExamDependencyWaitTimeInMillis() {
        return DEFAULT_EXAM_TIMEOUT;
    }

    protected boolean startHttpServer() {
        return true;
    }

    protected boolean shouldFakeModuleStartupEvent() {
        return true;
    }

    protected String getPomPath() {
        return getModulePath() + "/pom.xml";
    }

    protected String getDefaultDependenciesListFilename() {
        return getModulePath() + "/target/dependencies.list";
    }

    protected Set<String> getRequiredDependencyScopes() {
        return new HashSet<>(Arrays.asList("compile"));
    }

    protected Object getQuartzScheduler(BundleContext bundleContext) {
        Object motechSchedulerFactoryBean = getBeanFromBundleContext(bundleContext,
                "org.motechproject.motech-scheduler", "motechSchedulerFactoryBean");

        try {
            return MethodUtils.invokeMethod(motechSchedulerFactoryBean, "getQuartzScheduler", new Object[0]);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to retrieve the quartz scheduler", e);
        }
    }

    protected Object getBeanFromBundleContext(BundleContext bundleContext, String moduleName, String beanName) {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext,
                moduleName);

        Object bean = context.getBean(beanName);

        if (bean != null) {
            return bean;
        } else {
            throw new IllegalStateException("Unable to retrieve " + beanName + " bean from " + moduleName + " context");
        }
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

    private String getSymbolicNameFromJarFile(File file) throws IOException {
        JarFile jar = new JarFile(file);
        ZipEntry manifestEntry = jar.getEntry("META-INF/MANIFEST.MF");

        String symbolicName = null;

        if (manifestEntry != null) {
            try (InputStream in = jar.getInputStream(manifestEntry)) {
                Manifest manifest = new Manifest(in);
                symbolicName = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
            }
        }

        return symbolicName;
    }

    protected static void login() throws IOException, InterruptedException {
        final HttpPost loginPost = new HttpPost(
                String.format("http://localhost:%d/server/motech-platform-server/j_spring_security_check",
                        TestContext.getJettyPort()));

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", MOTECH_ADMIN_USERNAME));
        nvps.add(new BasicNameValuePair("j_password", MOTECH_ADMIN_PASSWORD));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        HttpResponse response = getHttpClient().execute(loginPost);
        EntityUtils.consume(response.getEntity());
    }

    protected static void createAdminUser() throws IOException, InterruptedException {
        String url = String.format("http://localhost:%d/server/startup", TestContext.getJettyPort());
        String json = "{\"language\":\"en\", \"adminLogin\":\"" + MOTECH_ADMIN_USERNAME + "\", " +
                "\"adminPassword\":\"" + MOTECH_ADMIN_PASSWORD + "\", \"adminConfirmPassword\": \"motech\", " +
                "\"adminEmail\":\"motech@motech.com\", \"loginMode\":\"repository\", \"queueUrl\": \"tcp://localhost:61616\"}";

        StringEntity entity = new StringEntity(json, HTTP.UTF_8);
        entity.setContentType("application/json");

        HttpPost post = new HttpPost(url);
        post.setEntity(entity);

        HttpResponse response = getHttpClient().execute(post);
        EntityUtils.consume(response.getEntity());
    }
}
