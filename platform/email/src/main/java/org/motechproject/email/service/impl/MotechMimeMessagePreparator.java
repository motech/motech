package org.motechproject.email.service.impl;

import org.motechproject.email.model.Mail;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

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
        message.setText(mail.getMessage(), true);
    }

    private MimeMessageHelper getMimeMessageHelper(MimeMessage mimeMessage) {
        return new MimeMessageHelper(mimeMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mail);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final MotechMimeMessagePreparator other = (MotechMimeMessagePreparator) obj;

        return Objects.equals(this.mail, other.mail);
    }

    @Override
    public String toString() {
        return String.format("MotechMimeMessagePreparator{mail=%s}", mail);
    }
}
