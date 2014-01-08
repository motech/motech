package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.ex.UserNotFoundException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ForgotControllerTest {

    private static final String EMAIL = "e@ma.il";

    @Mock
    private PasswordRecoveryService recoveryService;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private MotechSettings motechSettings;

    @InjectMocks
    private ForgotController controller = new ForgotController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(settingsFacade.getPlatformSettings()).thenReturn(motechSettings);
    }

    @Test
    public void testForgotPost() throws UserNotFoundException {
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.ENGLISH);
        when(motechSettings.getLoginMode()).thenReturn(LoginMode.REPOSITORY);

        controller.forgotPost(EMAIL);

        verify(recoveryService).passwordRecoveryRequest(EMAIL);
    }



    @Test
    public void testInvalidEmail() throws UserNotFoundException {
        doThrow(new UserNotFoundException()).when(recoveryService).passwordRecoveryRequest(EMAIL);
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.ENGLISH);
        when(motechSettings.getLoginMode()).thenReturn(LoginMode.REPOSITORY);

        assertEquals("security.forgot.noSuchUser", controller.forgotPost(EMAIL));

        verify(recoveryService).passwordRecoveryRequest(EMAIL);
    }
}
