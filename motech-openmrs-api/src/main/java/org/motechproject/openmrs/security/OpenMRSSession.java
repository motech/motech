package org.motechproject.openmrs.security;

import org.motechproject.mrs.security.MRSUser;
import org.openmrs.api.context.Context;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class OpenMRSSession {

    public void open() {
        Context.openSession();
    }

    public void authenticate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MRSUser principal = (MRSUser) authentication.getPrincipal();
        login(principal.getUsername(), principal.getPassword());
    }

    public void close() {
        Context.closeSession();
    }

    public static MRSUser login(String userName, String password) {
        Context.openSession();
        Context.authenticate(userName, password);
        OpenMRSUser openMRSUser = new OpenMRSUser(Context.getAuthenticatedUser());
        Context.closeSession();
        return openMRSUser;
    }
}
