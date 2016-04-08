package org.motechproject.security.authentication;

import org.motechproject.commons.api.json.MotechJsonMessage;
import org.motechproject.osgi.web.extension.HttpRequestEnvironment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Used to commence a form login authentication. Will redirect
 * if the reqeust wasn't an Ajax request.
 */
public class MotechLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * Performs the redirect (or forward) to the login form URL,
     * unless the request is an Ajax request, where a 401 response will be returned.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        if(HttpRequestEnvironment.isAjax(request)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", "application/json");
            MotechJsonMessage message = new MotechJsonMessage("ERROR");
            response.getWriter().write(message.toJson());
        } else {
            super.commence(request, response, authException);
        }
    }
}
