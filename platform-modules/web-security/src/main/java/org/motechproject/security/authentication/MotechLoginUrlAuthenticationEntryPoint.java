package org.motechproject.security.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Used to commence a form login authentication.
 */
public class MotechLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * Performs the redirect (or forward) to the login form URL.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        super.commence(request, response, authException);
    }
}
