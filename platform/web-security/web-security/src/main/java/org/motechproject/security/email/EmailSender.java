package org.motechproject.security.email;

import org.motechproject.security.domain.PasswordRecovery;

public interface EmailSender {

    void sendResecoveryEmail(PasswordRecovery recovery);

    void sendOneTimeToken(PasswordRecovery recovery);
}
