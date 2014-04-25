package org.motechproject.eventlogging.domain;


import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.annotations.Entity;

import java.util.Map;

/**
 * The <code>EventLog</code> class represents a single, logged event record,
 * that is persisted in the database.
 */
@Entity
public class EventLog {

    private String subject;
    private Map<String, Object> parameters;
    private DateTime timeStamp;

    public EventLog() {

    }

    public EventLog(String subject, Map<String, Object> parameters, DateTime timeStamp) {
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
        return DateUtil.setTimeZoneUTC(timeStamp);
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}
