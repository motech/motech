package org.motechproject.security.service;

import org.motechproject.security.domain.MotechUser;

import java.io.Serializable;
import java.util.List;

public class MotechUserProfile implements Serializable {

    private MotechUser user;

    public MotechUserProfile(MotechUser motechUser) {
        this.user = motechUser;
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
