package org.motechproject.model;

import java.io.Serializable;

/**
 *
 * Date: 07/03/11
 *
 */
public class InitiateCallData implements Serializable {

    private static final long serialVersionUID = 1L;

    private long messageId;
    private String phone;
    private int timeOut; //how long IVR will wait for the channel to be answered before its considered to have failed (in ms)
    private String vxmlUrl;

    public InitiateCallData(long messageId, String phone, int timeOut, String vxmlUrl) {

         if (phone == null) {
            throw new IllegalArgumentException("phone can not be null");
        }

        if (vxmlUrl == null) {
            throw new IllegalArgumentException("vxmlUrl can not be null");
        }

        this.messageId = messageId;
        this.phone = phone;
        this.timeOut = timeOut;
        this.vxmlUrl = vxmlUrl;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getPhone() {
        return phone;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public String getVxmlUrl() {
        return vxmlUrl;
    }
}
