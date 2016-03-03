package org.motechproject.security.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.email.EmailSender;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_TO_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_EVENT;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_DAYS_TILL_EXPIRE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXPIRATION_DATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXTERNAL_ID;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LAST_PASSWORD_CHANGE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LOCALE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_USERNAME;

public class PasswordReminderEventHandlerTest {

    private static final int DAYS_TO_CHANGE_PASSWORD = 20;
    private static final int DAYS_FOR_REMINDER = 5;

    @Mock
    private EmailSender emailSender;

    private PasswordReminderEventHandler eventHandler;
    private MotechEvent event;
    private MotechUser user;

    @Before
    public void setUp() {
        initMocks(this);
        prepareUser();
        prepareEvent();
        eventHandler = new PasswordReminderEventHandler(emailSender);
    }

    @Test
    public void shouldSendEmailForHandledEvent() {

        eventHandler.handleEvent(event);

        verify(emailSender).sendPasswordResetReminder(event.getParameters());
    }

    private void prepareUser() {
        user = new MotechUser();
        user.setLastPasswordChange(DateUtil.now().minusDays(DAYS_TO_CHANGE_PASSWORD - DAYS_FOR_REMINDER));
        user.setUserName("FooUsername");
        user.setEmail("fooUser@domain.com");
    }

    private void prepareEvent() {
        Map<String, Object> params = new HashMap<>();
        params.put(TEMPLATE_PARAM_USERNAME, user.getUserName());
        params.put(EMAIL_PARAM_TO_ADDRESS, user.getEmail());
        params.put(TEMPLATE_PARAM_EXPIRATION_DATE, user.getSafeLastPasswordChange().plusDays(DAYS_TO_CHANGE_PASSWORD));
        params.put(TEMPLATE_PARAM_LOCALE, user.getLocale());
        params.put(TEMPLATE_PARAM_LAST_PASSWORD_CHANGE, user.getSafeLastPasswordChange());
        params.put(TEMPLATE_PARAM_EXTERNAL_ID, user.getExternalId());
        params.put(TEMPLATE_PARAM_DAYS_TILL_EXPIRE, DAYS_FOR_REMINDER);
        event = new MotechEvent(PASSWORD_CHANGE_REMINDER_EVENT, params);
    }
}