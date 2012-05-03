package org.motechproject.sms.api;

public class SMSRecord {
    private SMSType type;
    private String phoneNo;
    private DeliveryStatus deliveryStatus;
    private String refNo;

    public SMSRecord(SMSType type, String phoneNo, DeliveryStatus deliveryStatus, String refNo) {
        this.type = type;
        this.phoneNo = phoneNo;
        this.deliveryStatus = deliveryStatus;
        this.refNo = refNo;
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
}
