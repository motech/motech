package org.motechproject.security.osgi;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.TestContext;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleException;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class RolesBundleIT extends BasePaxIT {

    private static final String BUNDLE_NAME = "bundle";

    private static final String PERMISSION_NAME = "test-permission";
    private static final String SOME_ROLE = "test-role";
    private static final String USER_NOT_AUTHORISED_TO_MANAGE_ROLES = "test-user-cannot-manage-roles";
    private static final String USER_PASSWORD = "test-password";
    private static final String USER_EXTERNAL_ID = "test-externalId";
    private static final Locale USER_LOCALE = Locale.ENGLISH;
    private static final String USER_AUTHORISED_TO_MANAGE_ROLES = "test-user-can-manage-roles";

    private static final String ROLES_ADMIN = "Roles Admin";

    public static final String MANAGE_ROLE = "manageRole";
    private static final String POST_DATA = "{\"roleName\":\"fooRole\",\"originalRoleName\":\"\",\"permissionNames\":[],\"deletable\":true}";

    @Inject
    private MotechRoleService roleService;
    @Inject
    private MotechPermissionService permissionService;
    @Inject
    private MotechUserService userService;

    @Override
    protected boolean startHttpServer() {
        return true;
    }

    @Test
    public void testThatRoleThatAllowsRoleManagementIsPresent() throws InterruptedException {
        RoleDto role = roleService.getRole(ROLES_ADMIN);
        assertNotNull(role);
        assertTrue(role.getPermissionNames().contains(MANAGE_ROLE));
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToViewRoles() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/roles",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToCreateRoles() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/roles/create",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, POST_DATA, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToUpdateRoles() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/roles/update",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, POST_DATA, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToDeleteRoles() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/roles/delete",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, POST_DATA, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testThatAuthorisedUserCanViewRoles() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/roles",
                USER_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_OK);
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToViewPermissions() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/permissions",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testThatAuthorisedUserCanViewPermissions() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/permissions",
                USER_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_OK);
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToCreatePermission() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/permissions/foo-permission",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, "{}", HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToDeletePermission() throws Exception {
        delete("http://localhost:%s/websecurity/api/web-api/permissions/foo-permission",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    @Before
    public void setUp() throws InterruptedException {
        PermissionDto someOtherPermission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto someOtherRole = new RoleDto(SOME_ROLE, Arrays.asList(PERMISSION_NAME));

        // when
        permissionService.addPermission(someOtherPermission);
        roleService.createRole(someOtherRole);

        if (!userService.hasUser(USER_AUTHORISED_TO_MANAGE_ROLES)) {
            userService.register(USER_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, "test-user-can-manage-roles@mail.com",
                    USER_EXTERNAL_ID, Arrays.asList(ROLES_ADMIN), USER_LOCALE);
        }
        if (!userService.hasUser(USER_NOT_AUTHORISED_TO_MANAGE_ROLES)) {
            userService.register(USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, "test-user-cannot-manage-roles@mail.com",
                    USER_EXTERNAL_ID, Arrays.asList(SOME_ROLE), USER_LOCALE);
        }
    }

    private void get(String urlTemplate, String username, String password, int exptectedResponseCode) throws Exception {
        String url = String.format(urlTemplate, TestContext.getJettyPort());
        getLogger().info("GET: " + url);
        HttpGet httpGet = new HttpGet(url);
        request(httpGet, username, password, exptectedResponseCode);
    }

    private void post(String urlTemplate, String username, String password, String postData, int exptectedResponseCode) throws Exception {
        String url = String.format(urlTemplate, TestContext.getJettyPort());
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(postData);
        httpPost.setEntity(entity);
        httpPost.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        request(httpPost, username, password, exptectedResponseCode);
    }

    private void delete(String urlTemplate, String username, String password, int exptectedResponseCode) throws Exception {
        String url = String.format(urlTemplate, TestContext.getJettyPort());
        getLogger().info("Delete: " + url);
        HttpDelete httpDelete = new HttpDelete(url);
        request(httpDelete, username, password, exptectedResponseCode);
    }

    private void request(HttpUriRequest request, String username, String password, int expectedResponseCode) throws InterruptedException, IOException, BundleException {
        addAuthHeader(request, username, password);

        HttpResponse response = (expectedResponseCode > 400) ?
                getHttpClient().execute(request, expectedResponseCode) : getHttpClient().execute(request);

        assertNotNull(response);
        assertEquals(expectedResponseCode, response.getStatusLine().getStatusCode());
    }

    private void addAuthHeader(HttpUriRequest request, String userName, String password) {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }
}
