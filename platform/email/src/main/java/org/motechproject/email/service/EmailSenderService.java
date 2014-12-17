package org.motechproject.email.service;


import org.motechproject.email.contract.Mail;

/**
 *  The <code>EmailSenderService</code> interface provides a method for sending email.
 */
public interface EmailSenderService {

    /**
     * Attempts to send the supplied email message. Adds an {@link org.motechproject.email.domain.EmailRecord}
     * entry to the log with the details of the activity.
     *
     * @param message  the message to send
     */
    void send(Mail message);
}
