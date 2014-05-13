package org.motechproject.security.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Locale;

import static org.motechproject.commons.date.util.DateUtil.setTimeZoneUTC;

/**
 * Default implementation of {@link PasswordRecovery}.
 */
@TypeDiscriminator("doc.type == 'PasswordRecovery'")
public class PasswordRecoveryImpl extends MotechBaseDataObject implements PasswordRecovery {
    private static final long serialVersionUID = -6849774668390898927L;

    private String token;
    private String username;
    private String email;
    private DateTime expirationDate;
    private Locale locale;

    public PasswordRecoveryImpl() {
        super(PasswordRecovery.class.getSimpleName());
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public DateTime getExpirationDate() {
        return setTimeZoneUTC(expirationDate);
    }

    @Override
    public void setExpirationDate(DateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
