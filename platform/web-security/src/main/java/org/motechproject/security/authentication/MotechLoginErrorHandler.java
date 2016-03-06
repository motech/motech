package org.motechproject.security.authentication;

import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.repository.MotechUsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.transaction.annotation.Transactional;

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
public class MotechLoginErrorHandler extends MotechLoginErrorJSONHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechLoginErrorHandler.class);

    @Autowired
    private MotechUsersDao motechUsersDao;

    @Autowired
    private SettingService settingService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String userBlockedUrl;

    public MotechLoginErrorHandler(String defaultFailureUrl, String userBlockedUrl, String changePasswordBaseUrl) {
        super();
        this.userBlockedUrl = userBlockedUrl;

        Map<String, String> failureUrlMap = new HashMap<>();
        failureUrlMap.put(CredentialsExpiredException.class.getName(), changePasswordBaseUrl);
        failureUrlMap.put(BadCredentialsException.class.getName(), defaultFailureUrl);
        failureUrlMap.put(LockedException.class.getName(), userBlockedUrl);

        setExceptionMappings(failureUrlMap);
    }

    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        //Wrong password or username
        if (exception instanceof BadCredentialsException) {
            MotechUser motechUser = motechUsersDao.findByUserName(exception.getAuthentication().getName());
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
                motechUsersDao.update(motechUser);
            }

            if (motechUser != null && !motechUser.isActive()) {
                LOGGER.debug("Redirecting to " + userBlockedUrl);
                redirectStrategy.sendRedirect(request, response, userBlockedUrl);
                return;
            }
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
