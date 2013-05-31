package org.motechproject.email.service.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.email.model.Mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;

public class MotechMimeMessagePreparatorTest {
    @Test
    public void shouldPrepareMimeMessage() throws MessagingException, IOException {
        String fromAddress = "from@emaildomain.com";
        String toAddress = "to@emaildomain.com";
        String text = "mail body";
        String subject = "Mail subject";
        Mail mail = new Mail(fromAddress, toAddress, subject, text);
        MotechMimeMessagePreparator preparator = new MotechMimeMessagePreparator(mail);
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);

        preparator.prepare(mimeMessage);

        assertThat(mimeMessage.getFrom()[0].toString(), IsEqual.equalTo(fromAddress));
        assertThat(mimeMessage.getAllRecipients()[0].toString(), IsEqual.equalTo(toAddress));
        assertThat(mimeMessage.getDataHandler().getContent().toString(), IsEqual.equalTo(text));
        assertThat(mimeMessage.getSubject(), IsEqual.equalTo(subject));
    }
}
