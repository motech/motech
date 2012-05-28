package org.motechproject.ivr.domain;

import com.google.gson.JsonSerializer;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type === 'CallSessionRecord'")
public class CallSessionRecord extends MotechBaseDataObject {

    @JsonProperty
    private Map<String, Object> data;

    @JsonProperty
    private String sessionId;

    CallSessionRecord() {
    }

    public CallSessionRecord(String sessionId) {
        this.sessionId = sessionId;
        data = new HashMap<String, Object>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallSessionRecord that = (CallSessionRecord) o;

        return new EqualsBuilder().append(this.sessionId, that.sessionId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sessionId).toHashCode();
    }

    public <T extends Serializable> void add(String key, T value) {
        data.put(key, value);
    }

    public <T extends Serializable> T valueFor(String key) {
        return (T) data.get(key);
    }
}
