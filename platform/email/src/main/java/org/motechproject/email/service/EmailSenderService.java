package org.motechproject.email.service;


import org.motechproject.email.exception.EmailSendException;

/**
 *  The <code>EmailSenderService</code> interface provides a method for sending email.
 */
public interface EmailSenderService {

    /**
     * Attempts to send the supplied email message. Adds an {@link org.motechproject.email.domain.EmailRecord}
     * entry to the log with the details of the activity.
     * @param fromAddress  the email address of the sender
     * @param toAddress  the email address of the recipient
     * @param subject  the subject of the email
     * @param message  the body of the email
     */
    void send(String fromAddress, String toAddress, String subject, String message) throws EmailSendException;
}
