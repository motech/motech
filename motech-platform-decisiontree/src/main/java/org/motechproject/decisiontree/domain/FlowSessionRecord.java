package org.motechproject.decisiontree.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.model.MotechBaseDataObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'FlowSessionRecord'")
public class FlowSessionRecord extends MotechBaseDataObject implements FlowSession {

    @JsonProperty
    private Map<String, Object> data;

    @JsonProperty
    private String sessionId;

    FlowSessionRecord() {
    }

    public FlowSessionRecord(String sessionId) {
        this.sessionId = sessionId;
        data = new HashMap<String, Object>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowSessionRecord that = (FlowSessionRecord) o;

        return new EqualsBuilder().append(this.sessionId, that.sessionId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sessionId).toHashCode();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public <T extends Serializable> void set(String key, T value) {
        data.put(key, value);
    }

    public <T extends Serializable> T get(String key) {
        return (T) data.get(key);
    }
}
