package org.motechproject.security.authentication;

import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.repository.AllMotechUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for increasing user failure login counter. Extends {@link SimpleUrlAuthenticationFailureHandler}.
 * It also redirect user to error login page.
 * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 */
public class MotechLoginErrorHandler extends ExceptionMappingAuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechLoginErrorHandler.class);

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Autowired
    private SettingService settingService;

    @Autowired
    private RedirectStrategy redirectStrategy;

    private String userBlockedUrl;

    private String changePasswordBaseUrl;

    public MotechLoginErrorHandler(String defaultFailureUrl, String userBlockedUrl, String changePasswordBaseUrl) {
        super();
        this.userBlockedUrl = userBlockedUrl;
        this.changePasswordBaseUrl = changePasswordBaseUrl;

        Map<String, String> failureUrlMap = new HashMap<String, String>();
        failureUrlMap.put(CredentialsExpiredException.class.getName(), changePasswordBaseUrl);
        failureUrlMap.put(BadCredentialsException.class.getName(), defaultFailureUrl);
        failureUrlMap.put(LockedException.class.getName(), userBlockedUrl);

        setExceptionMappings(failureUrlMap);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        //Wrong password or username
        if (exception instanceof BadCredentialsException) {
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
                return;
            }
        }

        if (exception instanceof CredentialsExpiredException) {
            StringBuilder sb = new StringBuilder();
            sb.append(changePasswordBaseUrl).append("?user=").append(exception.getAuthentication().getName());
            String changePasswordUrl = sb.toString();
            LOGGER.debug("User {} must change password. Redirecting to {}", exception.getAuthentication().getName(), changePasswordUrl);
            redirectStrategy.sendRedirect(request, response, changePasswordUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
