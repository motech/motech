package org.motechproject.security.service.authentication;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.AuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Extends Spring's @AbstractUserDetailsAuthenticationProvider to provide implementation for the API retrieve user
 * and additional checks on password.
 */
@Component
public class MotechAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    public static final String PLEASE_ENTER_PASSWORD = "Please enter password.";
    public static final String USER_NOT_FOUND = "The username or password you entered is incorrect. Please enter the correct credentials.";
    public static final String USER_BLOCKED = "The user has been blocked. Please contact your local administrator.";

    private AllMotechUsers allMotechUsers;
    private MotechPasswordEncoder passwordEncoder;
    private AuthoritiesService authoritiesService;

    @Autowired
    public MotechAuthenticationProvider(AllMotechUsers allMotechUsers, MotechPasswordEncoder motechPasswordEncoder,
                                        AuthoritiesService authoritiesService) {
        this.allMotechUsers = allMotechUsers;
        this.passwordEncoder = motechPasswordEncoder;
        this.authoritiesService = authoritiesService;
    }

    /**
     * Checks if entered password isn't empty and if it's
     * valid for given user
     *
     * @param userDetails details of user that should be used to validate password
     * @param authentication data used for authentication
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        String password = (String) authentication.getCredentials();
        if (StringUtils.isEmpty(password)) {
            throw new BadCredentialsException(PLEASE_ENTER_PASSWORD);
        }
        if (!passwordEncoder.isPasswordValid(userDetails.getPassword(), password)) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        }
    }

    /**
     * If user with given username exists and is active then
     * authenticates and returns him
     *
     * @param username username of user
     * @param authentication data used for authentication
     * @return authenticated user
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        } else if (!user.isActive()) {
            throw new LockedException(USER_BLOCKED);
        } else {
            authentication.setDetails(new MotechUserProfile(user));
            return new User(user.getUserName(), user.getPassword(), user.isActive(), true, true,
                    !UserStatus.BLOCKED.equals(user.getUserStatus()), authoritiesService.authoritiesFor(user.getUserName()));
        }
    }

}
