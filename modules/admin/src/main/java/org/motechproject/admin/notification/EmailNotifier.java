package org.motechproject.admin.notification;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.email.exception.EmailSendException;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

/**
 * A component responsible for sending emails from the admin module. The sent emails are Velocity templates
 * loaded from {@code /mail} on the classpath. Uses the {@link EmailSenderService}.
 *
 * @see EmailSenderService
 */
@Component
public class EmailNotifier {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private EmailSenderService emailSender;

    private static final String CRITICAL_NOTIFICATION_TEMPLATE = "/mail/criticalNotification.vm";

    /**
     * Sends a critical notification for a given {@link StatusMessage}. The sender address always
     * defaults to noreply@server.hostname where server.hostname is configured in the platform settings.
     *
     * @param statusMessage The {@link StatusMessage} for which the notification should be generated.
     * @param recipient The recipient of the notification.
     */
    public void send(StatusMessage statusMessage, String recipient) throws EmailSendException {
        Map<String, Object> model = templateParams(statusMessage);
        String text = mergeTemplateIntoString(model);
        emailSender.send(senderAddress(), recipient,
                statusMessage.getLevel() + " notification raised in Motech", text);
    }

    protected String mergeTemplateIntoString(Map<String, Object> model) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, CRITICAL_NOTIFICATION_TEMPLATE, model);
    }

    protected String senderAddress() {
        String address = "noreply@";

        String serverUrl = configurationService.getPlatformSettings().getServerHost();

        if (StringUtils.isNotBlank(serverUrl)) {
            address += serverUrl;
        }

        return address;
    }

    protected Map<String, Object> templateParams(StatusMessage statusMessage) {
        Map<String, Object> params = new HashMap<>();

        String dateTime = DateTimeFormat.shortDateTime().print(setTimeZone(statusMessage.getDate()));

        params.put("dateTime", dateTime);
        params.put("msg", statusMessage.getText());
        params.put("module", statusMessage.getModuleName());
        params.put("msgLink", messagesUrl());
        params.put("level", statusMessage.getLevel());

        return params;
    }

    /**
     * Creates url to the message option.
     *
     * @return url to message option
     */
    protected String messagesUrl() {
        String serverUrl = configurationService.getPlatformSettings().getServerUrl();
        if (serverUrl == null) {
            serverUrl = "";
        }
        return serverUrl + "/server/?moduleName=admin#/messages";
    }
}
