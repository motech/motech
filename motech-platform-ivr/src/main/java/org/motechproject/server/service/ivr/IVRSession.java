package org.motechproject.server.service.ivr;

import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class IVRSession {
    public static class IVRCallAttribute {
        public static final String CALL_STATE = "call_state";
        public static final String CALLER_ID = "caller_id";
        public static final String NUMBER_OF_ATTEMPTS = "number_of_retries";
        public static final String CALL_TIME = "call_time";
        public static final String PREFERRED_LANGUAGE_CODE = "preferred_lang_code";
        public static final String EXTERNAL_ID = "external_id";
        public static final String CURRENT_DECISION_TREE_POSITION = "current_decision_tree_position";
    }

    private HttpSession session;

    public IVRSession(HttpSession session) {
        this.session = session;
    }

    public Object get(String name) {
        return session.getAttribute(name);
    }

    public Integer getInt(String name) {
        return (Integer) session.getAttribute(name);
    }

    public void set(String key, Object value) {
        session.setAttribute(key, value);
    }

    public void renew(HttpServletRequest request) {
        session.invalidate();
        session = request.getSession();
    }

    public void close() {
        if (session != null)
            session.invalidate();
    }

    public boolean isValid() {
        return session != null;
    }

    public void currentDecisionTreePath(String nextCurrentPosition) {
        session.setAttribute(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION, nextCurrentPosition);
    }

    public String currentDecisionTreePath() {
        String currentDecisionTreePosition = (String) session.getAttribute(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION);
        return currentDecisionTreePosition == null ? ""
                : currentDecisionTreePosition;
    }

    public String getPreferredLanguageCode() {
        return (String) session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE);
    }

    public IVRCallState getState() {
        return (IVRCallState) session.getAttribute(IVRCallAttribute.CALL_STATE);
    }

    public boolean isAuthentication() {
        return getState().isCollectPin();
    }

    public void setState(IVRCallState callState) {
        session.setAttribute(IVRCallAttribute.CALL_STATE, callState);
    }

    public DateTime getCallTime() {
        return (DateTime) session.getAttribute(IVRCallAttribute.CALL_TIME);
    }

    public void setCallTime(DateTime value) {
        session.setAttribute(IVRCallAttribute.CALL_TIME, value);
    }

    public String getExternalId() {
        return (String) session.getAttribute(IVRCallAttribute.EXTERNAL_ID);
    }
}
