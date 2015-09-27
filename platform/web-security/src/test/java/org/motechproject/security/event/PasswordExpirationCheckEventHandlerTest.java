package org.motechproject.security.event;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.MotechUsersDataService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.constants.EventSubjects.PASSWORD_EXPIRATION_CHECK;
import static org.motechproject.security.constants.EventSubjects.PASSWORD_RESET_REMINDER;

public class PasswordExpirationCheckEventHandlerTest {

    @Mock
    private SettingService settingService;

    @Mock
    private MotechUsersDataService allUsers;

    @Mock
    private EventRelay eventRelay;

    @Captor
    private ArgumentCaptor<MotechEvent> eventCaptor;

    private PasswordExpirationCheckEventHandler eventHandler;

    private MotechEvent event;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        prepareEventHandler();
        prepareEvent();
        prepareSettingService();
    }

    @Test
    public void shouldSendEventIfTheUserShouldResetPassword() throws Exception {
        List<MotechUser> users = prepareMotechUsersOne();

        when(settingService.isPasswordResetReminderEnabled()).thenReturn(true);
        when(allUsers.retrieveAll()).thenReturn(users);

        eventHandler.handleEvent(event);

        MotechEvent expectedEvent = prepareExpectedEvent(users.get(1));

        verify(allUsers, times(1)).retrieveAll();
        verify(eventRelay, times(1)).sendEventMessage(eventCaptor.capture());
        verify(settingService, times(1)).isPasswordResetReminderEnabled();
        verify(settingService, times(1)).getNumberOfDaysForReminder();
        verify(settingService, times(1)).getNumberOfDaysToChangePassword();

        assertEquals(expectedEvent, eventCaptor.getValue());
    }

    @Test
    public void shouldNotSentEventIfTheUserShouldNotResetPassword() throws Exception {
        List<MotechUser> users = prepareMotechUsersOne();

        when(settingService.isPasswordResetReminderEnabled()).thenReturn(false);
        when(allUsers.retrieveAll()).thenReturn(users);

        eventHandler.handleEvent(event);

        verify(allUsers, times(0)).retrieveAll();
        verify(eventRelay, times(0)).sendEventMessage(any(MotechEvent.class));
        verify(settingService, times(1)).isPasswordResetReminderEnabled();
        verify(settingService, times(0)).getNumberOfDaysForReminder();
        verify(settingService, times(0)).getNumberOfDaysToChangePassword();
    }

    @Test
    public void shouldNotSendEventIfPasswordReminderIsDisabled() throws Exception {
        List<MotechUser> users = prepareMotechUsersOne();

        when(settingService.isPasswordResetReminderEnabled()).thenReturn(false);
        when(allUsers.retrieveAll()).thenReturn(users);

        eventHandler.handleEvent(event);

        verify(allUsers, times(0)).retrieveAll();
        verify(eventRelay, times(0)).sendEventMessage(any(MotechEvent.class));
        verify(settingService, times(1)).isPasswordResetReminderEnabled();
        verify(settingService, times(0)).getNumberOfDaysForReminder();
        verify(settingService, times(0)).getNumberOfDaysToChangePassword();
    }

    private MotechEvent prepareExpectedEvent(MotechUser motechUser) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", motechUser.getUserName());
        parameters.put("email", motechUser.getEmail());
        return new MotechEvent(PASSWORD_RESET_REMINDER, parameters);
    }

    private void prepareEvent() {
        event = new MotechEvent(PASSWORD_EXPIRATION_CHECK);
    }

    private void prepareEventHandler() {
        eventHandler = new PasswordExpirationCheckEventHandler();
        eventHandler.setAllUsers(allUsers);
        eventHandler.setEventRelay(eventRelay);
        eventHandler.setSettingsService(settingService);
    }

    private List<MotechUser> prepareMotechUsersOne() {
        List<MotechUser> users = new ArrayList<>();
        Calendar date = Calendar.getInstance();

        MotechUser user = new MotechUser();
        user.setUserName("fooUsernameOne");
        user.setEmail("foouserone@foodomain.com");
        date.add(Calendar.DAY_OF_YEAR, -14);
        user.setLastPasswordChange(new DateTime(date.getTime()));
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameTwo");
        user.setEmail("foousertwo@foodomain.com");
        date.add(Calendar.DAY_OF_YEAR, -1);
        user.setLastPasswordChange(new DateTime(date.getTime()));
        users.add(user);

        return users;
    }

    private List<MotechUser> prepareMotechUsersOTwo() {
        List<MotechUser> users = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_YEAR, -14);

        MotechUser user = new MotechUser();
        user.setUserName("fooUsernameOne");
        user.setEmail("foouserone@foodomain.com");
        user.setLastPasswordChange(new DateTime(date.getTime()));
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameTwo");
        user.setEmail("foousertwo@foodomain.com");
        user.setLastPasswordChange(new DateTime(date.getTime()));
        users.add(user);

        return users;
    }

    private void prepareSettingService() {
        when(settingService.getNumberOfDaysForReminder()).thenReturn(5);
        when(settingService.getNumberOfDaysToChangePassword()).thenReturn(20);
    }
}