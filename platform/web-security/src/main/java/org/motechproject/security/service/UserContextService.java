package org.motechproject.security.service;

/**
 * Interface to refresh user context (all or specified username)
 */
public interface UserContextService {

    /**
     * Refreshes context of all users as long as they're active
     */
    void refreshAllUsersContextIfActive();

    /**
     * Refreshes context of user with given name
     *
     * @param userName name of user
     */
    void refreshUserContextIfActive(String userName);
}
