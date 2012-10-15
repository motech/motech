package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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

    @InjectMocks
    private DashboardController controller = new DashboardController();

    @Before
    public void setUp() {
        initMocks(this);
        when(localeSettings.getUserLocale(httpServletRequest)).thenReturn(new Locale("en"));
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

        ModelAndView result = controller.index("demo", httpServletRequest);

        assertEquals("index", result.getViewName());
        assertNotNull(result.getModelMap().get(UPTIME));
        assertEquals(regData, result.getModelMap().get(CURRENT_MODULE));
        verify(uiFrameworkService).getModuleData(MODULE_NAME);
    }
}
