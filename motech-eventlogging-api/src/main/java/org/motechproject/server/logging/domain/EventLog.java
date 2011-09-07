package org.motechproject.server.logging.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Map;

@TypeDiscriminator("doc.documentType == 'EventLog'")
public class EventLog extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "EventLog";

    private String id;
    private String externalId;
    private String logType;
    private String name;
    private String description;
    private DateTime dateTime;
    private Map<String, String> data;

    public EventLog() {
    }

    public EventLog(String externalId, String logType, String name, String description, DateTime dateTime, Map<String, String> data) {
        this.externalId = externalId;
        this.logType = logType;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}