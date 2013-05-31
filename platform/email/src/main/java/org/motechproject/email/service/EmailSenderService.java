package org.motechproject.email.service;


import org.motechproject.email.model.Mail;

public interface EmailSenderService {

    void send(Mail message);
}
