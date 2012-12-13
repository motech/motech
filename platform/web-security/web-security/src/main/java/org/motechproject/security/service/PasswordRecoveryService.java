package org.motechproject.security.service;

import org.motechproject.security.password.InvalidTokenException;
import org.motechproject.security.password.UserNotFoundException;

public interface PasswordRecoveryService {

    void passwordRecoveryRequest(String email) throws UserNotFoundException;

    void resetPassword(String token, String password, String passwordConfirmation) throws InvalidTokenException;

    void cleanUpExpiredRecoveries();

    boolean validateToken(String token);
}
