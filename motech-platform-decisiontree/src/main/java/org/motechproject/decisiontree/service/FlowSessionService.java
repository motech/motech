package org.motechproject.decisiontree.service;


import org.motechproject.decisiontree.FlowSession;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to IVR Dial out call. Originates call as per given call request.
 * See implementation module for more configuration information.
 */
public interface FlowSessionService {
    String FLOW_SESSION_ID_PARAM = "flowSessionId";

    FlowSession getSession(String sessionId);

    void updateSession(FlowSession flowSession);

    void removeCallSession(String sessionId);

    boolean isValidSession(String sessionId);

    FlowSession updateSessionId(String sessionId, String newSessionId);

    FlowSession getSession(HttpServletRequest request);
}
