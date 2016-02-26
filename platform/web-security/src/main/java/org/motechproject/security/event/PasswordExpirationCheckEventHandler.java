package org.motechproject.security.event;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Days;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.service.mds.MotechUsersDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.security.constants.EmailConstants.EMAIL_PARAM_TO_ADDRESS;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_CHANGE_REMINDER_EVENT;
import static org.motechproject.security.constants.EmailConstants.PASSWORD_EXPIRATION_CHECK_EVENT;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_DAYS_TILL_EXPIRE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXPIRATION_DATE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_EXTERNAL_ID;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LAST_PASSWORD_CHANGE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_LOCALE;
import static org.motechproject.security.constants.EmailConstants.TEMPLATE_PARAM_USERNAME;

/**
 * Responsible for handling PASSWORD_EXPIRATION_CHECK_EVENT event. It will check password expiration date and send an motech
 * event if the user should change the password. This event is then handled and an e-mail is send to the user.
 */
@Component
public class PasswordExpirationCheckEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordExpirationCheckEventHandler.class);

    private MotechUsersDataService allUsers;
    private SettingService settingService;
    private EventRelay eventRelay;

    /**
     * Handles PASSWORD_EXPIRATION_CHECK_EVENT event. Checks every user for the date of the last password change and sends an
     * event if the user should be notified about the required password change.
     *
     * @param event  the event to be handled
     */
    @MotechListener(subjects = PASSWORD_EXPIRATION_CHECK_EVENT)
    public void handleEvent(MotechEvent event) {

        if (settingService.isPasswordResetReminderEnabled()) {

            LOGGER.info("Daily password reset reminder triggered");

            final int passwordExpirationInDays = settingService.getNumberOfDaysToChangePassword();
            final int daysBeforeExpirationToSendReminder = settingService.getNumberOfDaysForReminder();

            final int daysWithNoPassChangeForReminder = passwordExpirationInDays - daysBeforeExpirationToSendReminder;

            for (MotechUser user : allUsers.retrieveAll()) {
                final int daysWithoutPasswordChange = daysWithoutPasswordChange(user);

                LOGGER.debug("User {} hasn't changed password in {} days. Notification is being after {} days without" +
                                " password change, {} days before expiration", user.getUserName(),
                        daysWithoutPasswordChange, daysWithNoPassChangeForReminder, daysBeforeExpirationToSendReminder);

                if (daysWithoutPasswordChange == daysWithNoPassChangeForReminder) {
                    if (StringUtils.isNotBlank(user.getEmail())) {
                        sendPasswordReminderEvent(user, passwordExpirationInDays, daysBeforeExpirationToSendReminder);
                    } else {
                        LOGGER.debug("User {} doesn't have an email address set, skipping sending of reminder",
                                user.getUserName());
                    }
                }
            }
        } else {
            LOGGER.info("Daily password reset reminder is disabled, skipping processing of users");
        }
    }

    private void sendPasswordReminderEvent(MotechUser user, int daysTillPasswordChange, int daysTillExpire) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TEMPLATE_PARAM_USERNAME, user.getUserName());
        parameters.put(EMAIL_PARAM_TO_ADDRESS, user.getEmail());
        parameters.put(TEMPLATE_PARAM_EXPIRATION_DATE, user.getSafeLastPasswordChange().plusDays(daysTillPasswordChange));
        parameters.put(TEMPLATE_PARAM_LOCALE, user.getLocale());
        parameters.put(TEMPLATE_PARAM_LAST_PASSWORD_CHANGE, user.getSafeLastPasswordChange());
        parameters.put(TEMPLATE_PARAM_EXTERNAL_ID, user.getExternalId());
        parameters.put(TEMPLATE_PARAM_DAYS_TILL_EXPIRE, daysTillExpire);

        eventRelay.sendEventMessage(new MotechEvent(PASSWORD_CHANGE_REMINDER_EVENT, parameters));

        LOGGER.info("Event notifying user {} about incoming required password change sent. The password should be" +
                        "changed at {}. User e-mail is {}", user.getUserName(),
                parameters.get(TEMPLATE_PARAM_EXPIRATION_DATE).toString(), user.getEmail());
    }

    private int daysWithoutPasswordChange(MotechUser user) {
        return Days.daysBetween(user.getSafeLastPasswordChange(), DateUtil.now()).getDays();
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
