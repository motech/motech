package org.motechproject.sms;


import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

import static org.motechproject.util.DateUtil.newDateTime;
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

    public SMS(String refNo, String recipient, String messageContent, LocalDate sentDate, Time sentTime) {
        this.refNo = refNo;
        this.phoneNumber = recipient;
        this.messageContent = messageContent;
        this.messageTime = newDateTime(sentDate, sentTime);
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