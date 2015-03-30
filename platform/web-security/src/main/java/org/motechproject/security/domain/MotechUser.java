package org.motechproject.security.domain;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.annotations.Entity;

import java.util.List;
import java.util.Locale;

/**
 * Entity that represents Motech user
 */
@Entity(recordHistory = true)
public class MotechUser {
    private String externalId;
    private String userName;
    private String password;
    private String email;
    private List<String> roles;
    private boolean active;
    private String openId;
    private Locale locale;

    public MotechUser() {
        this(null, null, null, null, null, null, null);
    }

    public MotechUser(String userName, String password, String email, String externalId,
                      List<String> roles, String openId, Locale locale) {
        this.userName = userName == null ? null : userName.toLowerCase();
        this.password = password;
        this.email = email;
        this.externalId = externalId;
        this.roles = roles;
        this.active = true;
        this.openId = openId;
        this.locale = locale;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean hasRole(String role) {
        return CollectionUtils.isNotEmpty(roles) && roles.contains(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MotechUser that = (MotechUser) o;

        return userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", userName, email);
    }
}
