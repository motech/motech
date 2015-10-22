package org.motechproject.security.service.authentication;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Days;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.security.authentication.MotechPasswordEncoder;
import org.motechproject.security.config.SettingService;
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
    private SettingService settingService;

    @Autowired
    public MotechAuthenticationProvider(AllMotechUsers allMotechUsers, MotechPasswordEncoder motechPasswordEncoder,
                                        AuthoritiesService authoritiesService, SettingService settingService) {
        this.allMotechUsers = allMotechUsers;
        this.passwordEncoder = motechPasswordEncoder;
        this.authoritiesService = authoritiesService;
        this.settingService = settingService;
    }

    /**
     * Checks if entered password isn't empty and if it's valid for given user.
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
     * If user with given username exists and is active then authenticates and returns him. Updates the status of the
     * user when password has been expired.
     *
     * @param username username of user
     * @param authentication data used for authentication
     * @return the user information
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        MotechUser user = allMotechUsers.findByUserName(username);
        if (user == null) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        } else if (!user.isActive()) {
            throw new LockedException(USER_BLOCKED);
        } else {
            if (settingService.getNumberOfDaysToChangePassword() > 0 &&
                    Days.daysBetween(user.getSafeLastPasswordChange(), DateUtil.now()).getDays() >= settingService.getNumberOfDaysToChangePassword()) {
                user.setUserStatus(UserStatus.MUST_CHANGE_PASSWORD);
                allMotechUsers.update(user);
            }
            authentication.setDetails(new MotechUserProfile(user));
            return new User(user.getUserName(), user.getPassword(), user.isActive(), true, !UserStatus.MUST_CHANGE_PASSWORD.equals(user.getUserStatus()),
                    !UserStatus.BLOCKED.equals(user.getUserStatus()), authoritiesService.authoritiesFor(user));
        }
    }

}
