package org.motechproject.security.email;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.security.domain.PasswordRecovery;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSenderImpl implements EmailSender {

    private static final String RESET_MAIL_TEMPLATE = "/mail/resetMail.vm";
    private static final String ONE_TIME_TOKEN_TEMPLATE = "/mail/oneTimeTokenMail.vm";
    private static final String RECOVERY_SUBJECT = "Motech Password Recovery";
    private static final String ONE_TIME_TOKEN_SUBJECT = "Motech One Time Token For Admin User";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private PlatformSettingsService settingsService;

    @Override
    public void sendResecoveryEmail(final PasswordRecovery recovery) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(recovery.getEmail());
                message.setFrom("noreply@motechsuite.org");
                message.setSubject(RECOVERY_SUBJECT);

                Map<String, Object> model = templateParams(recovery, "reset");
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, RESET_MAIL_TEMPLATE, model);

                // send as html
                message.setText(text, true);
            }
        };
        mailSender.send(preparator);
    }

    @Override
    public void sendOneTimeToken(final PasswordRecovery recovery) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws MessagingException {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(recovery.getEmail());
                message.setFrom("noreply@motechsuite.org");
                message.setSubject(ONE_TIME_TOKEN_SUBJECT);

                Map<String, Object> model = templateParams(recovery, "onetimetoken");
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, ONE_TIME_TOKEN_TEMPLATE, model);

                // send as html
                message.setText(text, true);
            }
        };
        mailSender.send(preparator);
    }

    private Map<String, Object> templateParams(PasswordRecovery recovery, String flag) {
        Map<String, Object> params = new HashMap<>();

        String link = joinUrls(settingsService.getPlatformSettings().getServerUrl(),
                "/module/websecurity/api/"+flag+"?token=") + recovery.getToken();

        params.put("link", link);
        params.put("user", recovery.getUsername());

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
}
