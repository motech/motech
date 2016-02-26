package org.motechproject.security.event;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.service.mds.MotechUsersDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_TO_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_EVENT;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_EXPIRATION_CHECK_EVENT;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_DAYS_TILL_EXPIRE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXPIRATION_DATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXTERNAL_ID;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LAST_PASSWORD_CHANGE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LOCALE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_USERNAME;

public class PasswordExpirationCheckEventHandlerTest {

    private static final int DAYS_TO_CHANGE_PASSWORD = 20;
    private static final int DAYS_FOR_REMINDER = 5;

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

        List<MotechEvent> expectedEvent = prepareExpectedEvent(users);

        verify(allUsers).retrieveAll();
        verify(eventRelay, times(2)).sendEventMessage(eventCaptor.capture());
        verify(settingService).isPasswordResetReminderEnabled();
        verify(settingService).getNumberOfDaysForReminder();
        verify(settingService).getNumberOfDaysToChangePassword();

        assertEquals(expectedEvent, eventCaptor.getAllValues());
    }

    @Test
    public void shouldNotSentEventIfTheUserShouldNotResetPassword() throws Exception {
        List<MotechUser> users = prepareMotechUsersTwo();

        when(settingService.isPasswordResetReminderEnabled()).thenReturn(true);
        when(allUsers.retrieveAll()).thenReturn(users);

        eventHandler.handleEvent(event);

        verify(allUsers).retrieveAll();
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(settingService).isPasswordResetReminderEnabled();
        verify(settingService).getNumberOfDaysForReminder();
        verify(settingService).getNumberOfDaysToChangePassword();
    }

    @Test
    public void shouldNotSendEventIfPasswordReminderIsDisabled() throws Exception {
        List<MotechUser> users = prepareMotechUsersOne();

        when(settingService.isPasswordResetReminderEnabled()).thenReturn(false);
        when(allUsers.retrieveAll()).thenReturn(users);

        eventHandler.handleEvent(event);

        verify(allUsers, never()).retrieveAll();
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
        verify(settingService).isPasswordResetReminderEnabled();
        verify(settingService, never()).getNumberOfDaysForReminder();
        verify(settingService, never()).getNumberOfDaysToChangePassword();
    }

    private List<MotechEvent> prepareExpectedEvent(List<MotechUser> users) {
        List<MotechEvent> events = new ArrayList<>();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TEMPLATE_PARAM_USERNAME, users.get(1).getUserName());
        parameters.put(EMAIL_PARAM_TO_ADDRESS, users.get(1).getEmail());
        parameters.put(TEMPLATE_PARAM_EXPIRATION_DATE, calculateExpirationDate(users.get(1)));
        parameters.put(TEMPLATE_PARAM_LOCALE, users.get(1).getLocale());
        parameters.put(TEMPLATE_PARAM_LAST_PASSWORD_CHANGE, users.get(1).getSafeLastPasswordChange());
        parameters.put(TEMPLATE_PARAM_EXTERNAL_ID, users.get(1).getExternalId());
        parameters.put(TEMPLATE_PARAM_DAYS_TILL_EXPIRE, DAYS_FOR_REMINDER);
        events.add(new MotechEvent(PASSWORD_CHANGE_REMINDER_EVENT, parameters));

        parameters = new HashMap<>();
        parameters.put(TEMPLATE_PARAM_USERNAME, users.get(2).getUserName());
        parameters.put(EMAIL_PARAM_TO_ADDRESS, users.get(2).getEmail());
        parameters.put(TEMPLATE_PARAM_EXPIRATION_DATE, calculateExpirationDate(users.get(2)));
        parameters.put(TEMPLATE_PARAM_LOCALE, users.get(2).getLocale());
        parameters.put(TEMPLATE_PARAM_LAST_PASSWORD_CHANGE, users.get(2).getSafeLastPasswordChange());
        parameters.put(TEMPLATE_PARAM_EXTERNAL_ID, users.get(2).getExternalId());
        parameters.put(TEMPLATE_PARAM_DAYS_TILL_EXPIRE, DAYS_FOR_REMINDER);
        events.add(new MotechEvent(PASSWORD_CHANGE_REMINDER_EVENT, parameters));

        return events;
    }

    private DateTime calculateExpirationDate(MotechUser user) {
        return user.getSafeLastPasswordChange().plusDays(DAYS_TO_CHANGE_PASSWORD);
    }

    private void prepareEvent() {
        event = new MotechEvent(PASSWORD_EXPIRATION_CHECK_EVENT);
    }

    private void prepareEventHandler() {
        eventHandler = new PasswordExpirationCheckEventHandler();
        eventHandler.setAllUsers(allUsers);
        eventHandler.setEventRelay(eventRelay);
        eventHandler.setSettingsService(settingService);
    }

    private List<MotechUser> prepareMotechUsersOne() {
        List<MotechUser> users = new ArrayList<>();
        DateTime date = DateUtil.now().minusDays(14);

        MotechUser user = new MotechUser();
        user.setUserName("fooUsernameOne");
        user.setEmail("foouserone@foodomain.com");
        user.setLastPasswordChange(date);
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameTwo");
        user.setEmail("foousertwo@foodomain.com");
        user.setLastPasswordChange(date.minusDays(1));
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameThree");
        user.setEmail("foouserthree@foodomain.com");
        user.setLastPasswordChange(date.minusDays(1));
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameFour");
        user.setEmail("foouserfour@foodomain.com");
        user.setLastPasswordChange(date.minusDays(2));
        users.add(user);

        return users;
    }

    private List<MotechUser> prepareMotechUsersTwo() {
        List<MotechUser> users = new ArrayList<>();
        DateTime date = DateUtil.now().minusDays(14);

        MotechUser user = new MotechUser();
        user.setUserName("fooUsernameOne");
        user.setEmail("foouserone@foodomain.com");
        user.setLastPasswordChange(date);
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameTwo");
        user.setEmail("foousertwo@foodomain.com");
        user.setLastPasswordChange(date);
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameThree");
        user.setEmail("foouserthree@foodomain.com");
        user.setLastPasswordChange(date.minusDays(2));
        users.add(user);

        user = new MotechUser();
        user.setUserName("fooUsernameFour");
        user.setEmail("foouserfour@foodomain.com");
        user.setLastPasswordChange(date.minusDays(2));
        users.add(user);

        return users;
    }

    private void prepareSettingService() {
        when(settingService.getNumberOfDaysForReminder()).thenReturn(DAYS_FOR_REMINDER);
        when(settingService.getNumberOfDaysToChangePassword()).thenReturn(DAYS_TO_CHANGE_PASSWORD);
    }
}