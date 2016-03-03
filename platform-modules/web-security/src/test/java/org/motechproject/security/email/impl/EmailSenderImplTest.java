package org.motechproject.security.email.impl;

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
import java.util.Locale;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_FROM_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_MESSAGE;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_TO_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_MESSAGE_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_TEMPLATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_DAYS_TILL_EXPIRE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXPIRATION_DATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXTERNAL_ID;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LAST_PASSWORD_CHANGE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LOCALE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_MESSAGES;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_SERVER_URL;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_USERNAME;

public class EmailSenderImplTest {

    private static final int DAYS_TO_CHANGE_PASSWORD = 20;
    private static final int DAYS_FOR_REMINDER = 5;

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

    private ResourceBundleMessageSource messageSource;

    @Mock
    private MotechSettings platformSettings;

    private EmailSenderImpl emailSender;
    private MotechUser user;
    private MotechEvent event;
    private Map<String, Object> params;

    @Before
    public void setUp() {
        initMocks(this);
        prepareMessageSource();
        prepareEmailSender();
        prepareSettingService();
        prepareSettingsFacade();
        prepareUser();
        prepareEvent();
        prepareParams();
    }

    @Test
    public void shouldSendPasswordResetReminder() throws Exception {
        when(templateParser.mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params)))
                .thenReturn(EMAIL_MESSAGE);

        emailSender.sendPasswordResetReminder(params);

        verify(templateParser).mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params));
        verify(eventRelay).sendEventMessage(eq(event));
    }

    @Test
    public void shouldNotSendPasswordResetReminderIfParsingThrowsException() throws Exception {
        when(templateParser.mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params)))
                .thenThrow(new VelocityTemplateParsingException("Couldn't send password reminder email", null));

        emailSender.sendPasswordResetReminder(params);

        verify(templateParser).mergeTemplateIntoString(matches(PASSWORD_CHANGE_REMINDER_TEMPLATE), eq(params));
        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    private void prepareUser() {
        user = new MotechUser();
        user.setLastPasswordChange(DateUtil.now().minusDays(DAYS_TO_CHANGE_PASSWORD - DAYS_FOR_REMINDER));
        user.setUserName("FooUsername");
        user.setEmail("fooUser@domain.com");
        user.setLocale(Locale.ENGLISH);
    }
    
    private void prepareParams() {
        params = new HashMap<>();
        params.put(TEMPLATE_PARAM_USERNAME, user.getUserName());
        params.put(EMAIL_PARAM_TO_ADDRESS, user.getEmail());
        params.put(TEMPLATE_PARAM_EXPIRATION_DATE, user.getSafeLastPasswordChange().plusDays(DAYS_TO_CHANGE_PASSWORD));
        params.put(TEMPLATE_PARAM_LOCALE, user.getLocale());
        params.put(TEMPLATE_PARAM_LAST_PASSWORD_CHANGE, user.getSafeLastPasswordChange());
        params.put(TEMPLATE_PARAM_EXTERNAL_ID, user.getExternalId());
        params.put(TEMPLATE_PARAM_DAYS_TILL_EXPIRE, DAYS_FOR_REMINDER);
        params.put(TEMPLATE_PARAM_SERVER_URL, SERVER_URL);
        params.put(TEMPLATE_PARAM_MESSAGES, messageSource);
    }

    private void prepareEmailSender() {
        emailSender = new EmailSenderImpl();
        emailSender.setEventRelay(eventRelay);
        emailSender.setMessageSource(messageSource);
        emailSender.setSettingsFacade(settingsFacade);
        emailSender.setTemplateParser(templateParser);
    }

    private void prepareSettingService() {
        when(settingService.getNumberOfDaysToChangePassword())
                .thenReturn(DAYS_TO_CHANGE_PASSWORD);
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
        params.put(EMAIL_PARAM_SUBJECT, messageSource.getMessage(PASSWORD_CHANGE_REMINDER_MESSAGE_SUBJECT, null,
                Locale.ENGLISH));
        event = new MotechEvent("SendEMail", params);
    }

    private void prepareMessageSource() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
    }
}