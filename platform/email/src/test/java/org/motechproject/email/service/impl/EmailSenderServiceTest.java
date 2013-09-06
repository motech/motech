package org.motechproject.email.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailSenderServiceTest {

    @InjectMocks
    private EmailSenderService emailSender = new EmailSenderServiceImpl();

    @Mock
    private EmailAuditService emailAuditService;

    @Mock
    private JavaMailSender javaMailSender;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSendCriticalNotification() throws Exception {
        Mail mail = new Mail("from", "to", "subject", "text");

        emailSender.send(mail);

        verify(javaMailSender).send(new MotechMimeMessagePreparator(mail));
    }

}
