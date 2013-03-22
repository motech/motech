package org.motechproject.messagecampaign.web.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechRoleServiceImpl;
import org.motechproject.security.service.MotechUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static java.util.Arrays.asList;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class CampaignControllerIT {

    @Autowired
    CampaignController campaignController;

    @Autowired
    private MotechRoleServiceImpl motechRoleService;

    @Autowired
    private MotechUserService motechUserService;


    private String userName = "testuser1";
    private String credentials = "testpass";
    private String userName1 = "testuser2";

    @Before
    public void setup() {
        setUpSecurityContextWithManageCampaignRole();
    }

    @Test(expected = AccessDeniedException.class)
    public void shouldNotAllowToViewCampaignsWithoutManageCampaignPermission() {
        setUpSecurityContextWithoutManageCampaignRole();
        campaignController.getAllCampaigns();
        removeUser();
    }

    @Test
    public void shouldAllowToViewCampaignsWithManageCampaignPermission() {
        campaignController.getAllCampaigns();
    }

    private void removeUser() {
        final UserDto user = new UserDto();
        user.setUserName(userName1);
        motechUserService.deleteUser(user);
        motechRoleService.deleteRole(new RoleDto("NonmanageCampaignRole", null));
    }

    @After
    public void tearDown() throws Exception {
        final UserDto user = new UserDto();
        user.setUserName(userName);
        motechUserService.deleteUser(user);
        motechRoleService.deleteRole(new RoleDto("manageCampaignRole", null));
    }

    private void setUpSecurityContextWithManageCampaignRole() {
        RoleDto manageCampaignRole = new RoleDto("manageCampaignRole", asList("addUser", "editUser", "deleteUser", "manageUser", "activateUser", "manageRole", "manageCampaigns"));
        SecurityContext securityContext = new SecurityContextImpl();
        motechRoleService.createRole(manageCampaignRole);

        motechUserService.register(userName, credentials, "test1@example.com", "testid", Arrays.asList("manageCampaignRole"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userName, credentials);
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setUpSecurityContextWithoutManageCampaignRole() {
        RoleDto role = new RoleDto("NonmanageCampaignRole", asList("addUser", "editUser", "deleteUser", "manageUser", "activateUser", "manageRole"));
        motechRoleService.createRole(role);
        motechUserService.register(userName1, credentials, "test2@example.com", "testid1", Arrays.asList("NonmanageCampaignRole"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userName1, credentials);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }

}
