package org.motechproject.security.authentication;

import org.motechproject.security.helper.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class responsible for handling sessions in case
 * of authentication success
 */
public class MotechSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private SessionHandler sessionHandler;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        sessionHandler.addSession(request);
    }

    @Autowired
    public void setSessionHandler(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }
}
