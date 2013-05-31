package org.motechproject.email.service.impl;

import org.motechproject.email.model.Mail;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MotechMimeMessagePreparator implements MimeMessagePreparator {
    private final Mail mail;

    public MotechMimeMessagePreparator(Mail mail) {
        this.mail = mail;
    }

    @Override
    public void prepare(MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper message = getMimeMessageHelper(mimeMessage);
        message.setTo(mail.getToAddress());
        message.setFrom(mail.getFromAddress());
        message.setSubject(mail.getSubject());
        message.setText(mail.getText(), true);
    }

    private MimeMessageHelper getMimeMessageHelper(MimeMessage mimeMessage) {
        return new MimeMessageHelper(mimeMessage);
    }
}
