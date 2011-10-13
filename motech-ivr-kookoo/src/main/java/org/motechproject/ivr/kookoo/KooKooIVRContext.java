package org.motechproject.ivr.kookoo;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class KooKooIVRContext {
    private KookooRequest kooKooRequest;
    private HttpServletRequest request;
    private Cookies cookies;

    private static final String CURRENT_DECISION_TREE_POSITION = "current_decision_tree_position";
    public static final String PREFERRED_LANGUAGE_CODE = "preferred_lang_code";
    private static final String CALL_DETAIL_RECORD_ID = "call_detail_record_id";
    public static final String TREE_NAME_KEY = "tree_name";
    public static final String EXTERNAL_ID = "external_id";
    public static final String POUND_SYMBOL = "%23";
    public static final String CALL_ID = "call_id";

    protected KooKooIVRContext() {
    }

    public KooKooIVRContext(KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response) {
        this.kooKooRequest = kooKooRequest;
        this.request = request;
        cookies = new Cookies(request, response);
    }

    public HttpServletRequest httpRequest() {
        return request;
    }

    public String userInput() {
        return StringUtils.remove(kooKooRequest.getData(), POUND_SYMBOL);
    }

    public String currentTreePosition() {
        String currentPosition = cookies.getValue(CURRENT_DECISION_TREE_POSITION);
        return currentPosition == null ? "" : currentPosition;
    }

    public void currentDecisionTreePath(String path) {
        cookies.add(CURRENT_DECISION_TREE_POSITION, path);
    }

    public String callId() {
        String callId = cookies.getValue(CALL_ID);
        return callId == null ? kooKooRequest.getSid() : callId;
    }

    public void callId(String callid) {
        cookies.add(CALL_ID, callid);
    }

    public String preferredLanguage() {
        return cookies.getValue(PREFERRED_LANGUAGE_CODE);
    }

    public void callDetailRecordId(String kooKooCallDetailRecordId) {
        cookies.add(CALL_DETAIL_RECORD_ID, kooKooCallDetailRecordId);
    }

    public String callDetailRecordId() {
        return cookies.getValue(CALL_DETAIL_RECORD_ID);
    }

    public void treeName(String treeName) {
        request.setAttribute(TREE_NAME_KEY, treeName);
    }

    public String treeName() {
        return (String) request.getAttribute(TREE_NAME_KEY);
    }

    public KookooRequest kooKooRequest() {
        return kooKooRequest;
    }

    public Cookies cookies() {
        return cookies;
    }

    public String externalId() {
        return cookies.getValue(EXTERNAL_ID);
    }

    public void invalidateSession() {
        request.getSession().invalidate();
    }

    public String ivrEvent() {
        return kooKooRequest.getEvent();
    }

    public String callerId() {
        return kooKooRequest.getCid();
    }

    public CallDirection callDirection() {
        return kooKooRequest.getCallDirection();
    }

    public void initialize() {
        callId(kooKooRequest.getSid());
    }

    public void setDefaults() {
        kooKooRequest.setDefaults();
    }

    public String allCookies() {
        return cookies.toString();
    }
}
