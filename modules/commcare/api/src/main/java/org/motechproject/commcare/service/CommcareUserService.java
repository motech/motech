package org.motechproject.commcare.service;

import org.motechproject.commcare.domain.CommcareUser;

import java.util.List;

/**
 * A service to perform queries against CommCareHQ's user APIs. CommCareHQ
 * exposes one user API, therefore getting a user by Id simply filters the list
 * of all users returned by CommCareHQ.
 */
public interface CommcareUserService {

    /**
     * Queries CommCareHQ for a list of all users on the configured domain.
     * @return A list of CommcareUsers that represent the information about each user from CommCareHQ
     */
    List<CommcareUser> getAllUsers();

    /**
     * Queries CommCareHQ for a list of all users on the configured domain and filters the list to return the user that matches the provided id.
     * @param id The id of the user to retrieve.
     * @return A CommcareUser object representing the information about the user from CommCareHQ, or null if that user did not exist on that domain.
     */
    CommcareUser getCommcareUserById(String id);
}
