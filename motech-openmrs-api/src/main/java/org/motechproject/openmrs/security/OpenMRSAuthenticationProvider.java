package org.motechproject.openmrs.security;

import org.motechproject.mrs.security.InvalidCredentialsException;
import org.motechproject.mrs.security.MRSAuthenticationProvider;
import org.motechproject.mrs.security.MRSUser;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class OpenMRSAuthenticationProvider extends MRSAuthenticationProvider{

    private OpenMRSSession openMRSSession;

    private OpenMRSAuthenticationProvider(){}
    @Autowired
    public OpenMRSAuthenticationProvider(OpenMRSSession openMRSSession) {
        this.openMRSSession = openMRSSession;
    }

    @Override
    protected MRSUser retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication) throws InvalidCredentialsException {
        try {
            return openMRSSession.authenticate(userName, (String) authentication.getCredentials());
        } catch (ContextAuthenticationException e) {
            throw new InvalidCredentialsException();
        }
    }
}