package org.motechproject.security.service;

import org.motechproject.security.model.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface MotechUserService {

    void registerAdminUser(String username, String password, String email, List<String> roles, boolean isActive);

    void register(String username, String password, String email, String externalId, List<String> roles);

    @PreAuthorize("isFullyAuthenticated() and hasRole('addUser')")
    void register(String username, String password, String email, String externalId, List<String> roles, boolean isActive, String openId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('activateUser')")
    void activateUser(String username);

    MotechUserProfile retrieveUserByCredentials(String username, String password);

    MotechUserProfile changePassword(String username, String oldPassword, String newPassword);

    boolean hasUser(String username);

    @PreAuthorize("isFullyAuthenticated() and hasRole('manageUser')")
    List<MotechUserProfile> getUsers();

    @PreAuthorize("isFullyAuthenticated() and hasRole('editUser')")
    UserDto getUser(String userName);

    @PreAuthorize("isFullyAuthenticated() and hasRole('manageUser')")
    List<MotechUserProfile> getOpenIdUsers();

    void updateUser(UserDto user);

    @PreAuthorize("isFullyAuthenticated() and hasRole('deleteUser')")
    void deleteUser(UserDto user);
}
