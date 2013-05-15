package org.motechproject.ivr.calllog.domain;

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
public class CallRecord extends MotechBaseDataObject {

    /**
     * Call status
     */
    public enum Disposition {
        ANSWERED, BUSY, FAILED, NO_ANSWER, UNKNOWN;
    }

    private DateTime startDate;
    private DateTime endDate;
    private Date answerDate;
    private Disposition disposition;
    private String errorMessage;
    private String phoneNumber;
    private String callId;
    private Integer duration;
    private CallDirection callDirection;

    private List<CallEvent> callEvents = new ArrayList<CallEvent>();

    private Map<String, Object> customProperties = new HashMap<>();

    private CallRecord() {
    }

    public CallRecord(String callId, String phoneNumber) {
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
    public CallRecord(Date startDate, Date endDate, Date answerDate, Disposition disposition, Integer duration) {
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
    public CallRecord(Disposition disposition, String errorMessage) {
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
    public static CallRecord create(String phoneNumber, CallDirection callDirection, Disposition disposition) {
        CallRecord callDetailRecord = new CallRecord();
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

    public Disposition getDisposition() {
        return disposition;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public CallDirection getCallDirection() {
        return callDirection;
    }


    public CallRecord setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }


    public CallRecord setCallDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
        return this;
    }

    public CallRecord setStartDate(DateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public CallRecord setEndDate(DateTime endDate) {
        this.endDate = endDate;
        duration = new Period(startDate, endDate).toStandardSeconds().getSeconds();
        return this;
    }

    public CallRecord setAnswerDate(Date answerDate) {
        this.answerDate = answerDate;
        return this;
    }

    public CallRecord setDisposition(Disposition disposition) {
        this.disposition = disposition;
        return this;
    }

    public CallRecord setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public CallRecord setCustomProperties(Map<String, Object> customProperties) {
        if (customProperties != null) {
            this.customProperties = customProperties;
        }
        return this;
    }

    public CallRecord addCustomProperty(String key, Object value) {
        this.customProperties.put(key, value);
        return this;
    }

    public CallRecord setCallId(String callId) {
        this.callId = callId;
        return this;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<CallEvent> getCallEvents() {
        return callEvents;
    }

    public void setCallEvents(List<CallEvent> callEvents) {
        this.callEvents = callEvents;
    }
}
