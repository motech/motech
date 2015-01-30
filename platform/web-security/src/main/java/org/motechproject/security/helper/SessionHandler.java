package org.motechproject.security.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for handling session
 */
public class SessionHandler {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    /**
     * Adds session from request to the map
     *
     * @param request with session
     */
    public void addSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SESSIONS.put(session.getId(), session);
    }

    /**
     * Removes session from given request from map
     *
     * @param request with session
     */
    public void removeSession(HttpServletRequest request) {
        SESSIONS.remove(request.getSession().getId());
    }

    /**
     * Returns all sessions
     *
     * @return collection with sessions
     */
    public Collection<HttpSession> getAllSessions() {
        return SESSIONS.values();
    }
}
