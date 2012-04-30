package org.motechproject.sms;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.Date;

@TypeDiscriminator("doc.type === 'OutboundSMS'")
public class OutboundSMS extends SMS {
    @JsonProperty
    private String smscRefNo;
    @JsonProperty
    private DeliveryStatus deliveryStatus;

    public OutboundSMS() {
        super();
    }

    public OutboundSMS(String recipient, String refNo, String messageContent, Date sentDate, DeliveryStatus deliveryStatus) {
        super(recipient, messageContent, sentDate);
        this.smscRefNo = refNo;
        this.deliveryStatus = deliveryStatus;
    }

    public String getSmscRefNo() {
        return smscRefNo;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
