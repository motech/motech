package org.motechproject.commcare.events;

import java.util.HashMap;
import java.util.Map;
import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.scheduler.domain.MotechEvent;

/**
 * Wrapper class for convenience when a MotechEvent representing a CaseEvent is
 * received.
 */
public class CaseEvent {
    private String caseId;
    private String userId;
    private String apiKey;
    private String dateModified;
    private String action;
    private Map<String, String> fieldValues;
    private String caseType;
    private String caseName;
    private String ownerId;

    public CaseEvent(String caseId) {
        this.caseId = caseId;
    }

    public CaseEvent(MotechEvent event) {
        this.caseId = ((String) event.getParameters().get("caseId"));
        this.userId = ((String) event.getParameters().get("userId"));
        this.apiKey = ((String) event.getParameters().get("apiKey"));
        this.dateModified = ((String) event.getParameters().get("dateModified"));
        this.action = ((String) event.getParameters().get("caseAction"));
        this.fieldValues = ((Map<String, String>) event.getParameters().get(
                "fieldValues"));
        this.caseType = ((String) event.getParameters().get("caseType"));
        this.caseName = ((String) event.getParameters().get("caseName"));
        this.ownerId = ((String) event.getParameters().get("ownerId"));
    }

    public String getCaseId() {
        return this.caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getFieldValues() {
        return this.fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public String getCaseType() {
        return this.caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseName() {
        return this.caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public MotechEvent toMotechEventWithoutData() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("caseId", this.caseId);
        return new MotechEvent(EventSubjects.CASE_EVENT, parameters);
    }

    public MotechEvent toMotechEventWithData() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("caseId", this.caseId);
        parameters.put("userId", this.userId);
        parameters.put("caseAction", this.action);
        parameters.put("apiKey", this.apiKey);
        parameters.put("caseName", this.caseName);
        parameters.put("caseType", this.caseType);
        parameters.put("dateModified", this.dateModified);
        parameters.put("ownerId", this.ownerId);
        return new MotechEvent(EventSubjects.CASE_EVENT, parameters);
    }

    public CaseEvent eventFromCase(CaseXml caseInstance) {
        setCaseId(caseInstance.getCase_id());
        setUserId(caseInstance.getUser_id());
        setAction(caseInstance.getAction());
        setApiKey(caseInstance.getApi_key());
        setCaseName(caseInstance.getCase_name());
        setCaseType(caseInstance.getCase_type());
        setDateModified(caseInstance.getDate_modified());
        setFieldValues(caseInstance.getFieldValues());
        setOwnerId(caseInstance.getOwner_id());

        return this;
    }
}
