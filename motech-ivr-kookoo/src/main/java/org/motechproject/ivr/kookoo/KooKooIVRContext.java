package org.motechproject.ivr.kookoo;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.ivr.model.IVRStatus;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * KooKoo IVR context includes user session information such as caller information, request info, etc.
 */
public class KooKooIVRContext {
    private KookooRequest kooKooRequest;
    private HttpServletRequest request;
    private Cookies cookies;

    private static final String CURRENT_DECISION_TREE_POSITION = "current_decision_tree_position";
    public static final String PREFERRED_LANGUAGE_CODE = "preferred_lang_code";
    public static final String CALL_DETAIL_RECORD_ID = "call_detail_record_id";
    public static final String TREE_NAME_KEY = "tree_name";
    public static final String EXTERNAL_ID = "external_id";
    public static final String POUND_SYMBOL = "%23";
    public static final String CALL_ID = "call_id";
    public static final String LIST_OF_COMPLETED_TREES = "list_of_completed_trees";
    public static final String DATA_TO_LOG = "data_to_log";

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

    /**
     * Get Current user input (dtmf digit pressed on phone)
     * @return return DTMF value as String
     */
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

    /**
     * Get current call id
     * @return
     */
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

    public void preferredLanguage(String languageCode) {
        cookies.add(PREFERRED_LANGUAGE_CODE, languageCode);
    }

    public void callDetailRecordId(String kooKooCallDetailRecordId) {
        cookies.add(CALL_DETAIL_RECORD_ID, kooKooCallDetailRecordId);
    }

    public String callDetailRecordId() {
        return cookies.getValue(CALL_DETAIL_RECORD_ID);
    }

    public void treeName(String treeName) {
        request.setAttribute(TREE_NAME_KEY, treeName);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(CallEventConstants.TREE_NAME, treeName);
        dataToLog(hashMap);
    }

    public String treeName() {
        return (String) request.getAttribute(TREE_NAME_KEY);
    }

    public HashMap<String, String> dataToLog() {
        HashMap<String, String> map = (HashMap<String, String>) request.getSession().getAttribute(DATA_TO_LOG);
        return (map == null) ? new HashMap<String, String>() : map;
    }

    public void dataToLog(HashMap<String, String> map) {
        HashMap<String, String> dataMap = (HashMap<String, String>) request.getSession().getAttribute(DATA_TO_LOG);
        if (dataMap == null) {
            request.getSession().setAttribute(DATA_TO_LOG, map);
        } else {
            dataMap.putAll(map);
        }
    }

    public List<String> getListOfCompletedTrees() {
        return (List<String>) request.getSession().getAttribute(LIST_OF_COMPLETED_TREES);
    }

    public void addToListOfCompletedTrees(String lastCompletedTreeName) {
        Object listOfCompletedTreesSoFar = request.getSession().getAttribute(LIST_OF_COMPLETED_TREES);
        List<String> listOfCompletedTrees = listOfCompletedTreesSoFar == null ? new ArrayList<String>() : (List<String>) listOfCompletedTreesSoFar;
        listOfCompletedTrees.add(lastCompletedTreeName);
        request.getSession().setAttribute(LIST_OF_COMPLETED_TREES, listOfCompletedTrees);
    }

    public KookooRequest kooKooRequest() {
        return kooKooRequest;
    }

    public Cookies cookies() {
        return cookies;
    }

    public String externalId() {
        return kooKooRequest.externalId() == null ? (String) request.getSession().getAttribute(EXTERNAL_ID) : kooKooRequest.externalId();
    }

    public boolean isValidSession() {
        return request.getSession(false) != null;
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
        callDetailRecordId(kooKooRequest.getParameter(CALL_DETAIL_RECORD_ID));
    }

    public void setDefaults() {
        kooKooRequest.setDefaults();
    }

    public String allCookies() {
        return cookies.toString();
    }

    public boolean isAnswered() {
        return IVRStatus.isAnswered(kooKooRequest.getStatus());
    }
}
