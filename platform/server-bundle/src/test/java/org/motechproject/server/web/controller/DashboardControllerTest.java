package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.ui.LocaleSettings;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DashboardControllerTest {

    private static final String UPTIME = "uptime";
    private static final String CURRENT_MODULE = "currentModule";
    private static final String MODULE_NAME = "demo";

    @Mock
    private UIFrameworkService uiFrameworkService;

    @Mock
    private LocaleSettings localeSettings;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private ModuleRegistrationData regData;

    @Mock
    private MessageSource messageSource;

    @Mock
    Principal principal;

    @Mock
    HttpSession session;

    @Mock
    ServletContext servletContext;

    @Mock
    MotechUserService userService;

    @Mock
    MotechRoleService roleService;


    @InjectMocks
    private DashboardController controller = new DashboardController();

    @Before
    public void setUp() {
        initMocks(this);
        Locale en = new Locale("en");
        when(localeSettings.getUserLocale(httpServletRequest)).thenReturn(en);
        when(messageSource.getMessage("day", null, en)).thenReturn("day");
        when(messageSource.getMessage("days", null, en)).thenReturn("days");
        when(messageSource.getMessage("and", null, en)).thenReturn("and");
        when(messageSource.getMessage("hour", null, en)).thenReturn("hour");
        when(messageSource.getMessage("hours", null, en)).thenReturn("hours");
        when(messageSource.getMessage("minute", null, en)).thenReturn("minute");
        when(messageSource.getMessage("minutes", null, en)).thenReturn("minutes");
        when(httpServletRequest.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("admin");
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/");

    }

    @Test
    public void testDashboardNoModule() {
        ModelAndView result = controller.index(null, httpServletRequest);

        assertEquals("index", result.getViewName());
        assertNotNull(result.getModelMap().get(UPTIME));
        assertNull(result.getModelMap().get(CURRENT_MODULE));
    }

    @Test
    public void testDashboardWithModule() {
        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(regData);

        ModelAndView result = controller.index(MODULE_NAME, httpServletRequest);

        assertEquals("index", result.getViewName());
        assertNotNull(result.getModelMap().get(UPTIME));
        assertEquals(regData, result.getModelMap().get(CURRENT_MODULE));
        verify(uiFrameworkService).getModuleData(MODULE_NAME);
        verify(uiFrameworkService).moduleBackToNormal(MODULE_NAME);
    }

    @Test
    public void testDashboardDisplayLinksWhichDontRequireAuthorization(){

        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(regData);
        UserDto userDto = new UserDto();
        userDto.setRoles(Arrays.asList("some role"));
        when(userService.getUser("admin")).thenReturn(userDto);

        HashMap hashMap = new HashMap();
        hashMap.put(UIFrameworkService.MODULES_WITHOUT_SUBMENU, Arrays.asList(regData));
        when(uiFrameworkService.getRegisteredModules()).thenReturn(hashMap);

        ModelAndView result = controller.index(MODULE_NAME, httpServletRequest);

        assertTrue(((List) result.getModelMap().get(UIFrameworkService.MODULES_WITHOUT_SUBMENU)).contains(regData));
    }

    @Test
    public void testDashboardLinksAreDisplayedForAuthorizedUsers(){
        String requiredRole = "testRole";
        when(regData.getRoleForAccess()).thenReturn(requiredRole);
        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(regData);
        UserDto userDto = new UserDto();
        userDto.setRoles(Arrays.asList("admin-role"));
        when(userService.getUser("admin")).thenReturn(userDto);

        HashMap hashMap = new HashMap();
        hashMap.put(UIFrameworkService.MODULES_WITHOUT_SUBMENU, Arrays.asList(regData));
        when(uiFrameworkService.getRegisteredModules()).thenReturn(hashMap);

        RoleDto roleDto= new RoleDto();
        roleDto.setPermissionNames(Arrays.asList(requiredRole));

        when(roleService.getRole("admin-role")).thenReturn(roleDto);


        ModelAndView result = controller.index(MODULE_NAME, httpServletRequest);

        assertTrue(((List) result.getModelMap().get(UIFrameworkService.MODULES_WITHOUT_SUBMENU)).contains(regData));
    }

    @Test
    public void testDashboardLinksAreNotDisplayedForUnauthorizedUsers(){
        String requiredRole = "testRole";
        when(regData.getRoleForAccess()).thenReturn(requiredRole);
        when(uiFrameworkService.getModuleData(MODULE_NAME)).thenReturn(regData);
        UserDto userDto = new UserDto();
        userDto.setRoles(Arrays.asList("admin-role"));
        when(userService.getUser("admin")).thenReturn(userDto);

        HashMap hashMap = new HashMap();
        hashMap.put(UIFrameworkService.MODULES_WITHOUT_SUBMENU, Arrays.asList(regData));
        when(uiFrameworkService.getRegisteredModules()).thenReturn(hashMap);

        RoleDto roleDto= new RoleDto();
        roleDto.setPermissionNames(Arrays.asList("some other role"));

        when(roleService.getRole("admin-role")).thenReturn(roleDto);

        ModelAndView result = controller.index(MODULE_NAME, httpServletRequest);

        assertFalse(((List) result.getModelMap().get(UIFrameworkService.MODULES_WITHOUT_SUBMENU)).contains(regData));

    }
}
