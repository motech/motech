package org.motechproject.server.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.exception.InvalidTokenException;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.server.web.dto.ChangePasswordViewData;
import org.motechproject.server.web.dto.ResetViewData;
import org.motechproject.server.web.form.ChangePasswordForm;
import org.motechproject.server.web.form.ResetForm;
import org.motechproject.server.web.validator.ResetFormValidator;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
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
    private MotechUserService motechUserService;

    @Mock
    MotechUserProfile motechUserProfile;

    @InjectMocks
    private ResetController resetController = new ResetController();

    private MockMvc controller;

    @Before
    public void setUp() {
        controller = MockMvcBuilders.standaloneSetup(resetController).build();
        when(cookieLocaleResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
    }

    @Test
    public void testInvalidTokenOnView() throws Exception {
        ResetViewData expected = getResetViewData(true, false, null, new ResetForm());

        when(recoveryService.validateToken(TOKEN)).thenReturn(false);
        controller.perform(get("/forgotresetviewdata")
                .locale(Locale.ENGLISH))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void testValidView() throws Exception {
        ResetViewData expected = getResetViewData(false, false, null, getResetForm(TOKEN, null, null));

        when(recoveryService.validateToken(TOKEN)).thenReturn(true);

        controller.perform(get("/forgotresetviewdata")
                .locale(Locale.ENGLISH)
                .param(TOKEN, TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void testValidationErrors() throws Exception {
        ResetViewData expected = getResetViewData(false, false, asList(ERROR), getResetForm(TOKEN, null, null));

        when(resetFormValidator.validate(any(ResetForm.class))).thenReturn(asList(ERROR));

        controller.perform(post("/forgotreset")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getResetForm(TOKEN, null, null)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void testReset() throws Exception {
        ResetViewData expected = getResetViewData(false, true, new ArrayList<String>(), getResetForm(TOKEN, PASSWORD, PASSWORD));

        controller.perform(post("/forgotreset")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getResetForm(TOKEN, PASSWORD, PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void testResetInvalidToken() throws Exception {
        ResetViewData expected = getResetViewData(true, true, asList("server.reset.invalidToken"), getResetForm(TOKEN, PASSWORD, PASSWORD));

        doThrow(new InvalidTokenException()).when(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);

        controller.perform(post("/forgotreset")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getResetForm(TOKEN, PASSWORD, PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(recoveryService).resetPassword(TOKEN, PASSWORD, PASSWORD);
    }

    @Test
    public void testShouldNotChangePasswordWhenPasswordIsWrong() throws Exception {
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenReturn(null);

        controller.perform(post("/changepassword")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getPasswordForm(USER, PASSWORD, NEW_PASSWORD, NEW_PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(getChangePasswordViewData(false, false,
                        asList("server.reset.wrongPassword"), getPasswordForm(EMPTY, EMPTY, EMPTY, EMPTY)))));

        verify(motechUserService).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void testShouldNotChangePasswordWhenConfirmationIsWrong() throws Exception {
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenReturn(motechUserProfile);

        controller.perform(post("/changepassword")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getPasswordForm(USER, PASSWORD, NEW_PASSWORD, PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(getChangePasswordViewData(false, false,
                        asList("server.error.invalid.password"), getPasswordForm(EMPTY, EMPTY, EMPTY, EMPTY)))));

        verify(motechUserService, never()).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void testShouldChangePassword() throws Exception {
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenReturn(motechUserProfile);

        controller.perform(post("/changepassword")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getPasswordForm(USER, PASSWORD, NEW_PASSWORD, NEW_PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(getChangePasswordViewData(true, false,
                        new ArrayList<String>(), getPasswordForm(EMPTY, EMPTY, EMPTY, EMPTY)))));

        verify(motechUserService).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    @Test
    public void shouldSetUserBlockedFlag()  throws Exception {
        when(motechUserService.changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD)).thenThrow(new LockedException("User has been blocked!"));

        controller.perform(post("/changepassword")
                .locale(Locale.ENGLISH)
                .body(new ObjectMapper().writeValueAsBytes(getPasswordForm(USER, PASSWORD, NEW_PASSWORD, NEW_PASSWORD)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(getChangePasswordViewData(false, true,
                        new ArrayList<String>(), getPasswordForm(EMPTY, EMPTY, EMPTY, EMPTY)))));

        verify(motechUserService).changeExpiredPassword(USER, PASSWORD, NEW_PASSWORD);
    }

    private ChangePasswordViewData getChangePasswordViewData(boolean changeSucceded, boolean userBlocked, List<String> errors,
                                                             ChangePasswordForm changePasswordForm) {
        ChangePasswordViewData changePasswordViewData = new ChangePasswordViewData(changePasswordForm);
        changePasswordViewData.setChangeSucceded(changeSucceded);
        changePasswordViewData.setUserBlocked(userBlocked);
        changePasswordViewData.setErrors(errors);
        return changePasswordViewData;
    }

    private ChangePasswordForm getPasswordForm(String username, String old, String newPassword, String confirm) {
        ChangePasswordForm passwordForm = new ChangePasswordForm();
        passwordForm.setOldPassword(old);
        passwordForm.setPassword(newPassword);
        passwordForm.setPasswordConfirmation(confirm);
        passwordForm.setUsername(username);

        return passwordForm;
    }

    private ResetViewData getResetViewData(boolean invalidToken, boolean resetSuceed, List<String> errors, ResetForm resetForm) {
        ResetViewData resetViewData = new ResetViewData();
        resetViewData.setInvalidToken(invalidToken);
        resetViewData.setResetSucceed(resetSuceed);
        resetViewData.setPageLang(Locale.ENGLISH);
        resetViewData.setErrors(errors);
        resetViewData.setResetForm(resetForm);
        return resetViewData;
    }

    private ResetForm getResetForm(String token, String password, String passwordConfirmation) {
        ResetForm resetForm = new ResetForm();
        resetForm.setToken(token);
        resetForm.setPassword(password);
        resetForm.setPasswordConfirmation(passwordConfirmation);
        return resetForm;
    }
}
