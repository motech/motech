package org.motechproject.security.email.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.exception.ServerUrlIsEmptyException;
import org.motechproject.security.exception.VelocityTemplateParsingException;
import org.motechproject.security.velocity.VelocityTemplateParser;
import org.motechproject.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_FROM_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_MESSAGE;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_TO_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.LOGIN_INFORMATION_MESSAGE_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.LOGIN_INFORMATION_TEMPLATE;
import static org.motechproject.security.constants.EmailConstants.ONE_TIME_TOKEN_MESSAGE_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.ONE_TIME_TOKEN_PATH;
import static org.motechproject.security.constants.EmailConstants.ONE_TIME_TOKEN_TEMPLATE;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_MESSAGE_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_TEMPLATE;
import static org.motechproject.security.constants.EmailConstants.RECOVERY_MESSAGE_SUBJECT;
import static org.motechproject.security.constants.EmailConstants.RESET_MAIL_TEMPLATE;
import static org.motechproject.security.constants.EmailConstants.RESET_PATH;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LINK;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LOCALE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_MESSAGES;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_SERVER_URL;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_USERNAME;

/**
 * Implementation of the {@link org.motechproject.security.email.EmailSender} interface. Class
 * provides API for sending e-mails
 */
@Service
public class EmailSenderImpl implements EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderImpl.class);

    private EventRelay eventRelay;
    private VelocityTemplateParser templateParser;
    private SettingsFacade settingsFacade;
    private ResourceBundleMessageSource messageSource;

    @Override
    public void sendRecoveryEmail(final PasswordRecovery recovery) {

        LOGGER.info("Sending recovery e-mail");

        try {

            String text = templateParser.mergeTemplateIntoString(RESET_MAIL_TEMPLATE,
                    passRecoveryTemplateParams(recovery));

            String subject = messageSource.getMessage(RECOVERY_MESSAGE_SUBJECT, null, recovery.getLocale());

            sendEmail(recovery.getEmail(), text, subject);

        } catch (VelocityTemplateParsingException e) {
            LOGGER.error("Couldn't send recovery e-mail", e);
        }
    }

    @Override
    public void sendOneTimeToken(final PasswordRecovery recovery) {

        LOGGER.info("Sending one time token");

        try {

            String text = templateParser.mergeTemplateIntoString(ONE_TIME_TOKEN_TEMPLATE,
                    passRecoveryTemplateParams(recovery));

            String subject = messageSource.getMessage(ONE_TIME_TOKEN_MESSAGE_SUBJECT, null, recovery.getLocale());

            sendEmail(recovery.getEmail(), text, subject);

        } catch (VelocityTemplateParsingException e) {
            LOGGER.error("Couldn't send one time token", e);
        }
    }

    @Override
    public void sendLoginInfo(final MotechUser user, String token) {

        LOGGER.info("Sending login information to user: {}", user.getUserName());

        try {

            String text = templateParser.mergeTemplateIntoString(LOGIN_INFORMATION_TEMPLATE,
                    passLoginInfoTemplateParams(user.getUserName(), user.getLocale(), token));

            String subject = messageSource.getMessage(LOGIN_INFORMATION_MESSAGE_SUBJECT, null, user.getLocale());

            sendEmail(user.getEmail(), text, subject);

        } catch (VelocityTemplateParsingException e) {
            LOGGER.error("Couldn't send login information to user: {}", user.getUserName(), e);
        }
    }

    @Override
    public void sendPasswordResetReminder(Map<String, Object> params) {

        String username = (String) params.get(TEMPLATE_PARAM_USERNAME);

        LOGGER.info("Sending password change reminder to user: {}", username);

        try {

            String text = templateParser.mergeTemplateIntoString(PASSWORD_CHANGE_REMINDER_TEMPLATE,
                    passExpirationTemplateParams(params));

            String subject = messageSource.getMessage(PASSWORD_CHANGE_REMINDER_MESSAGE_SUBJECT, null,
                    (Locale) params.get(TEMPLATE_PARAM_LOCALE));

            sendEmail((String) params.get(EMAIL_PARAM_TO_ADDRESS), text, subject);

        } catch (VelocityTemplateParsingException e) {
            LOGGER.error("Couldn't send password change reminder to user: {}", username, e);
        }
    }

    private Map<String, Object> passRecoveryTemplateParams(PasswordRecovery passwordRecovery) {
        return passLoginInfoTemplateParams(passwordRecovery.getUsername(), passwordRecovery.getLocale(), passwordRecovery.getToken());
    }

    private Map<String, Object> passLoginInfoTemplateParams(String username, Locale locale, String token) {
        Map<String, Object> params = new HashMap<>();

        String path = "";
        String flag;
        if (settingsFacade.getPlatformSettings().getLoginMode().isRepository()) {
            flag = RESET_PATH;
            path += "/server/";
        } else {
            flag = ONE_TIME_TOKEN_PATH;
            path += "/websecurity/api/";
        }

        if (StringUtils.isEmpty(settingsFacade.getPlatformSettings().getServerUrl())) {
            throw new ServerUrlIsEmptyException("The server url property has to be set");
        }

        String link = joinUrls(settingsFacade.getPlatformSettings().getServerUrl(),
                path + "forgot" + flag + "?token=") + token;

        params.put(TEMPLATE_PARAM_LINK, link);
        params.put(TEMPLATE_PARAM_USERNAME, username);
        params.put(TEMPLATE_PARAM_MESSAGES, messageSource);
        params.put(TEMPLATE_PARAM_LOCALE, locale);

        return params;
    }

    private Map<String, Object> passExpirationTemplateParams(Map<String, Object> eventParams) {
        Map<String, Object> params = new HashMap<>();

        String serverUrl = settingsFacade.getPlatformSettings().getServerUrl();
        if (StringUtils.isEmpty(serverUrl)) {
            throw new ServerUrlIsEmptyException("The server url property has to be set");
        }

        params.putAll(eventParams);
        params.put(TEMPLATE_PARAM_MESSAGES, messageSource);
        params.put(TEMPLATE_PARAM_SERVER_URL, serverUrl);

        return params;
    }

    private String joinUrls(String first, String second) {
        StringBuilder sb = new StringBuilder(first);
        if (!first.endsWith("/") && !second.startsWith("/")) {
            sb.append("/");
        }
        sb.append(second);
        return sb.toString();
    }

    private void sendEmail(String userEmail, String messageText, String messageSubject) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_PARAM_FROM_ADDRESS, getSenderAddress());
        params.put(EMAIL_PARAM_TO_ADDRESS, userEmail);
        params.put(EMAIL_PARAM_MESSAGE, messageText);
        params.put(EMAIL_PARAM_SUBJECT, messageSubject);

        MotechEvent emailEvent = new MotechEvent("SendEMail", params);
        eventRelay.sendEventMessage(emailEvent);
        LOGGER.info("Sent email event: {}", emailEvent.getSubject());
    }

    private String getSenderAddress() {
        String address = "noreply@";

        String serverUrl = settingsFacade.getPlatformSettings().getServerHost();

        if (StringUtils.isNotBlank(serverUrl)) {
            address += serverUrl;
        }

        return address;
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @Autowired
    public void setMessageSource(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setTemplateParser(VelocityTemplateParser templateParser) {
        this.templateParser = templateParser;
    }
}
