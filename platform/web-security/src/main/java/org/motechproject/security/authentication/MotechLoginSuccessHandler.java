package org.motechproject.security.authentication;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.helper.SessionHandler;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.repository.MotechUsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Class responsible for logging info about users that log in and for resetting their failure login counter.
 * Extends {@link SavedRequestAwareAuthenticationSuccessHandler}. It also serves as a fallback for storing
 * sessions that started with the server before web-security was started.
 * @see org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
 */
public class MotechLoginSuccessHandler extends MotechLoginSuccessJSONHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechLoginSuccessHandler.class);

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private SettingService settingService;

    @Autowired
    private MotechUsersDao motechUsersDao;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);

        LOGGER.info("User {} logged in", authentication.getName());
        LOGGER.debug("Authorities for {}: {}", authentication.getName(), authentication.getAuthorities());

        MotechUser motechUser = motechUsersDao.findByUserName(authentication.getName());
        motechUser.setFailureLoginCounter(0);
        motechUsersDao.update(motechUser);

        HttpSession session = request.getSession();
        // set session timeout
        session.setMaxInactiveInterval(settingService.getSessionTimeout());

        // this is a fallback for sessions started before web-security started, i.e. on the Bootstrap screen
        sessionHandler.addSession(session);
    }
}
