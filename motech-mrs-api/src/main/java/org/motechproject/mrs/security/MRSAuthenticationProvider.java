/**
 * \ingroup MRS
 * Authentication Handler package
 */
package org.motechproject.mrs.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Authenticates MRS login. Uses Spring Security for authentication.
 */
public abstract class MRSAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * Method Stub to add additional authentication checks for a user
     *
     * @param userDetails User's login details
     * @param usernamePasswordAuthenticationToken Authentication Token for username & password
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    }

    /**
     * Retrieves User details on successful authentication
     *
     * @param userName       Login username
     * @param authentication Login credentials
     * @return Security Details of the user
     */
    @Override
    protected abstract MRSSecurityUser retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication);
}
