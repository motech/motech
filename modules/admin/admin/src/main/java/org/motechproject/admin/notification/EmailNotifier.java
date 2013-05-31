package org.motechproject.admin.notification;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmailNotifier {

    @Autowired
    private PlatformSettingsService settingsService;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private EmailSenderService emailSender;

    private static final String CRITICAL_NOTIFICATION_TEMPLATE = "/mail/criticalNotification.vm";

    public void send(StatusMessage statusMessage, String recipients) {
        Map<String, Object> model = templateParams(statusMessage);
        String text = mergeTemplateIntoString(model);
        emailSender.send(new Mail(senderAddress(), recipients, "Critical notification raised in Motech", text));
    }

    String mergeTemplateIntoString(Map<String, Object> model) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, CRITICAL_NOTIFICATION_TEMPLATE, model);
    }

    private String senderAddress() {
        String address = "noreply@";

        String serverUrl = settingsService.getPlatformSettings().getServerHost();

        if (StringUtils.isNotBlank(serverUrl)) {
            address += serverUrl;
        }

        return address;
    }


    private Map<String, Object> templateParams(StatusMessage statusMessage) {
        Map<String, Object> params = new HashMap<>();

        String dateTime = DateTimeFormat.shortDateTime().print(statusMessage.getDate());

        params.put("dateTime", dateTime);
        params.put("msg", statusMessage.getText());
        params.put("module", statusMessage.getModuleName());
        params.put("msgLink", messagesUrl());

        return params;
    }

    String messagesUrl() {
        String serverUrl = settingsService.getPlatformSettings().getServerUrl();
        if (serverUrl == null) {
            serverUrl = "";
        }
        return serverUrl + "/module/server/?moduleName=admin#/messages";
    }
}
