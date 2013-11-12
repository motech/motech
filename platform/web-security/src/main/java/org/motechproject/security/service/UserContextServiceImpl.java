package org.motechproject.security.service;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.helper.SessionHandler;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Implementation class for @UserContextService.
 * APIs to refresh user contexts for users in session
 */
@Service
public class UserContextServiceImpl implements UserContextService {

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Override
    public void refreshAllUsersContextIfActive() {
        Collection<HttpSession> sessions = sessionHandler.getAllSessions();
        MotechUser user;

        for (HttpSession session : sessions) {
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
            Authentication authentication = context.getAuthentication();
            AbstractAuthenticationToken token;
            User userInSession = (User) authentication.getPrincipal();
            user = allMotechUsers.findByUserName(userInSession.getUsername());
            token = getToken(authentication, user);
            context.setAuthentication(token);
        }

    }

    @Override
    public void refreshUserContextIfActive(String userName) {
        MotechUser user = allMotechUsers.findByUserName(userName);
        Collection<HttpSession> sessions = sessionHandler.getAllSessions();

        for (HttpSession session : sessions) {
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
            Authentication authentication = context.getAuthentication();
            AbstractAuthenticationToken token;
            User userInSession = (User) authentication.getPrincipal();
            if (userInSession.getUsername().equals(userName)) {
                token = getToken(authentication, user);
                context.setAuthentication(token);
            }
        }

    }

    private AbstractAuthenticationToken getToken(Authentication authentication, MotechUser user) {
        AbstractAuthenticationToken token = null;
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken oldToken = (UsernamePasswordAuthenticationToken) authentication;
            token = new UsernamePasswordAuthenticationToken(oldToken.getPrincipal(),
                    oldToken.getCredentials(), authoritiesService.authoritiesFor(user));

        } else if (authentication instanceof OpenIDAuthenticationToken) {
            OpenIDAuthenticationToken oldToken = (OpenIDAuthenticationToken) authentication;
            token = new OpenIDAuthenticationToken(oldToken.getPrincipal(), authoritiesService.authoritiesFor(user),
                    user.getOpenId(), oldToken.getAttributes());
        }
        return token;
    }
}
