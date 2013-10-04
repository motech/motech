package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.startup.StartupManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class})
public class LoginControllerTest {

    private String BOOTSTRAP_VIEW_NAME = "redirect:bootstrap.do";

    @Mock
    StartupManager startupManager;

    @InjectMocks
    LoginController loginController = new LoginController();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StartupManager.class);

        initMocks(this);
    }

    @Test
    public void shouldRedirectToBootstrapUIIfBootstrapConfigIsNotAvailable() {
        when(startupManager.isBootstrapConfigRequired()).thenReturn(true);

        ModelAndView view = loginController.login(null);

        assertThat(view.getViewName(), is(equalTo(BOOTSTRAP_VIEW_NAME)));
    }
}
