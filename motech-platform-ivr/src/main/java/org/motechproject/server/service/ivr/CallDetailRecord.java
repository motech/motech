package org.motechproject.server.service.ivr;

import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallDetailRecord {
    public enum Disposition {
        ANSWERED, BUSY, FAILED, NO_ANSWER, UNKNOWN;
    }

    private Date startDate;
    private Date endDate;
    private Date answerDate;
    private Disposition disposition;
    private Integer duration;
    private String errorMessage;
    private String phoneNumber;

    private String callId;
    private IVRRequest.CallDirection callDirection;
    private List<CallEvent> callEvents = new ArrayList<CallEvent>();

    private CallDetailRecord() {
    }

    public static CallDetailRecord create(String callId, String phoneNumber) {
        CallDetailRecord callDetailRecord = new CallDetailRecord();
        callDetailRecord.startDate = DateUtil.today().toDate();
        callDetailRecord.disposition = Disposition.ANSWERED;
        callDetailRecord.answerDate = callDetailRecord.startDate;
        callDetailRecord.phoneNumber = phoneNumber;
        callDetailRecord.callId = callId;
        callDetailRecord.callDirection = IVRRequest.CallDirection.Inbound;
        return callDetailRecord;
    }

    public CallDetailRecord(Date startDate, Date endDate, Date answerDate,
                            Disposition disposition, Integer duration) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.answerDate = answerDate;
        this.disposition = disposition;
        this.duration = duration;
    }

    public CallDetailRecord(Disposition disposition, String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getAnswerDate() {
        return answerDate;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public IVRRequest.CallDirection getCallDirection() {
        return callDirection;
    }

    public String getCallId() {
        return callId;
    }

    public List<CallEvent> getCallEvents() {
        return callEvents;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public void setCallEvents(List<CallEvent> callEvents) {
        this.callEvents = callEvents;
    }

    public void setCallDirection(IVRRequest.CallDirection callDirection) {
        this.callDirection = callDirection;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setAnswerDate(Date answerDate) {
        this.answerDate = answerDate;
    }

    public void setDisposition(Disposition disposition) {
        this.disposition = disposition;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addCallEvent(CallEvent callEvent) {
        callEvents.add(callEvent);
    }
}
