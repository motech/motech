package org.motechproject.security.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.security.web.form.ResetForm;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResetControllerTest {

    private static final String TOKEN = "token";
    private static final String ERROR = "error";
    private static final String PASSWORD = "password";
    private static final String PAGE_LANG = "pageLang";

    @Mock
    private PasswordRecoveryService recoveryService;

    @Mock
    private ObjectError objectError;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ResetForm form;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ResetController controller = new ResetController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void testInvalidTokenOnView() {
        when(recoveryService.validateToken(TOKEN)).thenReturn(false);

        ModelAndView mav = controller.resetView(TOKEN, request);

        assertEquals("invalidReset", mav.getViewName());
        assertEquals(Locale.ENGLISH, mav.getModel().get(PAGE_LANG));
    }

    @Test
    public void testValidView() {
        when(recoveryService.validateToken(TOKEN)).thenReturn(true);

        ModelAndView mav = controller.resetView(TOKEN, request);

        assertEquals("reset", mav.getViewName());
        assertEquals(TOKEN, mav.getModel().get("token"));
        assertEquals(Locale.ENGLISH, mav.getModel().get(PAGE_LANG));
    }

    @Test
    public void testBindingErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(objectError));
        when(objectError.getCode()).thenReturn(ERROR);
        when(form.getToken()).thenReturn(TOKEN);

        ModelAndView mav = controller.reset(form, bindingResult, request);

        List<String> errors = (List<String>) mav.getModel().get("errors");

        assertEquals("reset", mav.getViewName());
        assertEquals(TOKEN, mav.getModel().get("token"));
        assertEquals(1, errors.size());
        assertEquals(ERROR, errors.get(0));
        assertEquals(Locale.ENGLISH, mav.getModel().get(PAGE_LANG));
    }

    @Test
    public void testReset() throws InvalidTokenException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(form.getToken()).thenReturn(TOKEN);
        when(form.getPassword()).thenReturn(PASSWORD);
        when(form.getPasswordConfirmation()).thenReturn(PASSWORD);

        ModelAndView mav = controller.reset(form, bindingResult, request);

        assertEquals("afterReset", mav.getViewName());
        assertEquals(Locale.ENGLISH, mav.getModel().get(PAGE_LANG));
        verify(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void testResetInvalidToken() throws InvalidTokenException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(form.getToken()).thenReturn(TOKEN);
        when(form.getPassword()).thenReturn(PASSWORD);
        when(form.getPasswordConfirmation()).thenReturn(PASSWORD);
        doThrow(new InvalidTokenException()).when(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);

        ModelAndView mav = controller.reset(form, bindingResult, request);

        List<String> errors = (List<String>) mav.getModel().get("errors");
        assertEquals(1, errors.size());
        assertEquals("security.invalidToken", errors.get(0));

        assertEquals("afterReset", mav.getViewName());
        assertNull(mav.getModel().get("token"));
        assertEquals(Locale.ENGLISH, mav.getModel().get(PAGE_LANG));
        verify(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);
    }
}
