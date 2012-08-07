package org.motechproject.sms.smpp;


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

    public SMS() {
    }

    public SMS(String recipient, String messageContent, DateTime sentDate) {
        this.phoneNumber = recipient;
        this.messageContent = messageContent;
        this.messageTime = sentDate;
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
}
