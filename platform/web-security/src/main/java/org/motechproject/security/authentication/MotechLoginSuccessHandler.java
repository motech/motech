package org.motechproject.security.authentication;

import org.motechproject.security.helper.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class responsible for logging info about users that log in. Extends {@link SavedRequestAwareAuthenticationSuccessHandler}.
 * It also serves as a fallback for storing sessions that started with the server before web-security was started.
 * @see org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
 */
public class MotechLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechLoginSuccessHandler.class);

    @Autowired
    private SessionHandler sessionHandler;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        LOGGER.info("User {} logged in", authentication.getName());
        LOGGER.debug("Authorities for {}: {}", authentication.getName(), authentication.getAuthorities());

        // this is a fallback for sessions started before web-security started, i.e. on the Bootstrap screen
        sessionHandler.addSession(request.getSession());
    }
}
