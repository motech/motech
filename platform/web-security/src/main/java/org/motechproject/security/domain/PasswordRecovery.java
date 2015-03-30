package org.motechproject.security.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;

import java.util.Locale;

/**
 * Entity that holds data used for password recovery
 */
@Entity
public class PasswordRecovery {
    private String token;
    private String username;
    private String email;
    private DateTime expirationDate;
    private Locale locale;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(DateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
