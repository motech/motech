package org.motechproject.openmrs;

import org.motechproject.mrs.security.InvalidCredentialsException;
import org.motechproject.mrs.security.MRSUser;
import org.motechproject.openmrs.security.OpenMRSSession;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenMRSAuthenticationProviderForTests {
    private OpenMRSSession openMRSSession;

    private OpenMRSAuthenticationProviderForTests(){}

    @Autowired
    public OpenMRSAuthenticationProviderForTests(OpenMRSSession openMRSSession) {
        this.openMRSSession = openMRSSession;
    }

    public MRSUser authenticate(String userName, String password) throws InvalidCredentialsException {
        try {
            return openMRSSession.authenticate(userName, password);
        } catch (ContextAuthenticationException e) {
            throw new InvalidCredentialsException();
        }
    }

}
