package org.motechproject.callflow.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.ivr.domain.CallDetailRecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'FlowSessionRecord'")
public class FlowSessionRecord extends MotechBaseDataObject implements FlowSession {

    @JsonProperty
    private CallDetailRecord callDetailRecord;
    @JsonProperty
    private String language;
    @JsonProperty
    private Map<String, Object> data;
    @JsonProperty
    private Node currentNode;

    private FlowSessionRecord() {
    }

    public FlowSessionRecord(String sessionId, String phoneNumber) {
        this.callDetailRecord = new CallDetailRecord(sessionId, phoneNumber);
        this.callDetailRecord.setCallId(sessionId);
        data = new HashMap<>();
    }

    @Override
    public String getSessionId() {
        return callDetailRecord.getCallId();
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    @JsonIgnore
    public String getPhoneNumber() {
        return callDetailRecord.getPhoneNumber();
    }

    @Override
    public <T extends Serializable> void set(String key, T value) {
        data.put(key, value);
    }

    @Override
    public <T extends Serializable> T get(String key) {
        return (T) data.get(key);
    }

    @Override
    public Node getCurrentNode() {
        return currentNode;
    }

    @Override
    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public CallDetailRecord getCallDetailRecord() {
        return callDetailRecord;
    }

    public void setCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
    }

    @JsonIgnore
    public void setSessionId(String sessionId) {
        callDetailRecord.setCallId(sessionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FlowSessionRecord that = (FlowSessionRecord) o;

        return new EqualsBuilder().append(getSessionId(), that.getSessionId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getSessionId()).toHashCode();
    }
}
