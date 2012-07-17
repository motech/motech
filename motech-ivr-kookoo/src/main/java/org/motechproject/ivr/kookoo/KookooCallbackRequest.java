package org.motechproject.ivr.kookoo;

public class KookooCallbackRequest {

    private static final String ANSWERED = "answered";
    private static final String NOT_ANSWERED = "ring";

    private String sid;
    private String statusDetails;
    private String callerId;
    private String status;
    private String phoneNumber;
    private String startTime;
    private String externalId;
    private String callType;
    private String callDetailRecordId;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(String statusDetails) {
        this.statusDetails = statusDetails;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public boolean notAnswered() {
        return !ANSWERED.equals(status);
    }

    public String getCallDetailRecordId() {
        return callDetailRecordId;
    }

    public void setCallDetailRecordId(String callDetailRecordId) {
        this.callDetailRecordId = callDetailRecordId;
    }
}
