package org.motechproject.security.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for keeping track of http sessions. Registered as an HttpSessionListener OSGi service, the felix http
 * proxy will notify this class about session events.
 */
@Service("sessionHandler")
public class SessionHandler implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHandler.class);

    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        addSession(session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        sessions.remove(session.getId());
        LOGGER.debug("Session with id {} destroyed", session.getId());
    }

    public void addSession(HttpSession session) {
        sessions.put(session.getId(), session);
        LOGGER.debug("Session with id {} created", session.getId());
    }

    /**
     * Returns all sessions
     *
     * @return collection with sessions
     */
    public Collection<HttpSession> getAllSessions() {
        return sessions.values();
    }
}
