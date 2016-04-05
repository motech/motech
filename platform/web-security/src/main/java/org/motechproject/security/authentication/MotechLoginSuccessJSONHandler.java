package org.motechproject.security.authentication;

import org.motechproject.osgi.web.extension.HttpRequestEnvironment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MotechLoginSuccessJSONHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (HttpRequestEnvironment.isAjax(request)) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            response.getWriter().write("SUCCESS");
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
