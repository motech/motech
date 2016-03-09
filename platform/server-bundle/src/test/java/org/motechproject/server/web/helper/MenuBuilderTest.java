package org.motechproject.server.web.helper;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.SubmenuInfo;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.osgi.web.extension.ApplicationEnvironment;
import org.motechproject.osgi.web.util.ModuleRegistrations;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.dto.ModuleMenu;
import org.motechproject.server.web.dto.ModuleMenuLink;
import org.motechproject.server.web.dto.ModuleMenuSection;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationEnvironment.class)
public class MenuBuilderTest {

    private static final String DOC_URL = "Bundle-DocURL";
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

    @Mock
    private Dictionary dictionary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(ApplicationEnvironment.class);

        when(ApplicationEnvironment.isInDevelopmentMode()).thenReturn(true);

        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getHeaders()).thenReturn(dictionary);

        setUpMenu();
        setUpPermissions();
    }

    @Test
    public void shouldBuildMenuWithAllLinks() {
        setUpRest();
        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("emailRole", "adminRole", "schedulerRole",
                "mcRole", "mdsRole", "viewRestRole"));

        when(dictionary.get(DOC_URL)).thenReturn("http://grameenfoundation.org/");
        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertNotNull(menu);

        List<ModuleMenuSection> menuSections = menu.getSections();
        assertNotNull(menuSections);
        assertEquals(4, menuSections.size());

        ModuleMenuSection adminSection = menu.getSections().get(0);
        verifyAdminSection(adminSection);

        ModuleMenuSection wsSection = menu.getSections().get(1);
        verifyWsSection(wsSection);

        ModuleMenuSection modulesSection = menu.getSections().get(2);
        verifyModulesSection(modulesSection, true, true);

        ModuleMenuSection restSection = menu.getSections().get(3);
        verifyRestSection(restSection, true, true);
    }

    @Test
    public void shouldFilterMenuBasedOnRoles() {
        setUpRest();
        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("emailRole", "mcRole"));
        when(bundle.getHeaders().get(DOC_URL)).thenReturn("www.docs.motechproject.org");
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

    @Test
    public void shouldAddDocumentationUrls() {
        setUpRest();
        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("emailRole", "mcRole"));
        when(bundle.getHeaders().get(DOC_URL)).thenReturn("www.docs.motechproject.org");
        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertEquals(menu.getSections().get(0).getModuleDocsUrl(), "www.docs.motechproject.org");
        assertNotNull(menu);
    }

    @Test
    public void shouldAddDocumentationUrlForModulesTab() {
        setUpRest();
        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("emailRole", "mcRole"));
        when(bundle.getHeaders().get(DOC_URL)).thenReturn("http://docs.motechproject.org/en/latest/modules/email.html");
        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertEquals(null, menu.getSections().get(1).getModuleDocsUrl());
        assertEquals("http://docs.motechproject.org/en/latest/modules/email.html", menu.getSections().get(1).getLinks().get(1).getModuleDocsUrl());
        assertNotNull(menu);
    }


    @Test
    public void shouldNotAddLinksForSubMenusForWhichUserDoesNotHaveRequisiteRole() {

        setUpToTestAccessControlledSubMenuLinks(true);

        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("fooRole"));
        when(bundle.getHeaders().get(DOC_URL)).thenReturn("http://grameenfoundation.org/");
        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertNotNull(menu);

        List<ModuleMenuSection> menuSections = menu.getSections();
        assertNotNull(menuSections);

        ModuleMenuLink onlyFooHasAccessToLink = new ModuleMenuLink("Foo", "foo", "#/foo", false, null);
        ModuleMenuLink onlyBarHasAccessToLink = new ModuleMenuLink("Bar", "foo", "#/bar", false, null);
        ModuleMenuLink linkIsNotAccessControlled = new ModuleMenuLink("Random", "foo", "#/random", false, null);

        ModuleMenuSection fooMenuSection = menuSections.get(0);

        assertNotNull(fooMenuSection);

        assertTrue(fooMenuSection.hasLinkFor(onlyFooHasAccessToLink.getUrl()));
        assertTrue(fooMenuSection.hasLinkFor(linkIsNotAccessControlled.getUrl()));
        assertFalse(fooMenuSection.hasLinkFor(onlyBarHasAccessToLink.getUrl()));
    }

    @Test
    public void shouldNotAddMenuSectionIfUserDoesNotHaveAccessToAnySubMenu() {

        setUpToTestAccessControlledSubMenuLinks(false);

        when(userService.getRoles(USERNAME)).thenReturn(Arrays.asList("someOtherRole"));
        when(bundle.getHeaders().get(DOC_URL)).thenReturn("http://grameenfoundation.org/");
        ModuleMenu menu = menuBuilder.buildMenu(USERNAME);
        assertNotNull(menu);

        List<ModuleMenuSection> menuSections = menu.getSections();
        assertFalse(menuSections.isEmpty());

        assertThat(menuSections.size(), Is.is(1));
        assertThat(menuSections.get(0).getName(), Is.is("server.modules"));
    }

    private void setUpMenu() {
        HashMap<String, String> i18n = new HashMap<>();
        List<String> angularModules = new ArrayList<>();

        ModuleRegistrationData adminRegData = new ModuleRegistrationData("admin", "/admin",
                angularModules, i18n);
        adminRegData.setRoleForAccess("adminPerm");
        adminRegData.addSubMenu("#/log", "Log");
        adminRegData.addSubMenu("#/settings", "Settings");
        adminRegData.addSubMenu("#/manage", "manage.modules");
        adminRegData.setNeedsAttention(true);
        adminRegData.addAngularModule("admin");
        adminRegData.setBundle(bundle);

        ModuleRegistrationData wsRegData = new ModuleRegistrationData("web-security", "/ws",
                angularModules, i18n);
        wsRegData.addSubMenu("#/roles", "Roles");
        wsRegData.addSubMenu("#/users", "Users");
        wsRegData.addAngularModule("webSecurity");
        wsRegData.setBundle(bundle);

        ModuleRegistrationData emailRegData = new ModuleRegistrationData("email", "/email",
                angularModules, i18n);
        List<String> rolesForAccess = new ArrayList<>();
        rolesForAccess.add("emailPerm");
        rolesForAccess.add("otherPerm");
        rolesForAccess.add("completlyOtherPerm");
        emailRegData.setRoleForAccess(rolesForAccess);
        emailRegData.addAngularModule("email");
        emailRegData.setDefaultURL("/email/send");
        emailRegData.setBundle(bundle);

        ModuleRegistrationData schedulerRegData = new ModuleRegistrationData("scheduler", "/scheduler",
                angularModules, i18n);
        schedulerRegData.setRoleForAccess("schedulerPerm");
        schedulerRegData.setDefaultURL("/scheduler");
        schedulerRegData.setBundle(bundle);

        ModuleRegistrationData metricsRegData = new ModuleRegistrationData("metrics", "/metrics",
                angularModules, i18n);
        metricsRegData.setDefaultURL("/metrics");
        metricsRegData.setBundle(bundle);

        ModuleRegistrationData outboxRegData = new ModuleRegistrationData("outbox", "outbox",
                null, i18n);
        outboxRegData.setBundle(bundle);

        ModuleRegistrations modules = new ModuleRegistrations();

        modules.setModulesWithoutSubmenu(Arrays.asList(emailRegData, schedulerRegData, metricsRegData));
        modules.setModulesWithSubMenu(Arrays.asList(adminRegData, wsRegData));
        modules.setModulesWithoutUI(Arrays.asList(outboxRegData));

        when(uiFrameworkService.getRegisteredModules()).thenReturn(modules);
    }

    private void setUpRest() {
        Map<String, String> restDocLinks = new TreeMap<>();
        restDocLinks.put("data-services", "../mds/rest-docs");
        restDocLinks.put("message-campaign", "../message-campaign/apidocs");
        when(uiFrameworkService.getRestDocLinks()).thenReturn(restDocLinks);
    }

    private void setUpToTestAccessControlledSubMenuLinks(boolean addSubMenuWithoutAccessControl) {

        ModuleRegistrationData fooRegData = new ModuleRegistrationData("foo", "foo");
        Map<String, SubmenuInfo> subMenuMap = new HashMap<>();
        SubmenuInfo subMenuWithAccessForUserFoo = new SubmenuInfo("#/foo");
        subMenuWithAccessForUserFoo.setRoleForAccess("foo");
        SubmenuInfo subMenuWithAccessForUserBar = new SubmenuInfo("#/bar");
        subMenuWithAccessForUserBar.setRoleForAccess("bar");
        SubmenuInfo subMenuWithoutAccessControl = new SubmenuInfo("#/random");

        subMenuMap.put("Foo", subMenuWithAccessForUserFoo);
        subMenuMap.put("Bar", subMenuWithAccessForUserBar);
        if (addSubMenuWithoutAccessControl) {
            subMenuMap.put("Random", subMenuWithoutAccessControl);
        }

        fooRegData.setSubMenu(subMenuMap);
        fooRegData.setBundle(bundle);

        ModuleRegistrations modules = new ModuleRegistrations();

        modules.setModulesWithSubMenu(Arrays.asList(fooRegData));

        when(uiFrameworkService.getRegisteredModules()).thenReturn(modules);

        RoleDto fooRole = new RoleDto("fooRole", Arrays.asList("foo"));
        RoleDto someOtherRole = new RoleDto("someOtherRole", Arrays.asList("someOtherPermission"));

        when(roleService.getRole("fooRole")).thenReturn(fooRole);
        when(roleService.getRole("someOtherRole")).thenReturn(someOtherRole);
    }


    private void setUpPermissions() {
        RoleDto emailRoleDto = new RoleDto("emailRole", Arrays.asList("emailPerm", "emailPerm2", "emailPerm3"));
        when(roleService.getRole("emailRole")).thenReturn(emailRoleDto);
        RoleDto adminRoleDto = new RoleDto("adminRole", Arrays.asList("adminPerm"));
        when(roleService.getRole("adminRole")).thenReturn(adminRoleDto);
        RoleDto schedulerRoleDto = new RoleDto("schedulerRole", Arrays.asList("schedulerPerm", "test"));
        when(roleService.getRole("schedulerRole")).thenReturn(schedulerRoleDto);
        RoleDto mdsRoleDto = new RoleDto("mdsRole", Arrays.asList("viewMds"));
        when(roleService.getRole("mdsRole")).thenReturn(mdsRoleDto);
        RoleDto mcRoleDto = new RoleDto("mdsRole", Arrays.asList("viewCampaign"));
        when(roleService.getRole("mcRole")).thenReturn(mcRoleDto);
        RoleDto viewRestRole = new RoleDto("viewRestRole", Collections.singletonList("viewRestApi"));
        when(roleService.getRole("viewRestRole")).thenReturn(viewRestRole);
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
            assertEquals("/email/send", emailLink.getUrl());
        }

        if (schedulerExpected) {
            ModuleMenuLink schedulerLink = moduleLinks.get(index);
            index++;

            assertNotNull(schedulerLink);
            assertEquals("scheduler", schedulerLink.getName());
            assertEquals("scheduler", schedulerLink.getModuleName());
            assertEquals("/scheduler", schedulerLink.getUrl());
        }

        ModuleMenuLink metricsLink = moduleLinks.get(index);

        assertNotNull(metricsLink);
        assertEquals("metrics", metricsLink.getName());
        assertEquals("metrics", metricsLink.getModuleName());
        assertEquals("/metrics", metricsLink.getUrl());
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
        assertEquals("webSecurity", roleLink.getModuleName());
        assertEquals("Roles", roleLink.getName());
        assertEquals("#/roles", roleLink.getUrl());

        ModuleMenuLink usersLink = wsLinks.get(1);
        assertNotNull(usersLink);
        assertEquals("webSecurity", usersLink.getModuleName());
        assertEquals("Users", usersLink.getName());
        assertEquals("#/users", usersLink.getUrl());
    }

    private void verifyRestSection(ModuleMenuSection restSection, boolean mdsLinkExpected, boolean mcLinkExpected) {
        assertNotNull(restSection);
        assertNotNull(restSection.getLinks());

        int expectedLinkCount = 0;
        if (mdsLinkExpected) {
            expectedLinkCount++;
        }
        if (mcLinkExpected) {
            expectedLinkCount++;
        }

        assertEquals(expectedLinkCount, restSection.getLinks().size());

        if (mdsLinkExpected) {
            verifyRestLink(restSection.getLinks().get(0), "data-services");
        }
        if (mcLinkExpected) {
            int mcLinkIndex = mdsLinkExpected ? 1 : 0;
            verifyRestLink(restSection.getLinks().get(mcLinkIndex), "message-campaign");
        }
    }

    private void verifyRestLink(ModuleMenuLink restLink, String moduleName) {
        assertNotNull(restLink);
        assertEquals(moduleName, restLink.getName());
        assertEquals("rest-docs", restLink.getModuleName());
        assertEquals("/rest-docs/" + moduleName, restLink.getUrl());
    }
}
