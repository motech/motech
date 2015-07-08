package org.motechproject.security.email;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;

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
}
