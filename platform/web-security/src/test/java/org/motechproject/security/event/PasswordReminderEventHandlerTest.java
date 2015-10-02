package org.motechproject.security.event;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.repository.MotechUsersDataService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.constants.EventSubjects.PASSWORD_CHANGE_REMINDER;

public class PasswordReminderEventHandlerTest {

    @Mock
    private EmailSender emailSender;

    @Mock
    private MotechUsersDataService usersDataService;

    private PasswordReminderEventHandler eventHandler;
    private MotechEvent event;
    private MotechUser user;

    @Before
    public void setUp() {
        initMocks(this);
        prepareUser();
        prepareEvent();
        eventHandler = new PasswordReminderEventHandler(emailSender, usersDataService);
    }

    @Test
    public void shouldSendEmailForHandledEvent() {
        when(usersDataService.findByUserName((String) event.getParameters().get("username"))).thenReturn(user);

        eventHandler.handleEvent(event);

        verify(usersDataService, times(1)).findByUserName((String) event.getParameters().get("username"));
        verify(emailSender, times(1))
                .sendPasswordResetReminder(user, (DateTime) event.getParameters().get("expirationDate"));
    }

    private void prepareUser() {
        user = new MotechUser();
        user.setLastPasswordChange(DateUtil.now().minusDays(15));
        user.setUserName("FooUsername");
        user.setEmail("fooUser@domain.com");
    }

    private void prepareEvent() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUserName());
        params.put("expirationDate", user.getLastPasswordChange().plusDays(20));
        event = new MotechEvent(PASSWORD_CHANGE_REMINDER, params);
    }
}