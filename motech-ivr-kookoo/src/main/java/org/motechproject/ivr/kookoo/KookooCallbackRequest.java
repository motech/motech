package org.motechproject.ivr.kookoo;

public class KookooCallbackRequest {

    private final String ANSWERED = "answered";
    private final String NOT_ANSWERED = "ring";

    private String sid;
    private String status_details;
    private String caller_id;
    private String status;
    private String phone_no;
    private String start_time;
    private String external_id;
    private String call_type;
    private String call_detail_record_id;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getStatus_details() {
        return status_details;
    }

    public void setStatus_details(String status_details) {
        this.status_details = status_details;
    }

    public String getCaller_id() {
        return caller_id;
    }

    public void setCaller_id(String caller_id) {
        this.caller_id = caller_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public boolean notAnswered() {
        return !ANSWERED.equals(status);
    }

    public String getCall_detail_record_id() {
        return call_detail_record_id;
    }

    public void setCall_detail_record_id(String call_detail_record_id) {
        this.call_detail_record_id = call_detail_record_id;
    }
}
