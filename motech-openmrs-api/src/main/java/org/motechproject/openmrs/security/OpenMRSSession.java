package org.motechproject.openmrs.security;

import org.motechproject.mrs.security.MRSUser;
import org.openmrs.User;
import org.openmrs.api.context.Context;


public class OpenMRSSession {

    private String userName;
    private String password;

    public void open() {
        Context.openSession();
    }

    public MRSUser authenticate(String userName, String password) {
        this.userName = userName;
        this.password = password;
        open();
        return authenticate();
    }

    public MRSUser authenticate() {
        Context.authenticate(this.userName, this.password);
        User user = Context.getAuthenticatedUser();
        return new MRSUser(user.getUsername(), null);
    }

    public void close() {
        Context.closeSession();
    }
}
