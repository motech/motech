package org.motechproject.decisiontree.service;


import org.motechproject.decisiontree.FlowSession;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to IVR Dial out call. Originates call as per given call request.
 * See implementation module for more configuration information.
 */
public interface FlowSessionService {
    public static final String FLOW_SESSION_ID_PARAM = "flowSessionId";

    public FlowSession getSession(String sessionId);

    public void updateSession(FlowSession flowSession);

    public void removeCallSession(String sessionId);

    public boolean isValidSession(String sessionId);

    public FlowSession updateSessionId(String sessionId, String newSessionId);

    public FlowSession getSession(HttpServletRequest request);
}
