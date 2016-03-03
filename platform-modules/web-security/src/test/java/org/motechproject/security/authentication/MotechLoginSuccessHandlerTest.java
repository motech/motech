package org.motechproject.security.authentication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.helper.SessionHandler;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MotechLoginSuccessHandlerTest {

    @InjectMocks
    private MotechLoginSuccessHandler motechLoginSuccessHandler = new MotechLoginSuccessHandler();

    @Mock
    AllMotechUsers allMotechUsers;

    @Mock
    private SettingService settingService;

    @Mock
    private SessionHandler sessionHandler;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    ArgumentCaptor<MotechUser> userCaptor = ArgumentCaptor.forClass(MotechUser.class);

    @Test
    public void shouldSetSessionTimeoutAndStore() throws ServletException, IOException {
        MotechUser user = new MotechUser();
        user.setUserName("testUser");
        user.setFailureLoginCounter(3);

        when(authentication.getName()).thenReturn("testUser");
        when(allMotechUsers.findByUserName("testUser")).thenReturn(user);
        when(request.getSession()).thenReturn(session);
        when(settingService.getSessionTimeout()).thenReturn(500);

        motechLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setMaxInactiveInterval(500);
        verify(sessionHandler).addSession(session);
    }

    @Test
    public void shouldResetFailureLoginCounter() throws ServletException, IOException {
        MotechUser user = new MotechUser();
        user.setUserName("testUser");
        user.setFailureLoginCounter(3);

        when(authentication.getName()).thenReturn("testUser");
        when(allMotechUsers.findByUserName("testUser")).thenReturn(user);
        when(request.getSession()).thenReturn(session);
        when(settingService.getSessionTimeout()).thenReturn(500);

        motechLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(allMotechUsers).update(userCaptor.capture());
        assertEquals((Integer)0, userCaptor.getValue().getFailureLoginCounter());
    }
}
