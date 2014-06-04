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

    /**
     * Creates a new instance of <code>EventLog</code>, with all fields set to null.
     */
    public EventLog() {

    }

    /**
     * Creates a new instance of <code>EventLog</code>, with all fields set to the values
     * specified in the parameters.
     *
     * @param subject  the event subject
     * @param parameters  map of key-value pairs representing the event parameters
     * @param timeStamp  the date/time at which the event occured
     */
    public EventLog(String subject, Map<String, Object> parameters, DateTime timeStamp) {
        this.subject = subject;
        this.parameters = parameters;
        this.timeStamp = timeStamp;
    }

    /**
     * Gets the event subject.
     *
     * @return the event subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the event subject.
     *
     * @param subject  the event subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the event parameters.
     *
     * @return map of key-value pairs representing the event parameters
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the event parameters.
     *
     * @param parameters  map of key-value pairs representing the event parameters
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the event timestamp.
     *
     * @return the date/time at which the event occurred
     */
    public DateTime getTimeStamp() {
        return DateUtil.setTimeZoneUTC(timeStamp);
    }

    /**
     * Sets the event timestamp.
     *
     * @param timeStamp  the date/time at which the event occured
     */
    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}
