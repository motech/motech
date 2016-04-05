package org.motechproject.security.authentication;


import org.motechproject.osgi.web.extension.HttpRequestEnvironment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MotechLoginErrorJSONHandler extends ExceptionMappingAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        if(HttpRequestEnvironment.isAjax(request)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("ERROR");
        }else{
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
