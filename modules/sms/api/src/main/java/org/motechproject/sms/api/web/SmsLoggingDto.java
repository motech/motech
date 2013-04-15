package org.motechproject.sms.api.web;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.sms.api.domain.SmsRecord;

public class SmsLoggingDto {

    private String phoneNumber;
    private String smsType;
    private String messageTime;
    private String deliveryStatus;
    private String messageContent;

    public SmsLoggingDto(SmsRecord record) {
        this.phoneNumber = record.getPhoneNumber();
        this.smsType = record.getSmsType().toString();
        this.messageTime = DateTimeFormat.forPattern("Y-MM-dd hh:mm:ss").print(record.getMessageTime());
        this.deliveryStatus = record.getDeliveryStatus().toString();
        this.messageContent = record.getMessageContent();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

}
