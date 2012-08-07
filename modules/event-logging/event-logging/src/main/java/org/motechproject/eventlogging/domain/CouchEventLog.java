package org.motechproject.eventlogging.domain;

import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CouchEventLog'")
public class CouchEventLog extends MotechBaseDataObject {

    @JsonProperty
    private String subject;
    @JsonProperty
    private Map<String, Object> parameters;
    @JsonProperty
    private DateTime timeStamp;

    public CouchEventLog() {

    }

    public CouchEventLog(String subject, Map<String, Object> parameters, DateTime timeStamp) {
        this.subject = subject;
        this.parameters = parameters;
        this.timeStamp = timeStamp;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
