package org.motechproject.security.authentication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotechLoginErrorHandlerTest {

    private static final String LOGIN_ERROR = "/module/server/login?error=true";
    private static final String LOGIN_BLOCKED = "/module/server/login?blocked=true";
    private static final String CHANGE_PASSWORD = "/module/server/changepassword";

    @Mock
    AllMotechUsers allMotechUsers;

    @Mock
    Authentication authentication;

    @Mock
    private SettingService settingService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    MotechLoginErrorHandler motechLoginErrorHandler = new MotechLoginErrorHandler(LOGIN_ERROR, LOGIN_BLOCKED, CHANGE_PASSWORD);

    ArgumentCaptor<MotechUser> userCaptor = ArgumentCaptor.forClass(MotechUser.class);

    @Before
    public void setUp() {
        initMocks(this);
        when(request.getContextPath()).thenReturn("");
        when(response.encodeRedirectURL(LOGIN_ERROR)).thenReturn(LOGIN_ERROR);
        when(response.encodeRedirectURL(LOGIN_BLOCKED)).thenReturn(LOGIN_BLOCKED);
        when(response.encodeRedirectURL(CHANGE_PASSWORD)).thenReturn(CHANGE_PASSWORD);
    }

    @Test
    public void shouldNotBlockUser() throws ServletException, IOException {
        AuthenticationException exception = new BadCredentialsException("Wrong Password");
        exception.setAuthentication(authentication);
        MotechUser user = createUser(UserStatus.ACTIVE, 2);

        when(authentication.getName()).thenReturn("testUser");
        when(allMotechUsers.findByUserName("testUser")).thenReturn(user);
        when(settingService.getFailureLoginLimit()).thenReturn(3);

        motechLoginErrorHandler.onAuthenticationFailure(request, response, exception);

        verify(response).sendRedirect(LOGIN_ERROR);
        verify(allMotechUsers).update(userCaptor.capture());

        MotechUser capturedUser = userCaptor.getValue();
        assertEquals((Integer)3, capturedUser.getFailureLoginCounter());
        assertEquals(UserStatus.ACTIVE, capturedUser.getUserStatus());
    }

    @Test
    public void shouldBlockUser() throws ServletException, IOException {
        AuthenticationException exception = new BadCredentialsException("Wrong Password");
        exception.setAuthentication(authentication);
        MotechUser user = createUser(UserStatus.ACTIVE, 3);

        when(authentication.getName()).thenReturn("testUser");
        when(allMotechUsers.findByUserName("testUser")).thenReturn(user);
        when(settingService.getFailureLoginLimit()).thenReturn(3);

        motechLoginErrorHandler.onAuthenticationFailure(request, response, exception);

        verify(response).sendRedirect(LOGIN_BLOCKED);
        verify(allMotechUsers).update(userCaptor.capture());

        MotechUser capturedUser = userCaptor.getValue();
        assertEquals((Integer)0, capturedUser.getFailureLoginCounter());
        assertEquals(UserStatus.BLOCKED, capturedUser.getUserStatus());
    }

    @Test
    public void shouldRedirectUserWithExpiredPassword() throws ServletException, IOException {
        AuthenticationException exception = new CredentialsExpiredException("Credentials expired");
        exception.setAuthentication(authentication);
        MotechUser user = createUser(UserStatus.MUST_CHANGE_PASSWORD, 0);

        when(authentication.getName()).thenReturn("testUser");
        when(allMotechUsers.findByUserName("testUser")).thenReturn(user);
        when(settingService.getFailureLoginLimit()).thenReturn(3);

        motechLoginErrorHandler.onAuthenticationFailure(request, response, exception);

        verify(response).sendRedirect(CHANGE_PASSWORD);
    }

    private MotechUser createUser(UserStatus userStatus, int failureLoginCounter) {
        MotechUser user = new MotechUser();
        user.setUserName("testUser");
        user.setFailureLoginCounter(failureLoginCounter);
        user.setUserStatus(userStatus);
        return user;
    }

}
