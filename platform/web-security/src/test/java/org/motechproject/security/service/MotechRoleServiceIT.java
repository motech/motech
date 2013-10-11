package org.motechproject.security.service;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.ex.RoleHasUserException;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechRolesCouchdbImpl;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.repository.AllMotechUsersCouchdbImpl;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;
import static java.util.Arrays.asList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/META-INF/motech/*.xml", "classpath*:/META-INF/security/*.xml" })
public class MotechRoleServiceIT extends SpringIntegrationTest {

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    private MotechRoleService motechRoleService;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Autowired
    @Qualifier("webSecurityDbConnector")
    private CouchDbConnector connector;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MotechPasswordEncoder passwordEncoder;

    @Before
    public void onStartUp() {
        ((AllMotechUsersCouchdbImpl) allMotechUsers).removeAll();
        ((AllMotechRolesCouchdbImpl) allMotechRoles).removeAll();
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
    public void testShouldNotDeleteNondeletableRole() {
        motechRoleService.createRole(new RoleDto("Nondeletable-Role", asList("permissionA, permissionB"), false));
        RoleDto role = motechRoleService.getRole("Nondeletable-Role");
        assertNotNull(role);
        motechRoleService.deleteRole(role);
        role = motechRoleService.getRole("Nondeletable-Role");
        assertNotNull(role);
    }

    @Test(expected = RoleHasUserException.class)
    public void testShouldNotDeleteRoleWithUsers() {
        motechRoleService.createRole(new RoleDto("Role-With-User", asList("permissionA, permissionB"), true));
        RoleDto role = motechRoleService.getRole("Role-With-User");
        assertNotNull(role);

        motechUserService.register("userName", "password", "email", "1234", asList("Role-With-User"), Locale.ENGLISH);
        MotechUser motechUser = allMotechUsers.findByUserName("userName");
        assertNotNull(motechUser);
        assertTrue(motechUser.getRoles().contains("Role-With-User"));

        motechRoleService.deleteRole(role);
    }

    @After
    public void tearDown() {
        ((AllMotechUsersCouchdbImpl) allMotechUsers).removeAll();
        ((AllMotechRolesCouchdbImpl) allMotechRoles).removeAll();
        super.tearDown();
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }
}
