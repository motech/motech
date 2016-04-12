package org.motechproject.security.authentication;


import org.motechproject.commons.api.json.MotechJsonMessage;
import org.motechproject.osgi.web.extension.HttpRequestEnvironment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class is responsible for checking login error responses,
 * and returning a JSON response rather than a redirect
 * if the response type is Ajax
 */

public class MotechLoginErrorJSONHandler extends ExceptionMappingAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        if(HttpRequestEnvironment.isAjax(request)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", "application/json");

            String message = getMessageForException(exception);
            MotechJsonMessage messageObject = new MotechJsonMessage(message);

            response.getWriter().write(messageObject.toJson());
        }else{
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    private String getMessageForException(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException){
            return "security.wrongPassword";
        }
        if (exception instanceof LockedException){
            return "security.userBlocked";
        }
        return "Error";
    }
}
