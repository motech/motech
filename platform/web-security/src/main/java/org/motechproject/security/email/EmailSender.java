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
     * Sends email that allows user to recover his or her password
     *
     * @param recovery with data necessary to send email
     * @param message with custom contents to be sent
     */
    void sendRecoveryEmail(PasswordRecovery recovery, String message);

    /**
     * Sends email that contains one time token used to recover password
     *
     * @param recovery with data necessary to send email
     */
    void sendOneTimeToken(PasswordRecovery recovery);

    /**
     * Sends email that contains one time token used to recover password
     *
     * @param recovery with data necessary to send email
     * @param message with custom contents to be sent
     */
    void sendOneTimeToken(PasswordRecovery recovery, String message);

    /**
     * Sends email with login info of given user
     *
     * @param user whose info will be send
     * @param password of user
     */
    void sendLoginInfo(MotechUser user, String password);

    /**
     * Sends email with login info of given user
     *
     * @param user whose info will be send
     * @param password of user
     * @param message with custom contents to be sent
     */
    void sendLoginInfo(MotechUser user, String password, String message);
}
