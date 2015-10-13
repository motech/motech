package org.motechproject.security.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_EVENT;

/**
 * Responsible for handling PASSWORD_CHANGE_REMINDER_EVENT event. It will send an e-mail notifying the user about required
 * password change.
 */
@Component
public class PasswordReminderEventHandler {

    private EmailSender emailSender;

    /**
     * Handles the PASSWORD_CHANGE_REMINDER_EVENT event by sending an e-mail to the user passed as a parameter notifying
     * him/her about incoming, required password change.
     *
     * @param event  the event to be handled
     */
    @MotechListener(subjects = PASSWORD_CHANGE_REMINDER_EVENT)
    public void handleEvent(MotechEvent event) {
        emailSender.sendPasswordResetReminder(event.getParameters());
    }

    @Autowired
    public PasswordReminderEventHandler(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
}
