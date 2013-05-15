package org.motechproject.ivr.calllog.domain;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ivr.event.CallEventCustomData;

import java.io.Serializable;

/**
 * Represents IVR event like DTMF key press, Dial
 * @see IVREvent
 */
public class CallEvent implements Serializable{
    private static final long serialVersionUID = -5399759051930894664L;

    private String name;
    private DateTime timeStamp;
    private CallEventCustomData callEventCustomData = new CallEventCustomData();

    private CallEvent() {
    }

    public CallEvent(String name) {
        this(name, DateUtil.now());
    }

    private CallEvent(String name, DateTime timeStamp) {
        this.name = name;
        this.timeStamp = timeStamp;
    }

    /**
     * Factory method to create new dial event.
     * @return Dial event
     */
    public static CallEvent newDialEvent() {
        return new CallEvent("Dial", DateUtil.now());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = DateUtil.setTimeZone(timeStamp);
    }

    /**
     * Get additional data for call event such as audio played etc.
     * @return Data map with key value pair
     *
     */
    public CallEventCustomData getData() {
        return callEventCustomData;
    }

    /**
     * Set additional data for call event such as audio played etc.
     * @param callEventCustomData
     */
    public void setData(CallEventCustomData callEventCustomData) {
        this.callEventCustomData = callEventCustomData;
    }

    /**
     * Add key value pair to additional data in call event.
     * It can be used to store information related to IVR event like audio played etc.
     * @param key
     * @param value
     */
    public void appendData(String key, String value) {
        callEventCustomData.put(key, value);
    }
}
