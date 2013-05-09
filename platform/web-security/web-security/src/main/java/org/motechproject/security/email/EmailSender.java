package org.motechproject.security.email;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.PasswordRecovery;

import java.util.Locale;

public interface EmailSender {

    void sendResecoveryEmail(PasswordRecovery recovery, Locale locale);

    void sendOneTimeToken(PasswordRecovery recovery, Locale locale);

    void sendLoginInfo(MotechUser user, String password, Locale locale);
}
