package org.motechproject.security.domain;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Represents Motech user
 */
public class MotechUserProfile implements Serializable {
    private static final long serialVersionUID = -5704183407915646673L;

    private String externalId;
    private String userName;
    private List<String> roles;
    private Locale locale;
    private boolean active;

    public MotechUserProfile(MotechUser user) {
        this.externalId = user.getExternalId();
        this.userName = user.getUserName();
        this.roles = user.getRoles();
        this.locale = user.getLocale();
        this.active = user.isActive();
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasRole(String role) {
        return CollectionUtils.isNotEmpty(roles) && roles.contains(role);
    }
}
