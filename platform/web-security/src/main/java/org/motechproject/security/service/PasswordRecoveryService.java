package org.motechproject.security.service;

import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.ex.NonAdminUserException;
import org.motechproject.security.ex.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Service that defines APIs to manage password recovery
 */
public interface PasswordRecoveryService {

    /**
     * Sends password recovery email to user with given email address
     *
     * @param email address of user
     * @throws UserNotFoundException when no user for given email exists
     */
    void passwordRecoveryRequest(String email) throws UserNotFoundException;

    /**
     * Sets new password for user from token
     *
     * @param token for {@link org.motechproject.security.domain.PasswordRecovery}
     * @param password to be set for user
     * @param passwordConfirmation to check is password is correct
     * @throws InvalidTokenException when {@link org.motechproject.security.domain.PasswordRecovery}
     * as a null, recovery is already expired
     * or when user for name from token doesn't exists
     */
    void resetPassword(String token, String password, String passwordConfirmation) throws InvalidTokenException;

    /**
     * Removes all expired recoveries
     */
    void cleanUpExpiredRecoveries();

    /**
     * Checks if there's a not expired {@link org.motechproject.security.domain.PasswordRecovery}
     * for given token
     *
     * @param token to validate
     * @return true if recovery exists, otherwise false
     */
    boolean validateToken(String token);

    /**
     * Sends one time token for OpenId to user with given email
     *
     * @param email of user
     * @throws UserNotFoundException when no user for given email exists
     * @throws NonAdminUserException when user for given email is not an
     * admin (don't have Admin role)
     */
    void oneTimeTokenOpenId(String email) throws UserNotFoundException, NonAdminUserException;

    /**
     * Creates new openId Token for user from token as long as there's a
     * {@link org.motechproject.security.domain.PasswordRecovery} for that token
     * and redirect to home page. If there's no such recovery then redirect to login page
     *
     * @param token for password recovery
     * @param request for session
     * @param response for session
     * @throws IOException when response cannot redirect to given URL (home or login page)
     */
    void validateTokenAndLoginUser(String token, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
