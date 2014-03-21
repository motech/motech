package org.motechproject.server.web.controller;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.form.UserInfo;
import org.motechproject.server.web.helper.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Locale;

import static java.util.Locale.ENGLISH;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class, Header.class})
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
    private LocaleService localeService;

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

    @Mock
    private BundleContext bundleContext;

    @Before
    public void setUp() {
        initMocks(this);
        Locale en = new Locale("en");
        when(localeService.getUserLocale(request)).thenReturn(en);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("server.admin");
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(context);
        when(context.getContextPath()).thenReturn("/");

        PowerMockito.mockStatic(Header.class);
        PowerMockito.when(Header.generateHeader(any(Bundle.class))).thenReturn("");
    }

    @Test
    public void testDashboardNoModule() {
        ModelAndView result = controller.index(request);

        Assert.assertEquals("index", result.getViewName());
        Assert.assertNull(result.getModelMap().get(CURRENT_MODULE));
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
