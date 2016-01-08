package org.motechproject.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A logout handler for logging users that log out from MOTECH.
 */
@Component("motechLogoutSuccessHandler")
public class MotechLogoutSuccessHandler implements LogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechLogoutSuccessHandler.class);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        LOGGER.info("User {} logged out", authentication == null ? "not_authenticated" : authentication.getName());
    }
}
