package org.motechproject.openmrs.security;

import org.motechproject.mrs.exception.InvalidCredentialsException;
import org.motechproject.mrs.security.MRSAuthenticationProvider;
import org.motechproject.mrs.security.MRSSecurityUser;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

public class OpenMRSAuthenticationProvider extends MRSAuthenticationProvider {

    @Override
    protected MRSSecurityUser retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            return OpenMRSSession.login(userName, (String) authentication.getCredentials());
        } catch (ContextAuthenticationException e) {
            throw new InvalidCredentialsException();
        }
    }
}