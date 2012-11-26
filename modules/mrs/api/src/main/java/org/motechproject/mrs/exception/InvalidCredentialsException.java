/**
 * \ingroup mrs
 * Exceptions thrown by MRS
 */
package org.motechproject.mrs.exception;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * Thrown when Invalid Credentials are provided during login into MRS system
 */
public class InvalidCredentialsException extends BadCredentialsException {
    public InvalidCredentialsException(Throwable cause) {
        super("Invalid username or password supplied.", cause);
    }
}
