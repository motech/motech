package org.motechproject.email.service.impl;

import org.motechproject.email.constants.SendEmailConstants;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The <code>SendEmailEventHandlerImpl</code> class is responsible for listening to and handling events
 * connected with sending e-mails
 */

@Service
public class SendEmailEventHandlerImpl {

    private EmailSenderService emailSenderService;

    @Autowired
    public SendEmailEventHandlerImpl(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @MotechListener (subjects = { SendEmailConstants.SEND_EMAIL_SUBJECT })
    public void handle(MotechEvent event) {
        String fromAddress = (String) event.getParameters().get(SendEmailConstants.FROM_ADDRESS);
        String toAddress = (String) event.getParameters().get(SendEmailConstants.TO_ADDRESS);
        String subject = (String) event.getParameters().get(SendEmailConstants.SUBJECT);
        String message = (String) event.getParameters().get(SendEmailConstants.MESSAGE);

        emailSenderService.send(new Mail(fromAddress, toAddress, subject, message));
    }
}
