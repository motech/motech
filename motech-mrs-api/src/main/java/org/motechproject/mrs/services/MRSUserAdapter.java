package org.motechproject.mrs.services;

import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Interface to handle MRSUser (Staff)
 */
public interface MRSUserAdapter {

    /**
     * Changes the password of the user.
     *
     * @param currentPassword Old password
     * @param newPassword     New password
     * @throws MRSException Thrown when change password fails
     */
    void changeCurrentUserPassword(String currentPassword, String newPassword) throws MRSException;

    /**
     * Creates a new MRSUser
     *
     * @param mrsUser Instance of the User object to be saved
     * @return A Map containing saved user's data
     * @throws UserAlreadyExistsException Thrown if the user already exists
     */
    Map<String, Object> saveUser(MRSUser mrsUser) throws UserAlreadyExistsException;

    /**
     * Resets the password of a given User
     *
     * @param userId User's unique identifier
     * @return New password
     * @throws UsernameNotFoundException If the user is not found.
     */
    String setNewPasswordForUser(String userId) throws UsernameNotFoundException;

    /**
     * Gets all users present in the MRS system
     *
     * @return List of all Users
     */
    List<MRSUser> getAllUsers();

    /**
     * Finds user by UserName
     *
     * @param userId User's unique Identifier
     * @return The User object, if found.
     */
    MRSUser getUserByUserName(String userId);

    /**
     * Updates User attributes.
     *
     * @param mrsUser MRS User object
     * @return A Map containing saved user's data
     */
    Map updateUser(MRSUser mrsUser);
}
