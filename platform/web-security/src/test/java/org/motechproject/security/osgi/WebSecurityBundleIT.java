package org.motechproject.security.osgi;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.domain.MotechSecurityConfiguration;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.osgi.helper.SecurityTestConfigBuilder;
import org.motechproject.security.repository.AllMotechSecurityRules;
import org.motechproject.security.repository.AllMotechSecurityRulesCouchdbImpl;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechProxyManager;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.TestContext;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.RESOLVED;
import static org.osgi.framework.Bundle.UNINSTALLED;

/**
 * Test class that verifies the web security services
 * and dynamic security configuration. Stops and
 * starts the web security bundle and makes HTTP
 * requests with various credentials to test
 * different permutations of dynamic security.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class WebSecurityBundleIT extends BasePaxIT {

    private static final String PERMISSION_NAME = "test-permission";
    private static final String ROLE_NAME = "test-role";
    private static final String SECURITY_ADMIN = "Security Admin";
    private static final String USER_NAME = "test-username";
    private static final String USER_PASSWORD = "test-password";
    private static final String USER_EMAIL = "test@email.com";
    private static final String USER_EXTERNAL_ID = "test-externalId";
    private static final Locale USER_LOCALE = Locale.ENGLISH;
    private static final String BUNDLE_NAME = "bundle";
    private static final String BAD_USER_NAME = "doesNotExist";
    private static final String BAD_PASSWORD = "badpassword";
    private static final String SECURITY_BUNDLE_NAME = "motech-platform-web-security";
    private static final String SECURITY_BUNDLE_SYMBOLIC_NAME = "org.motechproject." + SECURITY_BUNDLE_NAME;
    private static final String QUERY_URL = "http://localhost:%d/websecurity/api/web-api/securityStatus";
    private static final String UPDATE_URL = "http://localhost:%d/websecurity/api/web-api/securityRules";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private FilterChainProxy originalSecurityProxy;

    @Inject
    private MotechPermissionService permissionService;
    @Inject
    private MotechRoleService roleService;
    @Inject
    private MotechUserService userService;
    @Inject
    private BundleContext bundleContext;

    @Override
    protected boolean startHttpServer() {
        return true;
    }

    @Override
    protected Set<String> getTestDependencies() {
        Set<String> testDependencies = super.getTestDependencies();
        testDependencies.add("org.motechproject:motech-osgi-integration-tests");
        return testDependencies;
    }

    @Test
    public void testDynamicPermissionAccessSecurity() throws InterruptedException, IOException, BundleException {
        updateSecurity("dynamic-permission-access-allow-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_OK);
        request(GET, BAD_USER_NAME, BAD_PASSWORD, HttpStatus.SC_UNAUTHORIZED);

        updateSecurity("dynamic-permission-access-deny-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testDynamicUserAccessSecurity() throws InterruptedException, IOException, BundleException {
        updateSecurity("dynamic-user-access-allow-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_OK);
        request(GET, BAD_USER_NAME, BAD_PASSWORD, HttpStatus.SC_UNAUTHORIZED);

        updateSecurity("dynamic-user-access-deny-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testMethodSpecificSecurity() throws InterruptedException, IOException, BundleException {
        updateSecurity("dynamic-method-specific-GET-deny-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);

        updateSecurity("dynamic-method-specific-POST-deny-test.json");
        request(POST, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);

        updateSecurity("dynamic-method-specific-POST-allow-test.json");
        request(POST, USER_NAME, USER_PASSWORD, HttpStatus.SC_OK);
    }

    @Test
    public void testWebSecurityServices() throws Exception {
        // given
        PermissionDto permission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto role = new RoleDto(ROLE_NAME, Arrays.asList(PERMISSION_NAME));

        // when
        permissionService.addPermission(permission);
        roleService.createRole(role);
        userService.register(USER_NAME, USER_PASSWORD, USER_EMAIL, USER_EXTERNAL_ID, Arrays.asList(ROLE_NAME), USER_LOCALE);

        // then
        assertTrue(String.format("Permission %s has not been saved", PERMISSION_NAME),
                permissionService.getPermissions().contains(permission));
        assertEquals(String.format("Role %s has not been saved properly", ROLE_NAME), role,
                roleService.getRole(ROLE_NAME));
        assertNotNull(String.format("User %s has not been registered", USER_NAME),
                userService.hasUser(USER_NAME));
        assertTrue(String.format("User doesn't have role %s", ROLE_NAME),
                userService.getRoles(USER_NAME).contains(ROLE_NAME));
    }

    @Test
    public void testProxyInitialization() throws Exception {
        WebApplicationContext theContext = getSecurityContext();
        MotechProxyManager manager = theContext.getBean(MotechProxyManager.class);
        FilterChainProxy proxy = manager.getFilterChainProxy();
        assertNotNull(proxy);
        assertNotNull(proxy.getFilterChains());
    }

    @Test
    public void testUpdatingProxyOnRestart() throws InterruptedException, BundleException, IOException, ClassNotFoundException, InvalidSyntaxException {
        MotechSecurityConfiguration config = SecurityTestConfigBuilder.buildConfig("noSecurity", null, null);
        updateSecurity(config);

        restartSecurityBundle();

        MotechProxyManager manager = getProxyManager();
        //Receives one chain from config built in test, and two from OSGi IT bundle being scanned for two rules
        //Additionaly, several default rules are merged with the config

        int defaultSize = manager.getDefaultSecurityConfiguration().getSecurityRules().size();
        assertEquals(3 + defaultSize, manager.getFilterChainProxy().getFilterChains().size());

        MotechSecurityConfiguration updatedConfig = SecurityTestConfigBuilder.buildConfig("addPermissionAccess", "anyPermission", null);
        updateSecurity(updatedConfig);

        restartSecurityBundle();

        manager = getProxyManager();
        assertEquals(4 + defaultSize, manager.getFilterChainProxy().getFilterChains().size());
    }

    private void updateSecurity(String fileName) throws IOException, InterruptedException {
        HttpPost request = new HttpPost(String.format(UPDATE_URL, TestContext.getJettyPort()));
        addAuthHeader(request, USER_NAME, USER_PASSWORD);

        StringEntity entity = new StringEntity(getSecurityString(fileName), "application/json", "UTF-8");
        request.setEntity(entity);

        HttpResponse response = getHttpClient().execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    private String getSecurityString(String fileName) throws IOException {
        Resource resource = new ClassPathResource(fileName);
        try (InputStream in  =resource.getInputStream()) {
            return IOUtils.toString(in);
        }
    }

    private void request(String requestType, String username, String password, int expectedResponseStatus) throws InterruptedException, IOException, BundleException {
        HttpUriRequest request;

        switch (requestType) {
            case "POST":
                request = new HttpPost(String.format(QUERY_URL, TestContext.getJettyPort()));
                break;
            default:
                request = new HttpGet(String.format(QUERY_URL, TestContext.getJettyPort()));
        }

        addAuthHeader(request, username, password);

        // Don't retry when you receive an error code which you are expecting
        HttpResponse response = (expectedResponseStatus > 400) ? getHttpClient().execute(request, expectedResponseStatus) :
                getHttpClient().execute(request);

        assertNotNull(response);
        assertEquals(expectedResponseStatus, response.getStatusLine().getStatusCode());
    }

    private void addAuthHeader(HttpUriRequest request, String userName, String password) {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    private Bundle getBundle(String symbolicName) {
        Bundle testBundle = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            if (null != bundle.getSymbolicName() && bundle.getSymbolicName().contains(symbolicName)
                    && UNINSTALLED != bundle.getState()) {
                testBundle = bundle;
                break;
            }
        }
        assertNotNull(testBundle);
        return testBundle;
    }

    private void waitForBundleState(final Bundle bundle, final int state) throws InterruptedException {
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return state == bundle.getState();
            }
        }, 2000).start();
        assertEquals(state, bundle.getState());
    }

    private void updateSecurity(MotechSecurityConfiguration config) throws InterruptedException, InvalidSyntaxException {
        WebApplicationContext theContext = getSecurityContext();
        AllMotechSecurityRules allSecurityRules = theContext.getBean(AllMotechSecurityRules.class);
        allSecurityRules.addOrUpdate(config);
    }

    private void resetSecurityConfig() throws InterruptedException, InvalidSyntaxException {
        WebApplicationContext theContext = getSecurityContext();
        AllMotechSecurityRules allSecurityRules = theContext.getBean(AllMotechSecurityRules.class);
        ((AllMotechSecurityRulesCouchdbImpl) allSecurityRules).removeAll();
        getProxyManager().setFilterChainProxy(originalSecurityProxy);
    }

    private MotechProxyManager getProxyManager() throws InterruptedException, InvalidSyntaxException {
        WebApplicationContext theContext = getSecurityContext();
        return theContext.getBean(MotechProxyManager.class);
    }

    private void restartSecurityBundle() throws BundleException, InterruptedException, IOException {
        Bundle securityBundle = getBundle(SECURITY_BUNDLE_NAME);
        securityBundle.stop();
        waitForBundleState(securityBundle, RESOLVED);
        securityBundle.start();
        waitForBundleState(securityBundle, ACTIVE);
    }

    private WebApplicationContext getSecurityContext() {
        return ServiceRetriever.getWebAppContext(bundleContext, SECURITY_BUNDLE_SYMBOLIC_NAME);
    }

    @Before
    public void setUp() throws InterruptedException, InvalidSyntaxException {
        PermissionDto permission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto role = new RoleDto(ROLE_NAME, Arrays.asList(PERMISSION_NAME));

        // when
        permissionService.addPermission(permission);
        roleService.createRole(role);
        userService.register(USER_NAME, USER_PASSWORD, USER_EMAIL, USER_EXTERNAL_ID,
                Arrays.asList(ROLE_NAME, SECURITY_ADMIN), USER_LOCALE);

        originalSecurityProxy = getProxyManager().getFilterChainProxy();
    }

    @After
    public void tearDown() throws InterruptedException, InvalidSyntaxException {
        resetSecurityConfig();
    }
}
