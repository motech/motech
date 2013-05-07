package org.motechproject.commcare.service;

import org.motechproject.commcare.domain.CommcareAccountSettings;
import org.motechproject.commcare.exception.CommcareAuthenticationException;
import org.motechproject.commcare.exception.CommcareConnectionFailureException;

/**
 * A service to perform queries against CommCareHQ's connection APIs.
 */
public interface CommcareAccountService {

    /**
     * Verifies CommCareHQ connection
     * @return true if connection was successful
     * @throws IllegalArgumentException if one or more parameters are empty
     * @throws CommcareConnectionFailureException if connection to CommCareHQ count not be made
     * @throws CommcareAuthenticationException if the username and password are not valid
     */
    boolean verifySettings(CommcareAccountSettings commcareAccountSettings) throws CommcareConnectionFailureException, CommcareAuthenticationException;
}
