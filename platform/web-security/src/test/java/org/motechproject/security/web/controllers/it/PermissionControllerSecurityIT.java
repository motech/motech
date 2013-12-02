package org.motechproject.security.web.controllers.it;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.web.controllers.PermissionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.motechproject.security.StubUserService.USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES;
import static org.motechproject.security.StubUserService.USER_WITH_PERMISSION_TO_MANAGE_ROLES;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testSecurityContext.xml")
public class PermissionControllerSecurityIT {

    @Autowired
    private PermissionController permissionController;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Test
    public void shouldAllowCreationOfPermissionWithoutException() {
        login(USER_WITH_PERMISSION_TO_MANAGE_ROLES);
        permissionController.savePermission("some-permission");
    }

    @Test
    public void shouldAllowDeleteOfPermissionWithoutException() {
        login(USER_WITH_PERMISSION_TO_MANAGE_ROLES);
        permissionController.deletePermission("some-permission");
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowViewingOfPermissions() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        permissionController.getPermissions();
    }


    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowCreationOfPermission() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        permissionController.savePermission("some-permission");
    }


    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowDeletionOfPermission() {
        login(USER_WITHOUT_PERMISSION_TO_MANAGE_ROLES);
        permissionController.deletePermission("some-permission");
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
