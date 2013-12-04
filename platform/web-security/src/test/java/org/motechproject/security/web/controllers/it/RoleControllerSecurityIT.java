package org.motechproject.security.web.controllers.it;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.web.controllers.RoleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.motechproject.security.StubUserService.USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES;
import static org.motechproject.security.StubUserService.USER_WITH_PERMISSION_TO_MANAGE_ROLES;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testSecurityContext.xml")
public class RoleControllerSecurityIT {

    @Autowired
    private RoleController roleController;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Test
    public void shouldAllowViewingAllRolesException() {
        login(USER_WITH_PERMISSION_TO_MANAGE_ROLES);
        roleController.getRoles();
    }

    @Test
    public void shouldAllowRoleCreationWithoutException() {
        login(USER_WITH_PERMISSION_TO_MANAGE_ROLES);
        roleController.saveRole(new RoleDto());
    }

    @Test
    public void shouldAllowRoleDeletionWithoutException() {
        login(USER_WITH_PERMISSION_TO_MANAGE_ROLES);
        roleController.deleteRole(new RoleDto());
    }

    @Test
    public void shouldAllowRoleUpdateWithoutException() {
        login(USER_WITH_PERMISSION_TO_MANAGE_ROLES);
        roleController.updateRole(new RoleDto());
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowRoles() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        roleController.getRoles();
    }


    @Test(expected = AccessDeniedException.class)
    public void shouldDenyRoleCreation() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        roleController.saveRole(new RoleDto());
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowRoleDeletion() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        roleController.deleteRole(new RoleDto("someRole", Collections.<String>emptyList()));
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowRoleUpdate() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        roleController.updateRole(new RoleDto("someRole", Collections.<String>emptyList()));
    }


    private void login(String name) {
        Authentication auth = new UsernamePasswordAuthenticationToken(name, "testPassword");
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(auth));
    }

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }

}
