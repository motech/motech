package org.motechproject.sms.api;

import org.joda.time.DateTime;

public class SMSRecord {
    private SMSType type;
    private String phoneNo;
    private DeliveryStatus deliveryStatus;
    private String refNo;
    private String content;
    private DateTime messageTime;

    public SMSRecord(SMSType type, String phoneNo, DeliveryStatus deliveryStatus,
                     String refNo, String content, DateTime messageTime) {
        this.type = type;
        this.phoneNo = phoneNo;
        this.deliveryStatus = deliveryStatus;
        this.refNo = refNo;
        this.content = content;
        this.messageTime = messageTime;
    }

    public String getRefNo() {
        return refNo;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public SMSType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public DateTime getMessageTime() {
        return messageTime;
    }
}
