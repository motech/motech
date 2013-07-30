package org.motechproject.server.web.controller;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.UserInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.ENGLISH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITHOUT_SUBMENU;
import static org.motechproject.osgi.web.UIFrameworkService.MODULES_WITH_SUBMENU;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StartupManager.class)
public class DashboardControllerTest {
    private static final String LANG = ENGLISH.getLanguage();
    private static final String USER_NAME = "testUser";
    private static final String CURRENT_MODULE = "currentModule";
    private static final String MODULE_NAME = "demo";
    @Mock
    private StartupManager startupManager;

    @Mock
    private UIFrameworkService uiFrameworkService;

    @Mock
    private LocaleSettings localeSettings;

    @Mock
    private MotechUserService userService;

    @Mock
    private MotechRoleService roleService;

    @InjectMocks
    private DashboardController controller = new DashboardController();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext context;

    @Mock
    private ModuleRegistrationData moduleRegistrationData;

    @Mock
    private Principal principal;

    @Before
    public void setUp() {
        initMocks(this);
        Locale en = new Locale("en");
        when(localeSettings.getUserLocale(request)).thenReturn(en);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("server.admin");
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(context);
        when(context.getContextPath()).thenReturn("/");
    }

    @Test
    public void testDashboardNoModule() {
        ModelAndView result = controller.index(null, request);

        Assert.assertEquals("index", result.getViewName());
        Assert.assertNull(result.getModelMap().get(CURRENT_MODULE));
    }

    @Test
    public void testDashboardWithModule() {
        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(moduleRegistrationData);

        ModelAndView result = controller.index(MODULE_NAME, request);

        Assert.assertEquals("index", result.getViewName());
        Assert.assertEquals(moduleRegistrationData, result.getModelMap().get(CURRENT_MODULE));
        verify(uiFrameworkService).getModuleData(MODULE_NAME);
        verify(uiFrameworkService).moduleBackToNormal(MODULE_NAME);
    }

    @Test
    public void testDashboardDisplayLinksWhichDontRequireAuthorization() {

        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(moduleRegistrationData);
        UserDto userDto = new UserDto();
        userDto.setRoles(Arrays.asList("some role"));
        when(userService.getUser("server.admin")).thenReturn(userDto);

        Map<String, Collection<ModuleRegistrationData>> hashMap = new HashMap<>();
        hashMap.put(MODULES_WITHOUT_SUBMENU, Arrays.asList(moduleRegistrationData));
        hashMap.put(MODULES_WITH_SUBMENU, Arrays.asList(moduleRegistrationData));
        when(uiFrameworkService.getRegisteredModules()).thenReturn(hashMap);

        Assert.assertTrue(controller.getModulesWithoutSubMenu(request).contains(moduleRegistrationData));
        Assert.assertTrue(controller.getModulesWithSubMenu(request).contains(moduleRegistrationData));
    }

    @Test
    public void testDashboardLinksAreDisplayedForAuthorizedUsers() {
        String requiredRole = "testRole";
        when(moduleRegistrationData.getRoleForAccess()).thenReturn(requiredRole);
        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(moduleRegistrationData);
        when(userService.getRoles("server.admin")).thenReturn(Arrays.asList("admin-role"));

        Map<String, Collection<ModuleRegistrationData>> hashMap = new HashMap<>();
        hashMap.put(MODULES_WITHOUT_SUBMENU, Arrays.asList(moduleRegistrationData));
        hashMap.put(MODULES_WITH_SUBMENU, Arrays.asList(moduleRegistrationData));
        when(uiFrameworkService.getRegisteredModules()).thenReturn(hashMap);

        RoleDto roleDto = new RoleDto();
        roleDto.setPermissionNames(Arrays.asList(requiredRole));

        when(roleService.getRole("admin-role")).thenReturn(roleDto);


        Assert.assertTrue(controller.getModulesWithoutSubMenu(request).contains(moduleRegistrationData));
        Assert.assertTrue(controller.getModulesWithSubMenu(request).contains(moduleRegistrationData));
    }

    @Test
    public void testDashboardLinksAreNotDisplayedForUnauthorizedUsers() {
        String requiredRole = "testRole";
        when(moduleRegistrationData.getRoleForAccess()).thenReturn(requiredRole);
        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(moduleRegistrationData);
        UserDto userDto = new UserDto();
        userDto.setRoles(Arrays.asList("admin-role"));
        when(userService.getUser("server.admin")).thenReturn(userDto);

        Map<String, Collection<ModuleRegistrationData>> hashMap = new HashMap<>();
        hashMap.put(MODULES_WITHOUT_SUBMENU, Arrays.asList(moduleRegistrationData));
        hashMap.put(MODULES_WITH_SUBMENU, Arrays.asList(moduleRegistrationData));
        when(uiFrameworkService.getRegisteredModules()).thenReturn(hashMap);

        RoleDto roleDto = new RoleDto();
        roleDto.setPermissionNames(Arrays.asList("some other role"));

        when(roleService.getRole("admin-role")).thenReturn(roleDto);

        assertFalse(controller.getModulesWithoutSubMenu(request).contains(moduleRegistrationData));
        assertFalse(controller.getModulesWithSubMenu(request).contains(moduleRegistrationData));
    }

    @Test
    public void testGetUser() {
        when(request.getUserPrincipal()).thenReturn(null);

        assertEquals(
                new UserInfo("Admin Mode", false, LANG),
                controller.getUser(request)
        );

        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(USER_NAME);

        assertEquals(
                new UserInfo(USER_NAME, true, LANG),
                controller.getUser(request)
        );
    }

}
