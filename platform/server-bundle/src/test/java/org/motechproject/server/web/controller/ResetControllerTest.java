package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.web.dto.ChangePasswordViewData;
import org.motechproject.server.web.dto.ResetViewData;
import org.motechproject.server.web.form.ChangePasswordForm;
import org.motechproject.server.web.validator.ResetFormValidator;
import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.server.web.form.ResetForm;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResetControllerTest {

    private static final String TOKEN = "token";
    private static final String ERROR = "error";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "new_password";
    private static final String USER = "sampleUser";
    private static final String PAGE_LANG = "pageLang";

    @Mock
    private PasswordRecoveryService recoveryService;

    @Mock
    private ResetFormValidator resetFormValidator;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ResetForm form;

    @Mock
    private MotechUserService motechUserService;

    @Mock
    MotechUserProfile motechUserProfile;

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

        ResetViewData view = controller.getResetViewData(request);

        assertEquals(true, view.isInvalidToken());
        assertEquals(false, view.isResetSucceed());
        assertEquals(Locale.ENGLISH, view.getPageLang());
    }

    @Test
    public void testValidView() {
        String token = request.getParameter("token");
        when(recoveryService.validateToken(token)).thenReturn(true);

        ResetViewData view = controller.getResetViewData(request);

        assertEquals(false, view.isInvalidToken());
        assertEquals(false, view.isResetSucceed());
        assertEquals(Locale.ENGLISH, view.getPageLang());
    }

    @Test
    public void testValidationErrors() {
        when(resetFormValidator.validate(form)).thenReturn(Arrays.asList(ERROR));
        when(form.getToken()).thenReturn(TOKEN);

        ResetViewData view = controller.reset(form, request);
        List<String> errors = view.getErrors();

        assertEquals(false, view.isInvalidToken());
        assertEquals(false, view.isResetSucceed());
        assertEquals(TOKEN, view.getResetForm().getToken());
        assertEquals(1, errors.size());
        assertEquals(ERROR, errors.get(0));
        assertEquals(Locale.ENGLISH, view.getPageLang());
    }

    @Test
    public void testReset() throws InvalidTokenException {
        when(form.getToken()).thenReturn(TOKEN);
        when(form.getPassword()).thenReturn(PASSWORD);
        when(form.getPasswordConfirmation()).thenReturn(PASSWORD);

        ResetViewData view = controller.reset(form, request);

        assertEquals(false, view.isInvalidToken());
        assertEquals(true, view.isResetSucceed());
        assertEquals(Locale.ENGLISH, view.getPageLang());
        verify(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void testResetInvalidToken() throws InvalidTokenException {
        when(form.getToken()).thenReturn(TOKEN);
        when(form.getPassword()).thenReturn(PASSWORD);
        when(form.getPasswordConfirmation()).thenReturn(PASSWORD);
        doThrow(new InvalidTokenException()).when(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);

        ResetViewData view = controller.reset(form, request);

        List<String> errors = view.getErrors();
        assertEquals(1, errors.size());
        assertEquals("server.reset.invalidToken", errors.get(0));

        assertEquals(true, view.isResetSucceed());
        assertEquals(Locale.ENGLISH, view.getPageLang());
        verify(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void testShouldNotChangePasswordWhenPasswordIsWrong() {
        ChangePasswordForm passwordForm = buildPasswordForm(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenReturn(null);

        ChangePasswordViewData viewData = controller.changePassword(passwordForm);

        List<String> errors = viewData.getErrors();
        assertEquals(1, errors.size());
        assertEquals("server.reset.wrongPassword", errors.get(0));

        assertEquals(false, viewData.isChangeSucceded());
        assertEquals(false, viewData.isUserBlocked());
        verify(motechUserService).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void testShouldNotChangePasswordWhenConfirmationIsWrong() {
        ChangePasswordForm passwordForm = buildPasswordForm(PASSWORD, NEW_PASSWORD, PASSWORD);
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenReturn(motechUserProfile);

        ChangePasswordViewData viewData = controller.changePassword(passwordForm);

        List<String> errors = viewData.getErrors();
        assertEquals(1, errors.size());
        assertEquals("server.error.invalid.password", errors.get(0));

        assertEquals(false, viewData.isChangeSucceded());
        assertEquals(false, viewData.isUserBlocked());
        verify(motechUserService, never()).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void testShouldChangePassword() {
        ChangePasswordForm passwordForm = buildPasswordForm(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenReturn(motechUserProfile);

        ChangePasswordViewData viewData = controller.changePassword(passwordForm);

        List<String> errors = viewData.getErrors();
        assertEquals(0, errors.size());

        assertEquals(true, viewData.isChangeSucceded());
        assertEquals(false, viewData.isUserBlocked());
        verify(motechUserService).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void shouldSetUserBlockedFlag() {
        ChangePasswordForm passwordForm = buildPasswordForm(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenThrow(new LockedException("User has been blocked!"));

        ChangePasswordViewData viewData = controller.changePassword(passwordForm);

        List<String> errors = viewData.getErrors();
        assertEquals(0, errors.size());

        assertEquals(false, viewData.isChangeSucceded());
        assertEquals(true, viewData.isUserBlocked());
        verify(motechUserService).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    private ChangePasswordForm buildPasswordForm(String old, String newPassword, String confirm) {
        ChangePasswordForm passwordForm = new ChangePasswordForm();
        passwordForm.setOldPassword(old);
        passwordForm.setPassword(newPassword);
        passwordForm.setPasswordConfirmation(confirm);
        passwordForm.setUsername(USER);

        return passwordForm;
    }
}
