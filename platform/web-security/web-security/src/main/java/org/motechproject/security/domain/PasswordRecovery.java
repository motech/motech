package org.motechproject.security.domain;

import org.joda.time.DateTime;

public interface PasswordRecovery {

    String getToken();

    void setToken(String token);

    String getUsername();

    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    DateTime getExpirationDate();

    void setExpirationDate(DateTime expirationDate);
}
