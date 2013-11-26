package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class})
public class LoginControllerTest {

    private String BOOTSTRAP_VIEW_NAME = "redirect:bootstrap.do";

    private MockMvc controller;

    @Mock
    private LocaleService localeService;
    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private StartupManager startupManager;

    @Mock
    private MotechSettings motechSettings;

    @InjectMocks
    LoginController loginController = new LoginController();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StartupManager.class);

        initMocks(this);

        controller = MockMvcBuilders.standaloneSetup(loginController).build();

    }

    @Test
    public void shouldRedirectToBootstrapUIIfBootstrapConfigIsNotAvailable() {
        when(startupManager.isBootstrapConfigRequired()).thenReturn(true);

        ModelAndView view = loginController.login(null);

        assertThat(view.getViewName(), is(equalTo(BOOTSTRAP_VIEW_NAME)));
    }

    @Test
    public void testLogin() throws Exception {

        when(motechSettings.getLoginMode()).thenReturn(LoginMode.REPOSITORY);
        when(settingsFacade.getPlatformSettings()).thenReturn(motechSettings);

        ModelAndView actualModelAndView = controller.perform(
                get("/login")
        ).andReturn().getModelAndView();

        assertEquals("loginPage", actualModelAndView.getViewName());

        assertEquals(LoginMode.REPOSITORY, actualModelAndView.getModelMap().get("loginMode"));
    }
}
