package org.motechproject.decisiontree.server.service;


import org.motechproject.decisiontree.core.FlowSession;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to IVR Dial out call. Originates call as per given call request.
 * See implementation module for more configuration information.
 */
public interface FlowSessionService {

    String FLOW_SESSION_ID_PARAM = "flowSessionId";

    FlowSession getSession(String sessionId);

    FlowSession createSession(String sessionId, String phoneNumber);

    void updateSession(FlowSession flowSession);

    void removeCallSession(String sessionId);

    boolean isValidSession(String sessionId);

    FlowSession updateSessionId(String sessionId, String newSessionId);

    FlowSession getSession(HttpServletRequest request, String language, String phoneNumber);
}
