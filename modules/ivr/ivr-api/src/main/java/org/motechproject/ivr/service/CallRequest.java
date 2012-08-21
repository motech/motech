package org.motechproject.ivr.service;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.MotechEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to request a call from the IVR system
 * <p/>
 * To receive events related to this call provide this class with the events to raise when the following
 * events within the IVR system happen.  The supplied event will be augmented with {@link org.motechproject.ivr.model.CallDetailRecord}
 * if one is available
 * <p/>
 * onSuccessEvent - Following the successful completion of the call
 * onBusyEvent - If the IVR system is unable to place the call because the line is busy
 * onNoAnswerEvent - If the IVR system is unable to complete the call because of no answer
 * onFailureEvent - It failed.  I'm sorry for you.  Why?  Not sure. Your guess is as good as mine,
 */
public class CallRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private String phone;
    @JsonProperty
    private int timeOut;
    @JsonProperty
    private String callBackUrl;     // this is really the channel!

    @JsonProperty
    private Map<String, String> payload = new HashMap<String, String>();

    @JsonProperty
    private MotechEvent onSuccessEvent;
    @JsonProperty
    private MotechEvent onBusyEvent;
    @JsonProperty
    private MotechEvent onNoAnswerEvent;
    @JsonProperty
    private MotechEvent onFailureEvent;

    private String callId;

    /**
     * Generate a call request for the IVR system
     *
     * @param phone
     * @param callBackUrl
     */
    public CallRequest(String phone, int timeOut, String callBackUrl) {
        if (phone == null) {
            throw new IllegalArgumentException("phone can not be null");
        }

        if (callBackUrl == null) {
            throw new IllegalArgumentException("callBackUrl can not be null");
        }
        this.callId = UUID.randomUUID().toString();
        this.phone = phone;
        this.timeOut = timeOut;
        this.callBackUrl = callBackUrl;
    }

    /**
     * Creates call request with name value pair additional config
     *
     * @param phone       dialing number / sip id
     * @param params      custom data for additional parameters
     * @param callBackUrl application endpoint to process callbacks
     */
    public CallRequest(String phone, Map<String, String> params, String callBackUrl) {
        if (phone == null) {
            throw new IllegalArgumentException("phone can not be null");
        }
        if (callBackUrl == null) {
            throw new IllegalArgumentException("callBackUrl can not be null");
        }
        this.callId = UUID.randomUUID().toString();
        this.phone = phone;
        this.timeOut = 0;

        if (params != null) {
            this.payload.putAll(params);
        }

        this.callBackUrl = callBackUrl;
    }

    public CallRequest() {
        this.callId = UUID.randomUUID().toString();
    }

    public String getPhone() {
        return phone;
    }

    public MotechEvent getOnSuccessEvent() {
        return onSuccessEvent;
    }

    public void setOnSuccessEvent(MotechEvent onSuccessEvent) {
        this.onSuccessEvent = onSuccessEvent;
    }

    public MotechEvent getOnBusyEvent() {
        return onBusyEvent;
    }

    public void setOnBusyEvent(MotechEvent onBusyEvent) {
        this.onBusyEvent = onBusyEvent;
    }

    public MotechEvent getOnNoAnswerEvent() {
        return onNoAnswerEvent;
    }

    public void setOnNoAnswerEvent(MotechEvent onNoAnswerEvent) {
        this.onNoAnswerEvent = onNoAnswerEvent;
    }

    public MotechEvent getOnFailureEvent() {
        return onFailureEvent;
    }

    public void setOnFailureEvent(MotechEvent onFailureEvent) {
        this.onFailureEvent = onFailureEvent;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getCallId() {
        return callId;
    }
}
