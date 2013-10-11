package org.motechproject.security.helper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionHandler {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    public void addSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SESSIONS.put(session.getId(), session);
    }

    public void removeSession(HttpServletRequest request) {
         SESSIONS.remove(request.getSession().getId());
    }

    public Collection<HttpSession> getAllSessions() {
        return SESSIONS.values();
    }
}
