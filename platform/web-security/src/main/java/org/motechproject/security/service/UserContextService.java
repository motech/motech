package org.motechproject.security.service;

/**
 * Interface to manage user contexts (sessions)
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

    /**
     * Finds and invalidates session of the user with the given userName. When the
     * session is invalidated, the user will need to log in again.
     *
     * @param userName username of the user to invalidate session for
     */
    void logoutUser(String userName);
}
