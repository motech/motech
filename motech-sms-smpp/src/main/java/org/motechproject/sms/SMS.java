package org.motechproject.sms;


import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static org.motechproject.util.DateUtil.setTimeZone;

public class SMS extends MotechBaseDataObject {
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    @JsonProperty
    private Date messageTime;

    public SMS() {
    }

    public SMS(String phoneNumber, String messageContent, Date messageTime) {
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getMessageTime() {
        return setTimeZone(DateUtil.newDateTime(messageTime)).toDate();
    }

    public String getMessageContent() {
        return messageContent;
    }
}