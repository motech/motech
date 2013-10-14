package org.motechproject.security.authentication;

import org.motechproject.security.helper.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A logout handler for removing Motech user
 * sessions from Motech's internally kept session handler.
 * This is invoked when a user logs out.
 */
@Component
public class MotechLogoutSuccessHandler implements LogoutHandler {

    @Autowired
    private SessionHandler sessionHandler;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        sessionHandler.removeSession(request);
    }

}
