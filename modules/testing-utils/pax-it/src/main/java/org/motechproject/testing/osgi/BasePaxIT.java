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
import org.motechproject.testing.osgi.mvn.ArtifactJarFilter;
import org.motechproject.testing.osgi.mvn.MavenArtifact;
import org.motechproject.testing.osgi.mvn.MavenDependencyListParser;
import org.motechproject.testing.osgi.mvn.PomReader;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
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

    /**
     * Returns the configuration for the Pax Exam test. This method collects configuration options from
     * more specific methods in this class. In general, overriding the more specific methods called by this method
     * is advised over overriding this method. This method should be only overriden if the test environment needs to
     * be completely changed or extended by new options that don't fit the other methods.
     * @return the configuration for the PAX Exam test
     * @throws IOException if there were errors reading pom or jar files
     * @see #controlOptions()
     * @see #systemOptions()
     * @see #frameworkOptions()
     * @see #mvnRepositories()
     * @see #testedBundles()
     * @see #dependencies()
     */
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

    /**
     * Returns the system options for the Pax Exam tests. These options are primarily internal.
     * These options include an option for setting a system variable that tells Motech to load
     * log4j from the test classpath instead of the regular configuration and an option reqired
     * to get local maven repositories working.
     * @return the system options for Pax Exam
     */
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

    /**
     * Returns the options used by the OSGi framework during the test. These are loaded from the file
     * osgi.properties that's on the classpath (coming from the osgi-platform module).
     * @return the osgi framework options for PAX exam
     */
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

    /**
     * Returns the options describing the bundles that will be tested - this method will return the jar from
     * the target directory. These tests will not start until this bundle is ready - that is enforced by the MotechNativeTestContiner,
     * since this method creates a Pax option for the appropriate system variable.
     * @return the options describing the bundle that will be tested, both its location and its symbolic name as a system variable
     * @throws IOException if there were issues reading the jar from /target
     */
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

    /**
     * Returns the Maven repositories that will be used for retrieving test dependencies.
     * By default this method will return the Motech Nexus as the only Maven repository.
     * @return the options describing Maven repositories
     */
    protected Option mvnRepositories() {
        return repositories(
                repository(MOTECH_NEXUS_URL).id(MOTECH_REPO)
        );
    }

    /**
     * Returns the Pax Exam options describing the dependencies for the test (modules other than the tested one).
     * This method will take all dependencies of the tested module (using Maven) and remove dependencies outside of scopes not
     * being used during the test and dependencies that are marked as ignored. More specific methods than this one should be
     * easier to override in order to control the dependency management for the test. Only the osgi-platform module will be marked for being
     * started by this method, since it is responsible for starting other bundles.
     * @return the Pax Exam options describing the dependencies to use during the test.
     * @see #getAdditionalTestDependencies()
     * @see #getIgnoredDependencies()
     * @see #getRequiredDependencyScopes()
     */
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

    /**
     * Returns options for the PAX Exam test that will control parts of the server operation.
     * These options include an option for faking a module startup OSGi event, timeout for Blueprint
     * dependency injection and timeout for PAX Exam service injection. Those control options can be controlled
     * by their own more specific options.
     * @return options for PAX Exam controlling server operation
     * @see #shouldFakeModuleStartupEvent()
     * @see #getBlueprintDependencyWaitTimeInMillis()
     * @see #getExamDependencyWaitTimeInMillis()
     */
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

    /**
     * Returns the dependencies of the module that should be ignored during the test. This should come in the form
     * of groupId:artifactId strings. These modules will be omitted during the test.
     * @return a set of groupId:artifactId representations of dependencies that should be ignored
     */
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

    /**
     * Returns the dependencies for the test. These are the dependencies that are always required for the test
     * environment. This method will also call {@link #getAdditionalTestDependencies()}, that can be used by a test
     * to provide additional dependencies for the test.
     * @return a set of dependencies for the test, dependencies are represented by groupId:artifactId strings
     */
    protected Set<String> getTestDependencies() {
        Set<String> testDependencies = new HashSet<>(Arrays.asList(
                "org.apache.felix:org.apache.felix.eventadmin",
                "org.apache.felix:org.apache.felix.framework",
                "org.apache.servicemix.bundles:org.apache.servicemix.bundles.spring-web",
                "org.apache.servicemix.bundles:org.apache.servicemix.bundles.spring-webmvc",
                "org.apache.servicemix.bundles:org.apache.servicemix.bundles.spring-context-support",
                "org.apache.servicemix.bundles:org.apache.servicemix.bundles.spring-aop",
                "org.apache.servicemix.bundles:org.apache.servicemix.bundles.spring-security-config",
                "org.springframework.security:spring-security-core",
                "org.springframework.security:spring-security-web",
                "org.apache.httpcomponents:httpclient-osgi",
                "org.apache.httpcomponents:httpcore-osgi",
                "commons-codec:commons-codec",
                "commons-io:commons-io",
                "commons-lang:commons-lang",
                "org.motechproject:motech-osgi-platform",
                "org.motechproject:motech-testing-utils",
                "org.motechproject:motech-pax-it"
        ));

        if (startHttpServer()) {
            testDependencies.add("org.apache.felix:org.apache.felix.http.jetty");
        }

        testDependencies.addAll(getAdditionalTestDependencies());

        return testDependencies;
    }

    /**
     * Returns additional test dependencies. This method returns an empty list and can be overriden
     * by implementing classes in order to provide additional dependencies for the tests.
     * Note that all compile dependencies from Maven will be included in the test - the Maven dependency management
     * system should be used for managing dependencies, this should be only used in special cases.
     * @return a set of additional dependencies for the test, dependencies are represented by groupId:artifactId strings
     */
    protected Collection<String> getAdditionalTestDependencies() {
        return Collections.emptyList();
    }

    /**
     * Retrieves an SLF4J logger instantiated per test instance. Should be used for logging from the test.
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Returns an http client that should be used for testing controllers through real HTTP in the test.
     * The returned client is an instance of a {@link PollingHttpClient}. That client will keep polling the urls
     * until they timeout or return an unexpected response. The timeout for the client can be controlled by calling
     * {@link #setHttpClientTimeout(int)}.
     * @return an instance of the client
     * @see PollingHttpClient
     * @see #setHttpClientTimeout(int)
     */
    protected static PollingHttpClient getHttpClient() {
        if (pollingHttpClient == null) {
            pollingHttpClient = new PollingHttpClient(new DefaultHttpClient(), DEFAULT_HTTP_TIMEOUT);
        }
        return pollingHttpClient;
    }

    /**
     * Sets the maximum wait time for the polling HTTP client that is returned by {@link #getHttpClient()}.
     * @param maxWaitTimeInSeconds the max wait time in seconds
     * @see PollingHttpClient#setMaxWaitPeriodInSeconds(int)
     */
    protected static void setHttpClientTimeout(int maxWaitTimeInSeconds) {
        getHttpClient().setMaxWaitPeriodInSeconds(maxWaitTimeInSeconds);
    }

    /**
     * Returns the Blueprint dependency wait time in milliseconds that will be set for the duration of the tests.
     * This controls how long the Gemini Extender will wait for mandatory dependencies (OSGi services) for a context when creating
     * that context.
     * The default is 5 minutes.
     * @return blueprint dependency wait time in milliseconds
     */
    protected int getBlueprintDependencyWaitTimeInMillis() {
        return DEFAULT_BLUEPRINT_TIMEOUT;
    }

    /**
     * Returns the dependency injection wait time for PAX Exam in milliseconds. The default is 30 seconds.
     * This can be also controlled per Exam injection using the {@link org.ops4j.pax.exam.util.Filter} annotation
     * @return the timeout for PAX Exam dependency injection in milliseconds
     */
    protected int getExamDependencyWaitTimeInMillis() {
        return DEFAULT_EXAM_TIMEOUT;
    }

    /**
     * Controls whether the HTTP server should be started during tests. If set to true it will start the HTTP Jetty
     * bundle, if set to false it won't. The default is true.
     * @return true if the HTTP server should be started for the test, false otherwise
     */
    protected boolean startHttpServer() {
        return true;
    }

    /**
     * Returns whether the OSGi event should be faked by the test container before tests start. Normally this event
     * is fired by the server-bundle module, so faking this event is only required if the server-bundle is not present
     * during the tests (not a dependency). The event being fired is required in order for the osgi-platform to start
     * non-platform Motech modules. The default is true.
     * @return true if the module startup OSGi event should be faked, false otherwise
     */
    protected boolean shouldFakeModuleStartupEvent() {
        return true;
    }

    /**
     * Returns the path for the pom of the tested module. The pom is used for reading artifact, groupId, version, etc.
     * information of the tested module.
     * @return the path to the pom
     */
    protected String getPomPath() {
        return getModulePath() + "/pom.xml";
    }

    /**
     * Returns the path file containing the list of dependencies for the tested module. This file should be generated
     * using the list goal of the Maven dependency plugin. The build of Motech generates these files in the target
     * directory for each module.
     * @return the path to the dependency list file
     */
    protected String getDefaultDependenciesListFilename() {
        return getModulePath() + "/target/dependencies.list";
    }

    /**
     * Returns the dependency scopes that will be included in tests. By default only the <b>compile</b> scope is
     * included.
     * @return a set of the scopes that should be included
     */
    protected Set<String> getRequiredDependencyScopes() {
        return new HashSet<>(Collections.singletonList("compile"));
    }

    /**
     * Returns the quartz scheduler. A bit of hacky way of providing this to test code. Since
     * PAX doesn't have a dependency on quartz or tbe motech-scheduler, the scheduler is returned as an object
     * and should be casted by the caller. This method will retrieve the Spring context for scheduler and get
     * the scheduler by reflection from the scheduler factory bean.
     * @param bundleContext the bundle context that will be used for retrieving the scheduler
     * @return the quartz scheduler from the motech-scheduler module, as a java.lang.Object
     */
    protected Object getQuartzScheduler(BundleContext bundleContext) {
        Object motechSchedulerFactoryBean = getBeanFromBundleContext(bundleContext,
                "org.motechproject.motech-scheduler", "motechSchedulerFactoryBean");

        try {
            return MethodUtils.invokeMethod(motechSchedulerFactoryBean, "getQuartzScheduler", new Object[0]);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to retrieve the quartz scheduler", e);
        }
    }

    /**
     * Retrieves a bean with the given name from the context of a module with the given symbolic name.
     * In general OSGi service injection should be used in PAX ITs for testing the APIs. If access to beans
     * not exposed as OSGi services is required for whatever reason however, then this method can be used for
     * retrieving them. An IllegalStateException will be thrown if there is no such bean.
     * @param bundleContext the bundle context used for retrieval
     * @param bundleSymbolicName the symbolic name of the module
     * @param beanName the name of the bean
     * @return the bean, never null
     */
    protected Object getBeanFromBundleContext(BundleContext bundleContext, String bundleSymbolicName, String beanName) {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext,
                bundleSymbolicName);

        Object bean = context.getBean(beanName);

        if (bean != null) {
            return bean;
        } else {
            throw new IllegalStateException("Unable to retrieve " + beanName + " bean from " + bundleSymbolicName + " context");
        }
    }

    /**
     * Logs into Motech using HTTP. Default credentials of motech/motech will be used. The client from the
     * {@link #getHttpClient()} method will be used for logging in.
     * @throws IOException if there were any communications errors
     * @throws InterruptedException if the client was interrupted during polling
     */
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

    /**
     * Creates an admin user in Motech through HTTP with motech/motech credentials. This method uses the client from
     * the {@link #getHttpClient()} in order to go through the startup screen and register the user.
     * @throws IOException if there were any communications errors
     * @throws InterruptedException if the client was interrupted during polling
     */
    protected static void createAdminUser() throws IOException, InterruptedException {
        String url = String.format("http://localhost:%d/server/startup", TestContext.getJettyPort());
        String json = "{\"language\":\"en\", \"adminLogin\":\"" + MOTECH_ADMIN_USERNAME + "\", " +
                "\"adminPassword\":\"" + MOTECH_ADMIN_PASSWORD + "\", \"adminConfirmPassword\": \"motech\", " +
                "\"adminEmail\":\"motech@motech.com\", \"loginMode\":\"repository\"}";

        StringEntity entity = new StringEntity(json, HTTP.UTF_8);
        entity.setContentType("application/json");

        HttpPost post = new HttpPost(url);
        post.setEntity(entity);

        HttpResponse response = getHttpClient().execute(post);
        EntityUtils.consume(response.getEntity());
    }

    /**
     * This method sets up the security context with the motech/motech credentials and the list
     * of provided permissions. This allows to make calls to secured service methods from the test.
     * @param permissionNames the names of permissions to put into the security context
     */
    protected void setUpSecurityContextForDefaultUser(String... permissionNames) {
        setUpSecurityContext(MOTECH_ADMIN_USERNAME, MOTECH_ADMIN_PASSWORD, permissionNames);
    }

    /**
     * This method sets up the security context with the given username/password credentials and the list
     * of provided permissions. This allows to make calls to secured service methods from the test.
     * @param username the username to set in the context
     * @param password the password to set in the context
     * @param permissionNames the names of permissions to put into the security context
     */
    protected void setUpSecurityContext(String username, String password, String... permissionNames) {
        getLogger().info("Setting up security context with permissions: {}", Arrays.toString(permissionNames));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String permissionName : permissionNames) {
            authorities.add(new SimpleGrantedAuthority(permissionName));
        }

        User principal = new User(username, password, authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
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
}
