package org.motechproject.security.service.authentication;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.AuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    public static final String USER_NOT_ACTIVATED = "The user has been registered but not activated. Please contact your local administrator.";

    private AllMotechUsers allMotechUsers;
    private MotechPasswordEncoder passwordEncoder;
    private AuthoritiesService authoritiesService;

    @Autowired
    public MotechAuthenticationProvider(AllMotechUsers allMotechUsers, MotechPasswordEncoder motechPasswordEncoder, AuthoritiesService authoritiesService) {
        this.allMotechUsers = allMotechUsers;
        this.passwordEncoder = motechPasswordEncoder;
        this.authoritiesService = authoritiesService;
    }

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

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        } else if (!user.isActive()) {
            throw new BadCredentialsException(USER_NOT_ACTIVATED);
        } else {
            authentication.setDetails(new MotechUserProfile(user));
            return new User(user.getUserName(), user.getPassword(), user.isActive(), true, true, true, authoritiesService.authoritiesFor(user));
        }
    }

}
