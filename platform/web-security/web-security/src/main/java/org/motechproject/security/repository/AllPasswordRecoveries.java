package org.motechproject.security.repository;

import org.joda.time.DateTime;
import org.motechproject.security.domain.PasswordRecovery;

import java.util.List;

public interface AllPasswordRecoveries {

    List<PasswordRecovery> getExpired();

    List<PasswordRecovery> allRecoveries();

    PasswordRecovery findForUser(String username);

    PasswordRecovery findForToken(String token);

    PasswordRecovery createRecovery(String username, String email, String token, DateTime expirationDate);

    void update(PasswordRecovery passwordRecovery);

    void add(PasswordRecovery passwordRecovery);

    void remove(PasswordRecovery passwordRecovery);
}
