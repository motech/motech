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

    void register(String username, String password, String email, String externalId, List<String> roles, Locale locale);

    @PreAuthorize("hasRole('addUser')")
    void register(String username, String password, String email, String externalId, List<String> roles, Locale locale, boolean isActive, String openId);

    @PreAuthorize("hasRole('activateUser')")
    void activateUser(String username);

    MotechUserProfile retrieveUserByCredentials(String username, String password);

    MotechUserProfile changePassword(String username, String oldPassword, String newPassword);

    boolean hasUser(String username);

    @PreAuthorize("hasAnyRole('manageUser', 'viewUser')")
    List<MotechUserProfile> getUsers();

    @PreAuthorize("hasRole('editUser')")
    UserDto getUser(String userName);

    UserDto getUserByEmail(String email);

    UserDto getCurrentUser();

    Locale getLocale(String userName);

    @PreAuthorize("hasRole('manageUser')")
    List<MotechUserProfile> getOpenIdUsers();

    void updateUserDetailsWithoutPassword(UserDto user);

    void updateUserDetailsWithPassword(UserDto user);

    @PreAuthorize("hasRole('deleteUser')")
    void deleteUser(UserDto user);

    void sendLoginInformation(String userName, String password);

    void setLocale(String userName, Locale locale);

    List<String> getRoles(String userName);

    boolean hasActiveAdminUser();
}
