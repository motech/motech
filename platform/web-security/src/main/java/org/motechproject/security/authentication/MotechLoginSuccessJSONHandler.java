package org.motechproject.security.authentication;

import org.motechproject.commons.api.json.MotechJsonMessage;
import org.motechproject.osgi.web.extension.HttpRequestEnvironment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class is responsible for returning successful login attempts
 * as JSON objects instead of a redirect if the request was Ajax
 */

public class MotechLoginSuccessJSONHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        if (HttpRequestEnvironment.isAjax(request)) {
            response.setHeader("Content-Type", "application/json");
            MotechJsonMessage message = new MotechJsonMessage("SUCCESS");
            response.getWriter().write(message.toJson());
        }
    }

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if(!HttpRequestEnvironment.isAjax(request)) {
            super.handle(request, response, authentication);
        }
    }
}
