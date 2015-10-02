package org.motechproject.security.event;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.email.EmailSender;
import org.motechproject.security.repository.MotechUsersDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.motechproject.security.constants.EventSubjects.PASSWORD_CHANGE_REMINDER;

/**
 * Responsible for handling PASSWORD_CHANGE_REMINDER event. It will send an e-mail notifying the user about required
 * password change.
 */
@Component
public class PasswordReminderEventHandler {

    private EmailSender emailSender;
    private MotechUsersDataService usersDataService;

    /**
     * Handles the PASSWORD_CHANGE_REMINDER event by sending an e-mail to the user passed as a parameter notifying
     * him/her about incoming, required password change.
     *
     * @param event  the event to be handled
     */
    @MotechListener(subjects = {PASSWORD_CHANGE_REMINDER})
    public void handleEvent(MotechEvent event) {
        Map<String, Object> params = event.getParameters();
        emailSender.sendPasswordResetReminder(usersDataService.findByUserName((String) params.get("username")),
                (DateTime) event.getParameters().get("expirationDate"));
    }

    @Autowired
    public PasswordReminderEventHandler(EmailSender emailSender, MotechUsersDataService usersDataService) {
        this.emailSender = emailSender;
        this.usersDataService = usersDataService;
    }
}
