package org.motechproject.email.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

public class EmailSenderServiceTest {

    @InjectMocks
    private EmailSenderService emailSender = new EmailSenderServiceImplStub();

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MotechMimeMessagePreparator motechPreparator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSendCriticalNotification() throws Exception {
        Mail mail = new Mail("from","to","subject","text");

        emailSender.send(mail);

        verify(javaMailSender).send(motechPreparator);
    }

    private class EmailSenderServiceImplStub extends EmailSenderServiceImpl {
        EmailSenderServiceImplStub() {
        }

        @Override
        MotechMimeMessagePreparator getMimeMessagePreparator(Mail mail) {
            return motechPreparator;
        }
    }
}
