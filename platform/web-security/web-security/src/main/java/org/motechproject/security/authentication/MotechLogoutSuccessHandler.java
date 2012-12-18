package org.motechproject.security.authentication;

import org.motechproject.security.helper.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MotechLogoutSuccessHandler implements LogoutHandler {

    @Autowired
    private SessionHandler sessionHandler;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        sessionHandler.removeSession(request);
    }

}
