package org.motechproject.email.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.email.model.Mail;
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

public class SendEmailEventHandlerImplTest {

    @InjectMocks
    SendEmailEventHandlerImpl emailEventHandler = new SendEmailEventHandlerImpl();

    @Mock
    EmailSenderService emailSenderService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIfThereIsHandlerMethodForSendEmailEvent() throws NoSuchMethodException {
        Method handleMethod = emailEventHandler.getClass().getDeclaredMethod("handle", new Class[]{MotechEvent.class});
        assertTrue("MotechListener annotation missing", handleMethod.isAnnotationPresent(MotechListener.class));
        MotechListener annotation = handleMethod.getAnnotation(MotechListener.class);
        assertArrayEquals(new String[]{SEND_EMAIL_SUBJECT}, annotation.subjects());
    }

    @Test
    public void testIfEmailSenderServiceIsCalledWithEventValues(){

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
