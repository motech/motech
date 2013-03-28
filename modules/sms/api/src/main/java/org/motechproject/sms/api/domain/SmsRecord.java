package org.motechproject.sms.api.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.sms.api.DeliveryStatus;
import org.motechproject.sms.api.SMSType;

@TypeDiscriminator("doc.type === 'SmsRecord'")
public class SmsRecord extends MotechBaseDataObject {

    @JsonProperty
    private SMSType smsType;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String messageContent;
    /**
     * Should be in UTC
     */
    @JsonProperty
    private DateTime messageTime;
    @JsonProperty
    private DeliveryStatus deliveryStatus;
    @JsonProperty
    private String referenceNumber;

    public SmsRecord() {
    }

    public SmsRecord(SMSType smsType, String phoneNumber, String messageContent, DateTime messageTime, DeliveryStatus deliveryStatus, String referenceNumber) {
        super("SmsRecord");
        this.smsType = smsType;
        this.phoneNumber = phoneNumber;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
        this.deliveryStatus = deliveryStatus;
        this.referenceNumber = referenceNumber;
    }

    public SMSType getSmsType() {
        return smsType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public DateTime getMessageTime() {
        return messageTime;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setStatus(DeliveryStatus status) {
        this.deliveryStatus = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
