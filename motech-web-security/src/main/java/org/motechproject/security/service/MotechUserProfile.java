package org.motechproject.security.service;

import org.motechproject.security.domain.MotechUser;

import java.util.List;

public class MotechUserProfile {

    private MotechUser user;

    public MotechUserProfile(MotechUser motechUser) {
        this.user = user;
    }

    public String getExternalId() {
        return user.getExternalId();
    }

    public String getUserName() {
        return user.getUserName();
    }

    public List<String> getRoles() {
        return user.getRoles();
    }
}
