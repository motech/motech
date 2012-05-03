package org.motechproject.sms;


import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

import static org.motechproject.util.DateUtil.setTimeZone;

public abstract class SMS extends MotechBaseDataObject {
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    @JsonProperty
    private DateTime messageTime;
    @JsonProperty
    private String refNo;

    public SMS() {
    }

    public SMS(String refNo, String phoneNumber, String messageContent, DateTime messageTime) {
        this.refNo = refNo;
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public DateTime getMessageTime() {
        return setTimeZone(messageTime);
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getRefNo() {
        return refNo;
    }
}