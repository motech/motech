package org.motechproject.security.osgi;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.security.model.PermissionDto;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.BundleException;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;

public class RolesBundleIT extends BaseOsgiIT {


    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 60);
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


    public void testThatRoleThatAllowsRoleManagementIsPresent() throws InterruptedException {
        MotechRoleService motechRoleService = getService(MotechRoleService.class);
        RoleDto role = motechRoleService.getRole(ROLES_ADMIN);
        assertNotNull(role);
        assertTrue(role.getPermissionNames().contains(MANAGE_ROLE));
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToViewRoles() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/roles",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToCreateRoles() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/roles/create",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, POST_DATA, HttpStatus.SC_FORBIDDEN);
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToUpdateRoles() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/roles/update",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, POST_DATA, HttpStatus.SC_FORBIDDEN);
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToDeleteRoles() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/roles/delete",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, POST_DATA, HttpStatus.SC_FORBIDDEN);
    }

    public void testThatAuthorisedUserCanViewRoles() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/roles",
                USER_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_OK);
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToViewPermissions() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/permissions",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }

    public void testThatAuthorisedUserCanViewPermissions() throws Exception {
        get("http://localhost:%s/websecurity/api/web-api/permissions",
                USER_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_OK);
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToCreatePermission() throws Exception {
        post("http://localhost:%s/websecurity/api/web-api/permissions/foo-permission",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, "{}", HttpStatus.SC_FORBIDDEN);
    }

    public void testThatAccessIsDeniedForUnAuthorisedUserTryingToDeletePermission() throws Exception {
        delete("http://localhost:%s/websecurity/api/web-api/permissions/foo-permission",
                USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, HttpStatus.SC_FORBIDDEN);
    }


    @Override
    public void onSetUp() throws InterruptedException {
        MotechPermissionService permissions = getService(MotechPermissionService.class);
        MotechRoleService roles = getService(MotechRoleService.class);
        MotechUserService users = getService(MotechUserService.class);


        PermissionDto someOtherPermission = new PermissionDto(PERMISSION_NAME, BUNDLE_NAME);
        RoleDto someOtherRole = new RoleDto(SOME_ROLE, Arrays.asList(PERMISSION_NAME));

        // when
        permissions.addPermission(someOtherPermission);
        roles.createRole(someOtherRole);

        if (!users.hasUser(USER_AUTHORISED_TO_MANAGE_ROLES)) {
            users.register(USER_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, "test-user-can-manage-roles@mail.com", USER_EXTERNAL_ID, Arrays.asList(ROLES_ADMIN), USER_LOCALE);
        }
        if (!users.hasUser(USER_NOT_AUTHORISED_TO_MANAGE_ROLES)) {
            users.register(USER_NOT_AUTHORISED_TO_MANAGE_ROLES, USER_PASSWORD, "test-user-cannot-manage-roles@mail.com", USER_EXTERNAL_ID, Arrays.asList(SOME_ROLE), USER_LOCALE);
        }
    }


    private void get(String urlTemplate, String username, String password, int exptectedResponseCode) throws Exception {
        String url = String.format(urlTemplate, TestContext.getJettyPort());
        System.out.println(url);
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
        System.out.println(url);
        HttpDelete httpDelete = new HttpDelete(url);
        request(httpDelete, username, password, exptectedResponseCode);
    }

    private void request(HttpUriRequest request, String username, String password, int expectedResponseCode) throws InterruptedException, IOException, BundleException {
        addAuthHeader(request, username, password);

        HttpResponse response = (expectedResponseCode > 400) ?
                httpClient.execute(request, expectedResponseCode) : httpClient.execute(request);

        assertNotNull(response);
        assertEquals(expectedResponseCode, response.getStatusLine().getStatusCode());
    }

    private void addAuthHeader(HttpUriRequest request, String userName, String password) {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.security.domain", "org.motechproject.security.service", "org.motechproject.security.repository"
        );
    }

    @Override
    protected int getRetrievalRetries() {
        return 100;
    }
}
