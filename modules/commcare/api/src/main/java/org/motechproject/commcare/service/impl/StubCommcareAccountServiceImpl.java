package org.motechproject.commcare.service.impl;

import org.motechproject.commcare.domain.CommcareAccountSettings;
import org.motechproject.commcare.exception.CommcareAuthenticationException;
import org.motechproject.commcare.exception.CommcareConnectionFailureException;
import org.motechproject.commcare.service.CommcareAccountService;
import org.springframework.stereotype.Service;

@Service
public class StubCommcareAccountServiceImpl implements CommcareAccountService {

    @Override
    public boolean verifySettings(CommcareAccountSettings commcareAccountSettings) throws CommcareConnectionFailureException, CommcareAuthenticationException {
        if (!commcareAccountSettings.canMakeConnection()) {
            throw new IllegalArgumentException("Account settings are not valid.");
        }
        if (commcareAccountSettings.getCommcareBaseUrl().equals("fail")) {
            throw new CommcareConnectionFailureException("Motech was unable to connect to CommCareHQ. Please verify the URL is correct.");
        }
        if (commcareAccountSettings.getUsername().equals("fail") && commcareAccountSettings.getPassword().equals("fail")) {
            throw new CommcareAuthenticationException("Motech was unable to authenticate to CommCareHQ. Please verify the username and password.");
        }
        return true;
    }
}
