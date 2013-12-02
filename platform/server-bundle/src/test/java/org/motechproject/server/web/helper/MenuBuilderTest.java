package org.motechproject.server.web.helper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.osgi.web.Header;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.dto.ModuleMenu;
import org.motechproject.server.web.dto.ModuleMenuLink;
import org.motechproject.server.web.dto.ModuleMenuSection;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MenuBuilderTest {

    private static final String USERNAME = "motech";

    @InjectMocks
    private MenuBuilder menuBuilder = new MenuBuilder();

    @Mock
    private UIFrameworkService uiFrameworkService;

    @Mock
    private MotechRoleService roleService;

    @Mock
    private MotechUserService userService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle bundle;

    private Dictionary dictionary = new Hashtable();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getHeaders()).thenReturn(dictionary);

        setUpMenu();
        setUpPermissions();
    }

    @Test
    public void shouldBuildMenuWithAllLinks() {
        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("emailRole", "adminRole", "schedulerRole"));

        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertNotNull(menu);

        List<ModuleMenuSection> menuSections = menu.getSections();
        assertNotNull(menuSections);
        assertEquals(3, menuSections.size());

        ModuleMenuSection adminSection = menu.getSections().get(0);
        verifyAdminSection(adminSection);

        ModuleMenuSection wsSection = menu.getSections().get(1);
        verifyWsSection(wsSection);

        ModuleMenuSection modulesSection = menu.getSections().get(2);
        verifyModulesSection(modulesSection, true, true);
    }

    @Test
    public void shouldFilterMenuBasedOnRoles() {
        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("emailRole"));

        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertNotNull(menu);

        List<ModuleMenuSection> menuSections = menu.getSections();
        assertNotNull(menuSections);
        assertEquals(2, menuSections.size());

        ModuleMenuSection wsSection = menu.getSections().get(0);
        verifyWsSection(wsSection);

        ModuleMenuSection modulesSection = menu.getSections().get(1);
        verifyModulesSection(modulesSection, true, false);
    }

    private void setUpMenu() {
        HashMap<String, String> i18n = new HashMap<>();
        Header header = new Header(bundleContext);
        List<String> angularModules = Arrays.asList("m1", "m2");

        ModuleRegistrationData adminRegData = new ModuleRegistrationData("admin", "/admin",
                angularModules, i18n, header);
        adminRegData.setRoleForAccess("adminPerm");
        adminRegData.addSubMenu("#/log", "Log");
        adminRegData.addSubMenu("#/settings", "Settings");
        adminRegData.addSubMenu("#/manage", "manage.modules");
        adminRegData.setNeedsAttention(true);

        ModuleRegistrationData wsRegData = new ModuleRegistrationData("web-security", "/ws",
                angularModules, i18n, header);
        wsRegData.addSubMenu("#/roles", "Roles");
        wsRegData.addSubMenu("#/users", "Users");

        ModuleRegistrationData emailRegData = new ModuleRegistrationData("email", "/email",
                angularModules, i18n, header);
        emailRegData.setRoleForAccess("emailPerm");

        ModuleRegistrationData schedulerRegData = new ModuleRegistrationData("scheduler", "/scheduler",
                angularModules, i18n, header);
        schedulerRegData.setRoleForAccess("schedulerPerm");

        ModuleRegistrationData metricsRegData = new ModuleRegistrationData("metrics", "/metrics",
                angularModules, i18n, header);

        ModuleRegistrationData outboxRegData = new ModuleRegistrationData("outbox", "outbox",
                null, i18n, header);

        Map<String, Collection<ModuleRegistrationData>> modules = new HashMap<>();

        modules.put(UIFrameworkService.MODULES_WITHOUT_SUBMENU,
                Arrays.asList(emailRegData, schedulerRegData, metricsRegData));
        modules.put(UIFrameworkService.MODULES_WITH_SUBMENU, Arrays.asList(adminRegData, wsRegData));
        modules.put(UIFrameworkService.MODULES_WITHOUT_UI, Arrays.asList(outboxRegData));

        when(uiFrameworkService.getRegisteredModules()).thenReturn(modules);
    }


    private void setUpPermissions() {
        RoleDto emailRoleDto = new RoleDto("emailRole", Arrays.asList("emailPerm", "emailPerm2", "emailPerm3"));
        when(roleService.getRole("emailRole")).thenReturn(emailRoleDto);
        RoleDto adminRoleDto = new RoleDto("adminRole", Arrays.asList("adminPerm"));
        when(roleService.getRole("adminRole")).thenReturn(adminRoleDto);
        RoleDto schedulerRoleDto = new RoleDto("schedulerRole", Arrays.asList("schedulerPerm", "test"));
        when(roleService.getRole("schedulerRole")).thenReturn(schedulerRoleDto);
    }

    private void verifyAdminSection(ModuleMenuSection adminSection) {
        assertNotNull(adminSection);
        assertEquals("admin", adminSection.getName());
        assertTrue(adminSection.isNeedsAttention());

        List<ModuleMenuLink> adminLinks = adminSection.getLinks();
        assertNotNull(adminLinks);
        assertEquals(3, adminLinks.size());

        ModuleMenuLink logLink = adminLinks.get(0);
        assertNotNull(logLink);
        assertEquals("admin", logLink.getModuleName());
        assertEquals("Log", logLink.getName());
        assertEquals("#/log", logLink.getUrl());

        ModuleMenuLink settingsLink = adminLinks.get(1);
        assertNotNull(settingsLink);
        assertEquals("admin", settingsLink.getModuleName());
        assertEquals("Settings", settingsLink.getName());
        assertEquals("#/settings", settingsLink.getUrl());

        ModuleMenuLink manageLink = adminLinks.get(2);
        assertNotNull(manageLink);
        assertEquals("admin", manageLink.getModuleName());
        assertEquals("manage.modules", manageLink.getName());
        assertEquals("#/manage", manageLink.getUrl());
    }

    private void verifyModulesSection(ModuleMenuSection modulesSection, boolean emailExpected,
                                      boolean schedulerExpected) {
        assertNotNull(modulesSection);
        assertEquals("server.modules", modulesSection.getName());
        assertFalse(modulesSection.isNeedsAttention());

        List<ModuleMenuLink> moduleLinks = modulesSection.getLinks();
        assertNotNull(moduleLinks);

        int expectedModules = 1;
        if (emailExpected) {
            expectedModules++;
        }
        if (schedulerExpected) {
            expectedModules++;
        }

        assertEquals(expectedModules, moduleLinks.size());

        int index = 0;

        if (emailExpected) {
            ModuleMenuLink emailLink = moduleLinks.get(index);
            index++;

            assertNotNull(emailLink);
            assertEquals("email", emailLink.getName());
            assertEquals("email", emailLink.getModuleName());
            assertEquals("", emailLink.getUrl());
        }

        if (schedulerExpected) {
            ModuleMenuLink schedulerLink = moduleLinks.get(index);
            index++;

            assertNotNull(schedulerLink);
            assertEquals("scheduler", schedulerLink.getName());
            assertEquals("scheduler", schedulerLink.getModuleName());
            assertEquals("", schedulerLink.getUrl());
        }

        ModuleMenuLink metricsLink = moduleLinks.get(index);

        assertNotNull(metricsLink);
        assertEquals("metrics", metricsLink.getName());
        assertEquals("metrics", metricsLink.getModuleName());
        assertEquals("", metricsLink.getUrl());
    }

    private void verifyWsSection(ModuleMenuSection wsSection) {
        assertNotNull(wsSection);
        assertEquals("web-security", wsSection.getName());
        assertFalse(wsSection.isNeedsAttention());

        List<ModuleMenuLink> wsLinks = wsSection.getLinks();
        assertNotNull(wsLinks);
        assertEquals(2, wsLinks.size());

        ModuleMenuLink roleLink = wsLinks.get(0);
        assertNotNull(roleLink);
        assertEquals("web-security", roleLink.getModuleName());
        assertEquals("Roles", roleLink.getName());
        assertEquals("#/roles", roleLink.getUrl());

        ModuleMenuLink usersLink = wsLinks.get(1);
        assertNotNull(usersLink);
        assertEquals("web-security", usersLink.getModuleName());
        assertEquals("Users", usersLink.getName());
        assertEquals("#/users", usersLink.getUrl());
    }

}
