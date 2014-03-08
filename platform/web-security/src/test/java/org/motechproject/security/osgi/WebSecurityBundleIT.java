package org.motechproject.security.osgi;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.core.io.Resource;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
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
public class WebSecurityBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 100;
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

    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 60);

    public void testDynamicPermissionAccessSecurity() throws InterruptedException, IOException, BundleException {
        updateSecurity("dynamic-permission-access-allow-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_OK);
        request(GET, BAD_USER_NAME, BAD_PASSWORD, HttpStatus.SC_UNAUTHORIZED);

        updateSecurity("dynamic-permission-access-deny-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    public void testDynamicUserAccessSecurity() throws InterruptedException, IOException, BundleException {
        updateSecurity("dynamic-user-access-allow-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_OK);
        request(GET, BAD_USER_NAME, BAD_PASSWORD, HttpStatus.SC_UNAUTHORIZED);

        updateSecurity("dynamic-user-access-deny-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    public void testMethodSpecificSecurity() throws InterruptedException, IOException, BundleException {
        updateSecurity("dynamic-method-specific-GET-deny-test.json");
        request(GET, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);

        updateSecurity("dynamic-method-specific-POST-deny-test.json");
        request(POST, USER_NAME, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);

        updateSecurity("dynamic-method-specific-POST-allow-test.json");
        request(POST, USER_NAME, USER_PASSWORD, HttpStatus.SC_OK);
    }

    public void testWebSecurityServices() throws Exception {
        // given
        MotechPermissionService permissions = getService(MotechPermissionService.class);
        MotechRoleService roles = getService(MotechRoleService.class);
        MotechUserService users = getService(MotechUserService.class);

        PermissionDto permission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto role = new RoleDto(ROLE_NAME, Arrays.asList(PERMISSION_NAME));

        // when
        permissions.addPermission(permission);
        roles.createRole(role);
        users.register(USER_NAME, USER_PASSWORD, USER_EMAIL, USER_EXTERNAL_ID, Arrays.asList(ROLE_NAME), USER_LOCALE);

        // then
        assertTrue(String.format("Permission %s has not been saved", PERMISSION_NAME), permissions.getPermissions().contains(permission));
        assertEquals(String.format("Role %s has not been saved properly", ROLE_NAME), role, roles.getRole(ROLE_NAME));
        assertNotNull(String.format("User %s has not been registered", USER_NAME), users.hasUser(USER_NAME));
        assertTrue(String.format("User doesn't have role %s", ROLE_NAME), users.getRoles(USER_NAME).contains(ROLE_NAME));
    }

    public void testProxyInitialization() throws Exception {
        WebApplicationContext theContext = getWebSecurityContext();
        MotechProxyManager manager = theContext.getBean(MotechProxyManager.class);
        FilterChainProxy proxy = manager.getFilterChainProxy();
        assertNotNull(proxy);
        assertNotNull(proxy.getFilterChains());
    }

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

        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    private String getSecurityString(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + fileName);
        return IOUtils.toString(resource.getInputStream());
    }

    private void request(String requestType, String username, String password, int expectedResponseStatus) throws InterruptedException, IOException, BundleException {

        HttpUriRequest request = null;

        switch (requestType) {
            case "POST":
                request = new HttpPost(String.format(QUERY_URL, TestContext.getJettyPort()));
                break;
            default:
                request = new HttpGet(String.format(QUERY_URL, TestContext.getJettyPort()));
        }

        addAuthHeader(request, username, password);

        // Don't retry when you receive an error code which you are expecting
        HttpResponse response = (expectedResponseStatus > 400) ? httpClient.execute(request, expectedResponseStatus) :
                httpClient.execute(request);

        assertNotNull(response);
        assertEquals(expectedResponseStatus, response.getStatusLine().getStatusCode());
    }

    private WebApplicationContext getWebSecurityContext() throws InvalidSyntaxException, InterruptedException {
        WebApplicationContext theContext = null;

        int tries = 0;

        do {
            ServiceReference[] references =
                    bundleContext.getAllServiceReferences(WebApplicationContext.class.getName(), null);

            for (ServiceReference ref : references) {
                if (SECURITY_BUNDLE_SYMBOLIC_NAME.equals(ref.getBundle().getSymbolicName())) {
                    theContext = (WebApplicationContext) bundleContext.getService(ref);
                    break;
                }
            }

            ++tries;
            Thread.sleep(2000);
        } while (theContext == null && tries < TRIES_COUNT);

        assertNotNull("Unable to retrieve the web security bundle context", theContext);

        return theContext;
    }

    private <T> T getService(Class<T> clazz) throws InterruptedException {
        T service = clazz.cast(bundleContext.getService(getServiceReference(clazz)));

        assertNotNull(String.format("Service %s is not available", clazz.getName()), service);

        return service;
    }

    private <T> ServiceReference getServiceReference(Class<T> clazz) throws InterruptedException {
        ServiceReference serviceReference;
        int tries = 0;

        do {
            serviceReference = bundleContext.getServiceReference(clazz.getName());
            ++tries;
            Thread.sleep(2000);
        } while (serviceReference == null && tries < TRIES_COUNT);

        assertNotNull(String.format("Not found service reference for %s", clazz.getName()), serviceReference);

        return serviceReference;
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
        WebApplicationContext theContext = getWebSecurityContext();
        AllMotechSecurityRules allSecurityRules = theContext.getBean(AllMotechSecurityRules.class);
        allSecurityRules.addOrUpdate(config);
    }

    private void resetSecurityConfig() throws InterruptedException, InvalidSyntaxException {
        WebApplicationContext theContext = getWebSecurityContext();
        AllMotechSecurityRules allSecurityRules = theContext.getBean(AllMotechSecurityRules.class);
        ((AllMotechSecurityRulesCouchdbImpl) allSecurityRules).removeAll();
        getProxyManager().setFilterChainProxy(originalSecurityProxy);
    }

    private MotechProxyManager getProxyManager() throws InterruptedException, InvalidSyntaxException {
        WebApplicationContext theContext = getWebSecurityContext();
        return theContext.getBean(MotechProxyManager.class);
    }

    private void restartSecurityBundle() throws BundleException, InterruptedException, IOException {
        Bundle securityBundle = getBundle(SECURITY_BUNDLE_NAME);
        securityBundle.stop();
        waitForBundleState(securityBundle, RESOLVED);
        securityBundle.start();
        waitForBundleState(securityBundle, ACTIVE);
    }

    @Override
    public void onSetUp() throws InterruptedException, InvalidSyntaxException {
        MotechPermissionService permissions = getService(MotechPermissionService.class);
        MotechRoleService roles = getService(MotechRoleService.class);
        MotechUserService users = getService(MotechUserService.class);

        PermissionDto permission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto role = new RoleDto(ROLE_NAME, Arrays.asList(PERMISSION_NAME));

        // when
        permissions.addPermission(permission);
        roles.createRole(role);
        users.register(USER_NAME, USER_PASSWORD, USER_EMAIL, USER_EXTERNAL_ID, Arrays.asList(ROLE_NAME, SECURITY_ADMIN), USER_LOCALE);
        originalSecurityProxy = getProxyManager().getFilterChainProxy();
    }

    @Override
    public void onTearDown() throws InterruptedException, InvalidSyntaxException {
        resetSecurityConfig();
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.security.domain", "org.motechproject.security.service", "org.motechproject.security.repository"
        );
    }
}
