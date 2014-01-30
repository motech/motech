package org.motechproject.email.service;


import org.motechproject.email.model.Mail;

/**
 *  The <code>EmailSenderService</code> interface provides method for sending email.
 */
public interface EmailSenderService {

    void send(Mail message);
}
