package org.motechproject.security.service.impl;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.helper.SessionHandler;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.AuthoritiesService;
import org.motechproject.security.service.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Implementation of the {@link org.motechproject.security.service.UserContextService}
 * APIs to refresh user contexts for users in session. The purpose of this class is making sure that invoking/revoking
 * roles from users will have real-time effect, meaning they won't have to log out for the privilege changes to take
 * effect.
 */
@Service
public class UserContextServiceImpl implements UserContextService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserContextServiceImpl.class);

    private SessionHandler sessionHandler;
    private AllMotechUsers allMotechUsers;
    private AuthoritiesService authoritiesService;

    @Override
    @Transactional
    public void refreshAllUsersContextIfActive() {
        Collection<HttpSession> sessions = sessionHandler.getAllSessions();
        MotechUser user;

        LOGGER.info("Refreshing context for all active users, number of sessions: {}", sessions.size());

        for (HttpSession session : sessions) {
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

            if (context != null) {
                Authentication authentication = context.getAuthentication();
                AbstractAuthenticationToken token;

                User userInSession = (User) authentication.getPrincipal();
                user = allMotechUsers.findByUserName(userInSession.getUsername());

                if (user == null) {
                    LOGGER.warn("User {} has a session, but does not exist", userInSession.getUsername());
                } else {
                    LOGGER.debug("Refreshing context for user {}", user.getUserName());
                    token = getToken(authentication, user);
                    context.setAuthentication(token);
                }
            }
        }

        LOGGER.info("Refreshed context for all active users");
    }

    @Override
    @Transactional
    public void refreshUserContextIfActive(String userName) {
        LOGGER.info("Refreshing context for user: {}", userName);

        MotechUser user = allMotechUsers.findByUserName(userName);
        Collection<HttpSession> sessions = sessionHandler.getAllSessions();

        for (HttpSession session : sessions) {
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

            if (context != null) {
                Authentication authentication = context.getAuthentication();
                AbstractAuthenticationToken token;
                User userInSession = (User) authentication.getPrincipal();
                if (userInSession.getUsername().equals(userName)) {
                    token = getToken(authentication, user);
                    context.setAuthentication(token);
                }
            }
        }
        LOGGER.info("Refreshed context for user: {}", userName);

    }

    @Override
    public void logoutUser(String userName) {
        LOGGER.info("Logging out user: {}", userName);
        Collection<HttpSession> sessions = sessionHandler.getAllSessions();

        for (HttpSession session : sessions) {
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

            if (context != null) {
                Authentication authentication = context.getAuthentication();

                if (userName.equals(authentication.getName())) {
                    session.invalidate();
                }
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

    @Autowired
    public void setSessionHandler(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Autowired
    public void setAllMotechUsers(AllMotechUsers allMotechUsers) {
        this.allMotechUsers = allMotechUsers;
    }

    @Autowired
    public void setAuthoritiesService(AuthoritiesService authoritiesService) {
        this.authoritiesService = authoritiesService;
    }
}
