package org.motechproject.ivr.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

/**
 * Call Detail Record represents call events and data captured in a call along with call metrics.
 */
@TypeDiscriminator("doc.type === 'CallDetailRecord'")
public class CallDetailRecord extends MotechBaseDataObject implements CallDetail {

    private DateTime startDate;
    private DateTime endDate;
    private Date answerDate;
    private CallDisposition disposition;
    private String errorMessage;
    private String phoneNumber;
    private String callId;
    private Integer duration;
    private CallDirection callDirection;

    private List<CallEventLog> callEventLogs = new ArrayList<CallEventLog>();

    private Map<String, Object> customProperties = new HashMap<>();

    private CallDetailRecord() {
    }

    public CallDetailRecord(String callId, String phoneNumber) {
        this.callId = callId;
        this.phoneNumber = phoneNumber;
        this.startDate = now();
    }

    /**
     * Constructor to create CallDetailRecord
     *
     * @param startDate
     * @param endDate
     * @param answerDate
     * @param disposition
     * @param duration
     */
    public CallDetailRecord(Date startDate, Date endDate, Date answerDate, CallDisposition disposition, Integer duration) {
        this.startDate = startDate != null ? newDateTime(startDate) : null;
        this.endDate = endDate != null ? newDateTime(endDate) : null;
        this.answerDate = answerDate;
        this.disposition = disposition;
        this.duration = duration;
    }

    /**
     * CallDetailRecord constructor for failed calls
     *
     * @param disposition: Status of call
     * @param errorMessage
     */
    public CallDetailRecord(CallDisposition disposition, String errorMessage) {
        this.errorMessage = errorMessage;
        this.disposition = disposition;
    }

    /**
     * Creates a call details record for given phone number and call details
     *
     * @param phoneNumber:   phone number of user.
     * @param callDirection: Incoming/outgoing
     * @param disposition:   Call status (busy, failed etc)
     * @return
     */
    public static CallDetailRecord create(String phoneNumber, CallDirection callDirection, CallDisposition disposition) {
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.startDate = now();
        callDetailRecord.disposition = disposition;
        callDetailRecord.answerDate = callDetailRecord.startDate.toDate();
        callDetailRecord.phoneNumber = phoneNumber;
        callDetailRecord.callDirection = callDirection;
        return callDetailRecord;
    }

    public String getCallId() {
        return callId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public DateTime getStartDate() {
        return startDate != null ? setTimeZone(startDate) : startDate;
    }

    public DateTime getEndDate() {
        return endDate != null ? setTimeZone(endDate) : endDate;
    }

    public Date getAnswerDate() {
        return answerDate != null ? setTimeZone(newDateTime(answerDate)).toDate() : answerDate;
    }

    public CallDisposition getDisposition() {
        return disposition;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public CallDirection getCallDirection() {
        return callDirection;
    }

    public List<CallEventLog> getCallEvents() {
        return callEventLogs;
    }

    public CallDetailRecord setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CallDetailRecord setCallEvents(List<CallEventLog> callEventLogs) {
        this.callEventLogs = callEventLogs;
        return this;
    }

    public CallDetailRecord setCallDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
        return this;
    }

    public CallDetailRecord setStartDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public CallDetailRecord setEndDate(DateTime endDate) {
        this.endDate = endDate;
        duration = new Period(startDate, endDate).toStandardSeconds().getSeconds();
        return this;
    }

    public CallDetailRecord setAnswerDate(Date answerDate) {
        this.answerDate = answerDate;
        return this;
    }

    public CallDetailRecord setDisposition(CallDisposition disposition) {
        this.disposition = disposition;
        return this;
    }

    public CallDetailRecord setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public CallDetailRecord setCustomProperties(Map<String, Object> customProperties) {
        if (customProperties != null) {
            this.customProperties = customProperties;
        }
        return this;
    }

    public CallDetailRecord addCustomProperty(String key, Object value) {
        this.customProperties.put(key, value);
        return this;
    }

    public CallDetailRecord setCallId(String callId) {
        this.callId = callId;
        return this;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Adds IVR events such as key press, hangup etc., to current call detail record.
     *
     * @param callEventLog
     */
    public CallDetailRecord addCallEvent(CallEventLog callEventLog) {
        callEventLogs.add(callEventLog);
        return this;
    }

    /**
     * Get last call event for current call.
     *
     * @return Call event
     */
    public CallEventLog lastCallEvent() {
        return callEventLogs.size() == 0 ? null : callEventLogs.get(callEventLogs.size() - 1);
    }

}
