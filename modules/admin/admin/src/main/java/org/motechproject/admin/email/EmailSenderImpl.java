package org.motechproject.admin.email;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailSenderImpl implements EmailSender {

    private static final String CRITICAL_NOTIFICATION_TEMPLATE = "/mail/criticalNotification.vm";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private PlatformSettingsService settingsService;

    @Override
    public void sendCriticalNotificationEmail(final String address, final StatusMessage statusMessage) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(address);
                message.setFrom(senderAddress());
                message.setSubject("Critical notification raised in Motech");

                Map<String, Object> model = templateParams(statusMessage);
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, CRITICAL_NOTIFICATION_TEMPLATE, model);

                message.setText(text, true);
            }
        };

        mailSender.send(preparator);
    }

    private String senderAddress() {
        String address = "noreply@";

        String serverUrl = settingsService.getPlatformSettings().getServerUrl();

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

    private String messagesUrl() {
        String serverUrl = settingsService.getPlatformSettings().getServerUrl();

        if (StringUtils.isNotBlank(serverUrl)) {
            if (!serverUrl.matches("^\\w+://")) {
                serverUrl = "http://" + serverUrl;
            }
        } else {
            serverUrl = "";
        }

        return serverUrl + "/module/server/?moduleName=admin#/messages";
    }
}
