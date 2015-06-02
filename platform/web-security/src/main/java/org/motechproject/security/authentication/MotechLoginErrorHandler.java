package org.motechproject.security.authentication;

import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.repository.AllMotechUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class responsible for increasing user failure login counter. Extends {@link SimpleUrlAuthenticationFailureHandler}.
 * It also redirect user to error login page.
 * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 */
public class MotechLoginErrorHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechLoginErrorHandler.class);

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Autowired
    private SettingService settingService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String userBlockedUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        MotechUser motechUser = allMotechUsers.findByUserName(exception.getAuthentication().getName());
        int failureLoginLimit = settingService.getFailureLoginLimit();
        if (motechUser != null && failureLoginLimit > 0) {
            int failureLoginCounter = motechUser.getFailureLoginCounter();
            failureLoginCounter++;
            if (failureLoginCounter > failureLoginLimit && motechUser.isActive()) {
                motechUser.setUserStatus(UserStatus.BLOCKED);
                failureLoginCounter = 0;
                LOGGER.debug("User {} has been blocked", motechUser.getUserName());
            }
            motechUser.setFailureLoginCounter(failureLoginCounter);
            allMotechUsers.update(motechUser);
        }
        if (motechUser != null && !motechUser.isActive()) {
            LOGGER.debug("Redirecting to " + userBlockedUrl);
            redirectStrategy.sendRedirect(request, response, userBlockedUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }


    /**
     * Sets the URL which will be used as the user blocked destination.
     *
     * @param userBlockedUrl the user blocked URL.
     */
    public void setUserBlockedUrl(String userBlockedUrl) {
        this.userBlockedUrl = userBlockedUrl;
    }
}
