package org.motechproject.security.email;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link EmailSender} interface. Class provides API for sending e-mails
 */
@Service
public class EmailSenderImpl implements EmailSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderImpl.class);

    private static final String RESET_MAIL_TEMPLATE = "/mail/resetMail.vm";
    private static final String ONE_TIME_TOKEN_TEMPLATE = "/mail/oneTimeTokenMail.vm";
    private static final String RECOVERY_SUBJECT = "Motech Password Recovery";
    private static final String ONE_TIME_TOKEN_SUBJECT = "Motech One Time Token For Admin User";
    private static final String LOGIN_INFORMATION_TEMPLATE = "/mail/loginInfo.vm";
    private static final String LOGIN_INFORMATION_SUBJECT = "Motech Login Information";
    private static final String EMAIL_PARAM_FROM_ADDRESS = "fromAddress";
    private static final String EMAIL_PARAM_TO_ADDRESS = "toAddress";
    private static final String EMAIL_PARAM_MESSAGE = "message";
    private static final String EMAIL_PARAM_SUBJECT = "subject";
    private static final String TEMPLATE_PARAM_LINK = "link";
    private static final String TEMPLATE_PARAM_USERNAME = "user";
    private static final String TEMPLATE_PARAM_PASSWORD = "password";
    private static final String TEMPLATE_PARAM_MESSAGES = "messages";
    private static final String TEMPLATE_PARAM_LOCALE = "locale";

    @Autowired
    private EventRelay eventRelay;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @Override
    public void sendResecoveryEmail(final PasswordRecovery recovery) {
        LOGGER.info("Sending recovery email");
        Map<String, Object> model = templateParams(recovery, "reset");
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, RESET_MAIL_TEMPLATE, model);

        sendEmail(recovery.getEmail(), text, RECOVERY_SUBJECT);
    }

    @Override
    public void sendOneTimeToken(final PasswordRecovery recovery) {
        LOGGER.info("Sending one time token");
        Map<String, Object> model = templateParams(recovery, "onetimetoken");
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, ONE_TIME_TOKEN_TEMPLATE, model);

        sendEmail(recovery.getEmail(), text, ONE_TIME_TOKEN_SUBJECT);
    }

    public void sendLoginInfo(final MotechUser user, final String password) {
        LOGGER.info("Sending login information to user: {}", user.getUserName());
        Map<String, Object> model = loginInformationParams(user, password);
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, LOGIN_INFORMATION_TEMPLATE, model);

        sendEmail(user.getEmail(), text, LOGIN_INFORMATION_SUBJECT);
    }


    private Map<String, Object> loginInformationParams(MotechUser user, String password) {
        Map<String, Object> params = new HashMap<>();

        String link = settingsFacade.getPlatformSettings().getServerUrl();

        params.put(TEMPLATE_PARAM_LINK, link);
        params.put(TEMPLATE_PARAM_USERNAME, user.getUserName());
        params.put(TEMPLATE_PARAM_PASSWORD, password);
        params.put(TEMPLATE_PARAM_MESSAGES, messageSource);
        params.put(TEMPLATE_PARAM_LOCALE, user.getLocale());

        return params;
    }

    private Map<String, Object> templateParams(PasswordRecovery recovery, String flag) {
        Map<String, Object> params = new HashMap<>();

        String path = "/module";

        if ("reset".equals(flag)) {
            path += "/server/";
        } else {
            path += "/websecurity/api/";
        }

        String link = joinUrls(settingsFacade.getPlatformSettings().getServerUrl(),
                path + "forgot" + flag + "?token=") + recovery.getToken();

        params.put(TEMPLATE_PARAM_LINK, link);
        params.put(TEMPLATE_PARAM_USERNAME, recovery.getUsername());
        params.put(TEMPLATE_PARAM_MESSAGES, messageSource);
        params.put(TEMPLATE_PARAM_LOCALE, recovery.getLocale());

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
}
