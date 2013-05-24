package org.motechproject.security.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.ex.UserNotFoundException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ForgotPasswordControllerTest {

    private static final String EMAIL = "e@ma.il";

    @Mock
    private PasswordRecoveryService recoveryService;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ForgotPasswordController controller = new ForgotPasswordController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testForgotPost() throws UserNotFoundException {
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.ENGLISH);

        ModelAndView mav = controller.forgotPost(EMAIL, request);

        assertEquals("forgotProcessed", mav.getViewName());
        assertEquals(Locale.ENGLISH, mav.getModel().get("pageLang"));
        verify(recoveryService).passwordRecoveryRequest(EMAIL);
    }

    @Test
    public void testInvalidEmail() throws UserNotFoundException {
        doThrow(new UserNotFoundException()).when(recoveryService).passwordRecoveryRequest(EMAIL);
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.ENGLISH);

        ModelAndView mav = controller.forgotPost(EMAIL, request);

        assertEquals("forgotProcessed", mav.getViewName());
        assertEquals(Locale.ENGLISH, mav.getModel().get("pageLang"));
        assertEquals("security.forgot.noSuchUser", mav.getModel().get("error"));
        verify(recoveryService).passwordRecoveryRequest(EMAIL);
    }
}
