package org.motechproject.security.it;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.ex.RoleHasUserException;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.repository.MotechRolesDataService;
import org.motechproject.security.repository.MotechUsersDataService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MotechRoleServiceBundleIT extends BaseIT {

    @Inject
    private MotechRoleService motechRoleService;

    @Inject
    private MotechUserService motechUserService;

    @Inject
    private MotechUsersDataService usersDataService;

    @Inject
    private MotechRolesDataService rolesDataService;

    @Inject
    private BundleContext bundleContext;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        usersDataService.deleteAll();
        rolesDataService.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        usersDataService.deleteAll();
        rolesDataService.deleteAll();
    }

    @Test
    public void testCreate() {
        motechRoleService.createRole(new RoleDto("Test-Role", asList("permissionA", "permissionB"), true));
        RoleDto role = motechRoleService.getRole("Test-Role");
        assertNotNull(role);
        assertEquals("Test-Role", role.getRoleName());
        assertTrue(role.getPermissionNames().contains("permissionA"));
        assertTrue(role.getPermissionNames().contains("permissionB"));
        assertTrue(role.isDeletable());
    }

    @Test
    public void testDelete() {
        motechRoleService.createRole(new RoleDto("Test-Role", asList("permissionA, permissionB"), true));
        RoleDto role = motechRoleService.getRole("Test-Role");
        assertNotNull(role);
        motechRoleService.deleteRole(role);
        role = motechRoleService.getRole("Test-Role");
        assertNull(role);
    }

    @Test
    public void shouldNotDeleteNondeletableRole() {
        motechRoleService.createRole(new RoleDto("Nondeletable-Role", asList("permissionA, permissionB"), false));
        RoleDto role = motechRoleService.getRole("Nondeletable-Role");
        assertNotNull(role);
        motechRoleService.deleteRole(role);
        role = motechRoleService.getRole("Nondeletable-Role");
        assertNotNull(role);
    }

    @Test(expected = RoleHasUserException.class)
    public void shouldNotDeleteRoleWithUsers() {
        motechRoleService.createRole(new RoleDto("Role-With-User", asList("permissionA, permissionB"), true));
        RoleDto role = motechRoleService.getRole("Role-With-User");
        assertNotNull(role);

        motechUserService.registerMotechAdmin("admin", "admin", "aaa@admin.com", Locale.ENGLISH);
        setUpSecurityContext("admin", "admin");

        motechUserService.register("duke", "password", "email", "1234", asList("Role-With-User"), Locale.ENGLISH);

        clearSecurityContext();
        MotechUser motechUser = usersDataService.findByUserName("duke");

        assertNotNull(motechUser);
        assertTrue(motechUser.hasRole("Role-With-User"));

        motechRoleService.deleteRole(role);
    }

    @Test
    public void shouldRefreshMultipleSessionsOnRoleUpdates() throws IOException, InterruptedException {
        // create the admin user, and use his credentials
        motechUserService.registerMotechAdmin("motech", "motech", "aaa@admin.com", Locale.ENGLISH);
        setUpSecurityContext("motech", "motech");

        // create a role
        motechRoleService.createRole(new RoleDto("Role1", asList("permissionA", "permissionB"), true));
        RoleDto role = motechRoleService.getRole("Role1");
        assertNotNull(role);

        // create a second user
        motechUserService.register("duke", "password", "email", "1234", asList("Role1"), Locale.ENGLISH);

        // admin login through HTTP
        login();

        // just start a session
        HttpClient httpClient = HttpClients.createDefault();
        httpClient.execute(new HttpGet(String.format("http://localhost:%d/server/motech-platform-server/", TestContext.getJettyPort())));

        // add a permission to the role
        role.getPermissionNames().add("newPermission");
        motechRoleService.updateRole(role);

        // verify that the role was updated and the user still has it
        role = motechRoleService.getRole("Role1");
        assertNotNull(role);
        assertEquals(asList("permissionA", "permissionB", "newPermission"), role.getPermissionNames());

        UserDto user = motechUserService.getUser("duke");
        assertNotNull(user);
        assertEquals(asList("Role1"), user.getRoles());

        // remove the role from the user so we can delete it
        user.setRoles(Collections.<String>emptyList());
        motechUserService.updateUserDetailsWithoutPassword(user);

        // delete the role and make sure that it's gone
        motechRoleService.deleteRole(role);
        role = motechRoleService.getRole("Role1");
        assertNull(role);
    }
}
