package org.motechproject.ivr.kookoo;

import org.apache.commons.lang.StringUtils;
import org.motechproject.decisiontree.domain.FlowSessionRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.ivr.model.IVRStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * KooKoo IVR context includes user session information such as caller information, request info, etc.
 */
public class KooKooIVRContext implements IvrContext {
    private KookooRequest kooKooRequest;
    private HttpServletRequest request;

    private static final String CURRENT_DECISION_TREE_POSITION = "current_decision_tree_position";
    public static final String PREFERRED_LANGUAGE_CODE = "preferred_lang_code";
    public static final String CALL_DETAIL_RECORD_ID = "call_detail_record_id";
    public static final String TREE_NAME_KEY = "tree_name";
    public static final String EXTERNAL_ID = "external_id";
    public static final String POUND_SYMBOL = "%23";
    public static final String CALL_ID = "call_id";
    public static final String LIST_OF_COMPLETED_TREES = "list_of_completed_trees";
    public static final String DATA_TO_LOG = "data_to_log";
    private FlowSessionRecord flowSessionRecord;

    protected KooKooIVRContext() {
    }

    public KooKooIVRContext(KookooRequest kooKooRequest, HttpServletRequest request, HttpServletResponse response, FlowSessionRecord flowSessionRecord) {
        this.kooKooRequest = kooKooRequest;
        this.request = request;
        this.flowSessionRecord = flowSessionRecord;
    }

    @Override
    public HttpServletRequest httpRequest() {
        return request;
    }

    /**
     * Get Current user input (dtmf digit pressed on phone)
     *
     * @return return DTMF value as String
     */
    @Override
    public String userInput() {
        return StringUtils.remove(kooKooRequest.getData(), POUND_SYMBOL);
    }

    @Override
    public String currentDecisionTreePath() {
        String currentPosition = flowSessionRecord.get(CURRENT_DECISION_TREE_POSITION);
        return currentPosition == null ? "" : currentPosition;
    }

    @Override
    public void currentDecisionTreePath(String path) {
        addToRequestAndSession(CURRENT_DECISION_TREE_POSITION, path);
    }

    /**
     * Get current call id
     *
     * @return
     */
    @Override
    public String callId() {
        String callId = flowSessionRecord.get(CALL_ID);
        return callId == null ? kooKooRequest.getSid() : callId;
    }

    @Override
    public void callId(String callid) {
        addToRequestAndSession(CALL_ID, callid);
    }

    @Override
    public String preferredLanguage() {
        return flowSessionRecord.get(PREFERRED_LANGUAGE_CODE);
    }

    @Override
    public void preferredLanguage(String languageCode) {
        addToRequestAndSession(PREFERRED_LANGUAGE_CODE, languageCode);
    }

    @Override
    public void callDetailRecordId(String kooKooCallDetailRecordId) {
        addToRequestAndSession(CALL_DETAIL_RECORD_ID, kooKooCallDetailRecordId);
    }

    @Override
    public String callDetailRecordId() {
        return flowSessionRecord.get(CALL_DETAIL_RECORD_ID);
    }

    @Override
    public void treeName(String treeName) {
        request.setAttribute(TREE_NAME_KEY, treeName);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(CallEventConstants.TREE_NAME, treeName);
        dataToLog(hashMap);
    }

    @Override
    public String treeName() {
        return (String) request.getAttribute(TREE_NAME_KEY);
    }

    @Override
    public HashMap<String, String> dataToLog() {
        HashMap<String, String> map = flowSessionRecord.get(DATA_TO_LOG);
        return (map == null) ? new HashMap<String, String>() : map;
    }

    @Override
    public void dataToLog(HashMap<String, String> map) {
        HashMap<String, String> dataMap = flowSessionRecord.get(DATA_TO_LOG);
        if (dataMap == null) {
            flowSessionRecord.set(DATA_TO_LOG, map);
        } else {
            dataMap.putAll(map);
        }
    }

    @Override
    public List<String> getListOfCompletedTrees() {
        return flowSessionRecord.<ArrayList<String>>get(LIST_OF_COMPLETED_TREES);
    }

    @Override
    public void addToListOfCompletedTrees(String lastCompletedTreeName) {
        List<String> listOfCompletedTreesSoFar = flowSessionRecord.<ArrayList<String>>get(LIST_OF_COMPLETED_TREES);
        ArrayList<String> listOfCompletedTrees = listOfCompletedTreesSoFar == null ? new ArrayList<String>() : (ArrayList<String>) listOfCompletedTreesSoFar;
        listOfCompletedTrees.add(lastCompletedTreeName);
        flowSessionRecord.set(LIST_OF_COMPLETED_TREES, listOfCompletedTrees);
    }

    @Override
    public KookooRequest kooKooRequest() {
        return kooKooRequest;
    }

    @Override
    public String externalId() {
        String externalId = flowSessionRecord.get(EXTERNAL_ID);
        return kooKooRequest.externalId() == null ? externalId : kooKooRequest.externalId();
    }

    @Override
    public String ivrEvent() {
        return kooKooRequest.getEvent();
    }

    @Override
    public String callerId() {
        return kooKooRequest.getCid();
    }

    @Override
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

    public boolean isAnswered() {
        return IVRStatus.isAnswered(kooKooRequest.getStatus());
    }

    @Override
    public <T extends Serializable> void addToCallSession(String key, T value) {
        flowSessionRecord.set(key, value);
    }

    @Override
    public <T extends Serializable> T getFromCallSession(String key) {
        return (T) flowSessionRecord.get(key);
    }

    public FlowSessionRecord getFlowSessionRecord() {
        return flowSessionRecord;
    }

    private void addToRequestAndSession(String key, String kooKooCallDetailRecordId) {
        request.setAttribute(key, kooKooCallDetailRecordId);
        flowSessionRecord.set(key, kooKooCallDetailRecordId);
    }
}
