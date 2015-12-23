package org.motechproject.email.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.email.contract.Mail;
import org.motechproject.email.exception.EmailSendException;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.motechproject.email.constants.SendEmailConstants.FROM_ADDRESS;
import static org.motechproject.email.constants.SendEmailConstants.MESSAGE;
import static org.motechproject.email.constants.SendEmailConstants.SEND_EMAIL_SUBJECT;
import static org.motechproject.email.constants.SendEmailConstants.SUBJECT;
import static org.motechproject.email.constants.SendEmailConstants.TO_ADDRESS;

@RunWith(MockitoJUnitRunner.class)
public class SendEmailEventHandlerImplTest {

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private SendEmailEventHandlerImpl emailEventHandler = new SendEmailEventHandlerImpl();

    @Test
    public void testIfThereIsHandlerMethodForSendEmailEvent() throws NoSuchMethodException {
        Method handleMethod = emailEventHandler.getClass().getDeclaredMethod("handle", MotechEvent.class);
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{SEND_EMAIL_SUBJECT}, annotation.subjects());
    }

    @Test
    public void testIfEmailSenderServiceIsCalledWithEventValues() throws EmailSendException {

        String from = "testfromaddress";
        String to = "testtoaddress";
        String message = "test message";
        String subject = "test subject";

        Map<String, Object> values = new HashMap<>();
        values.put(FROM_ADDRESS, from);
        values.put(TO_ADDRESS, to);
        values.put(MESSAGE, message);
        values.put(SUBJECT, subject);

        emailEventHandler.handle(new MotechEvent(SEND_EMAIL_SUBJECT, values));
        ArgumentCaptor<Mail> captor = ArgumentCaptor.forClass(Mail.class);
        verify(emailSenderService).send(captor.capture());

        assertEquals(captor.getValue().getFromAddress(), from);
        assertEquals(captor.getValue().getToAddress(), to);
        assertEquals(captor.getValue().getSubject(), subject);
        assertEquals(captor.getValue().getMessage(), message);
    }

}