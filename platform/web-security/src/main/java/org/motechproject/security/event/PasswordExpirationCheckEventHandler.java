package org.motechproject.security.event;

import org.joda.time.Days;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.constants.EventSubjects;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.repository.MotechUsersDataService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.security.constants.EventSubjects.PASSWORD_EXPIRATION_CHECK;

/**
 * Responsible for handling PASSWORD_EXPIRATION_CHECK event. It will check password expiration date and send an motech
 * event if the user should change the password. This event is then handled and an e-mail is send to the user.
 */
@Component
public class PasswordExpirationCheckEventHandler {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PasswordExpirationCheckEventHandler.class);

    private MotechUsersDataService allUsers;
    private SettingService settingService;
    private EventRelay eventRelay;

    /**
     * Handles PASSWORD_EXPIRATION_CHECK event. Checks every user for the date of the last password change and sends an
     * event if the user should be notified about the required password change.
     *
     * @param event  the event to be handled
     */
    @MotechListener(subjects = {PASSWORD_EXPIRATION_CHECK})
    public void handleEvent(MotechEvent event) {

        LOGGER.info("Daily password reset reminder triggered");

        if (settingService.isPasswordResetReminderEnabled()) {
            int daysTillPasswordChange = settingService.getNumberOfDaysToChangePassword();
            int daysTillReminder = daysTillPasswordChange - settingService.getNumberOfDaysForReminder();

            for (MotechUser user : allUsers.retrieveAll()) {
                int daysWithoutPasswordChange = daysWithoutPasswordChange(user);

                LOGGER.debug("User {} hasn't changed password in {} days. Notification should be sent at day {}",
                        user.getUserName(), daysWithoutPasswordChange, daysTillReminder);

                if (daysWithoutPasswordChange == daysTillReminder) {
                    sendPasswordReminderEvent(user, daysTillPasswordChange);
                }
            }
        }
    }

    private void sendPasswordReminderEvent(MotechUser user, int daysTillPasswordChange) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", user.getUserName());
        parameters.put("email", user.getEmail());
        parameters.put("expirationDate", user.getLastPasswordChange().plusDays(daysTillPasswordChange));
        eventRelay.sendEventMessage(new MotechEvent(EventSubjects.PASSWORD_CHANGE_REMINDER, parameters));

        LOGGER.info("Event notifying user {} about incoming required password change sent. The password should be" +
                        "changed at {}. User e-mail is {}", user.getUserName(),
                parameters.get("expirationDate").toString(), user.getEmail());
    }

    private int daysWithoutPasswordChange(MotechUser user) {
        return Days.daysBetween(user.getLastPasswordChange(), DateUtil.now()).getDays();
    }

    @Autowired
    public void setAllUsers(MotechUsersDataService allUsers) {
        this.allUsers = allUsers;
    }

    @Autowired
    public void setSettingsService(SettingService settingService) {
        this.settingService = settingService;
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }
}
