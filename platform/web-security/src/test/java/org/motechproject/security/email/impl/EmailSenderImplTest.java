package org.motechproject.security.email.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.ex.VelocityTemplateParsingException;
import org.motechproject.security.velocity.VelocityTemplateParser;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_FROM_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_MESSAGE;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_TO_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_TEMPLATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_DAYS_TILL_EXPIRE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXPIRATION_DATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LAST_PASSWORD_CHANGE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LINK;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LOCALE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_MESSAGES;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_USERNAME;

public class EmailSenderImplTest {

    private static final String SERVER_URL = "server.url";
    private static final String EMAIL_MESSAGE = "Some e-mail message";
    private static final String SENDER_ADDRESS = "noreply@server.url";

    @Mock
    private EventRelay eventRelay;

    @Mock
    private VelocityTemplateParser templateParser;

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private SettingService settingService;

    @Mock
    private ResourceBundleMessageSource messageSource;

    @Mock
    private MotechSettings platformSettings;

    private EmailSenderImpl emailSender;
    private MotechUser user;
    private MotechEvent event;
    private DateTime expirationDate;
    private Map<String, Object> params;

    @Before
    public void setUp() {
        initMocks(this);
        prepareEmailSender();
        prepareSettingService();
        prepareSettingsFacade();
        prepareUser();
        prepareEvent();
        expirationDate = DateUtil.now().plus(5);
        prepareParams();
    }

    @Test
    public void shouldSendPasswordResetReminder() throws Exception {
        when(templateParser.mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params)))
                .thenReturn(EMAIL_MESSAGE);

        emailSender.sendPasswordResetReminder(user, expirationDate);

        verify(templateParser, times(1))
                .mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params));
        verify(eventRelay, times(1)).sendEventMessage(eq(event));
    }

    @Test
    public void shouldNotSendPasswordResetReminderIfParsingThrowsException() throws Exception {
        when(templateParser.mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params)))
                .thenThrow(new VelocityTemplateParsingException("Couldn't send password reminder email", null));

        emailSender.sendPasswordResetReminder(user, expirationDate);

        verify(templateParser, times(1))
                .mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params));
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    private void prepareUser() {
        user = new MotechUser();
        user.setLastPasswordChange(DateUtil.now().minusDays(15));
        user.setUserName("FooUsername");
        user.setEmail("fooUser@domain.com");
    }
    
    private void prepareParams() {
        params = new HashMap<>();
        params.put(TEMPLATE_PARAM_USERNAME, user.getUserName());
        params.put(TEMPLATE_PARAM_MESSAGES, messageSource);
        params.put(TEMPLATE_PARAM_LOCALE, user.getLocale());
        params.put(TEMPLATE_PARAM_EXPIRATION_DATE, expirationDate);
        params.put(TEMPLATE_PARAM_LAST_PASSWORD_CHANGE, user.getLastPasswordChange());
        params.put(TEMPLATE_PARAM_DAYS_TILL_EXPIRE, settingService.getNumberOfDaysForReminder());
        params.put(TEMPLATE_PARAM_LINK, SERVER_URL);
    }

    private void prepareEmailSender() {
        emailSender = new EmailSenderImpl();
        emailSender.setEventRelay(eventRelay);
        emailSender.setMessageSource(messageSource);
        emailSender.setSettingService(settingService);
        emailSender.setSettingsFacade(settingsFacade);
        emailSender.setTemplateParser(templateParser);
    }

    private void prepareSettingService() {
        when(settingService.getNumberOfDaysToChangePassword()).thenReturn(20);
    }

    private void prepareSettingsFacade() {
        when(settingsFacade.getPlatformSettings()).thenReturn(platformSettings);
        when(platformSettings.getServerUrl()).thenReturn(SERVER_URL);
        when(platformSettings.getServerHost()).thenReturn(SERVER_URL);
    }

    private void prepareEvent() {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_PARAM_FROM_ADDRESS, SENDER_ADDRESS);
        params.put(EMAIL_PARAM_TO_ADDRESS, user.getEmail());
        params.put(EMAIL_PARAM_MESSAGE, EMAIL_MESSAGE);
        params.put(EMAIL_PARAM_SUBJECT, PASSWORD_CHANGE_REMINDER_SUBJECT);
        event = new MotechEvent("SendEMail", params);
    }
}