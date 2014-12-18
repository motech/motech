package org.motechproject.security.service;

import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.model.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Locale;

/**
 * Service interface that defines APIs to retrieve and manage user details
 */
public interface MotechUserService {

    @PreAuthorize("hasRole('addUser')")
    void register(String username, String password, String email, String externalId, List<String> roles, Locale locale);

    @PreAuthorize("hasRole('addUser')")
    void register(String username, String password, String email, // NO CHECKSTYLE More than 7 parameters (found 8).
                  String externalId, List<String> roles, Locale locale, boolean isActive, String openId);

    /**
     * A method that allows to register the first MOTECH Admin in the application. Throws {@link java.lang.IllegalStateException}
     * when an active Admin User is already registered.
     *
     * @param username Username of a new user
     * @param password Password of a new user
     * @param email Email address of a new user
     * @param locale Selected locale for the new user
     */
    void registerMotechAdmin(String username, String password, String email, Locale locale);

    @PreAuthorize("hasRole('activateUser')")
    void activateUser(String username);

    @PreAuthorize("hasRole('viewUser')")
    MotechUserProfile retrieveUserByCredentials(String username, String password);

    /**
     * Allows to change a password of a currently logged-in user.
     *
     * @param oldPassword An old password of currently logged user
     * @param newPassword A new password for the currently logged user
     * @return MotechUserProfile with updated user information
     */
    MotechUserProfile changePassword(String oldPassword, String newPassword);

    /**
     * Changes the e-mail address of a currenty logged user
     *
     * @param email a new e-mail address
     */
    void changeEmail(String email);

    @PreAuthorize("hasRole('manageUser')")
    MotechUserProfile changePassword(String userName, String oldPassword, String newPassword);

    boolean hasUser(String username);

    boolean hasEmail(String email);

    @PreAuthorize("hasAnyRole('manageUser', 'viewUser')")
    List<MotechUserProfile> getUsers();

    @PreAuthorize("hasRole('editUser')")
    UserDto getUser(String userName);

    @PreAuthorize("hasRole('editUser')")
    UserDto getUserByEmail(String email);

    UserDto getCurrentUser();

    Locale getLocale(String userName);

    @PreAuthorize("hasRole('manageUser')")
    List<MotechUserProfile> getOpenIdUsers();

    @PreAuthorize("hasAnyRole('manageUser', 'editUser')")
    void updateUserDetailsWithoutPassword(UserDto user);

    @PreAuthorize("hasAnyRole('manageUser', 'editUser')")
    void updateUserDetailsWithPassword(UserDto user);

    @PreAuthorize("hasRole('deleteUser')")
    void deleteUser(UserDto user);

    void sendLoginInformation(String userName, String password);

    void setLocale(Locale locale);

    List<String> getRoles(String userName);

    boolean hasActiveMotechAdmin();
}
