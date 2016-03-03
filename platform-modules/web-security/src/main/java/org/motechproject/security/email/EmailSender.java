package org.motechproject.security.email;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;

import java.util.Map;

/**
 * Interface that exposes methods for sending emails
 */
public interface EmailSender {

    /**
     * Sends email that allows user to recover his or her password
     *
     * @param recovery with data necessary to send email
     */
    void sendRecoveryEmail(PasswordRecovery recovery);

    /**
     * Sends email that contains one time token used to recover password
     *
     * @param recovery with data necessary to send email
     */
    void sendOneTimeToken(PasswordRecovery recovery);

    /**
     * Sends email with login info of given user
     *
     * @param user whose info will be send
     * @param token for password recovery
     */
    void sendLoginInfo(MotechUser user, String token);

    /**
     * Sends email that informs the given user about password change.
     *
     * @param params  the map of parameters and their values used for sending the password reminder e-mail
     */
    void sendPasswordResetReminder(Map<String, Object> params);
}
