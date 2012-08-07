package org.motechproject.sms.smpp;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.sms.api.DeliveryStatus;

@TypeDiscriminator("doc.type === 'OutboundSMS'")
public class OutboundSMS extends SMS {
    @JsonProperty
    private DeliveryStatus deliveryStatus;
    @JsonProperty
    private String refNo;

    public OutboundSMS() {
    }

    public OutboundSMS(String recipient, String refNo, String messageContent, DateTime sentDate, DeliveryStatus deliveryStatus) {
        super(recipient, messageContent, sentDate);
        this.refNo = refNo;
        this.deliveryStatus = deliveryStatus;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getRefNo() {
        return refNo;
    }
}
