package org.motechproject.sms;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.sms.api.DeliveryStatus;

@TypeDiscriminator("doc.type === 'OutboundSMS'")
public class OutboundSMS extends SMS {
    @JsonProperty
    private DeliveryStatus deliveryStatus;

    public OutboundSMS() {
    }

    public OutboundSMS(String recipient, String refNo, String messageContent, LocalDate sentDate, Time sentTime, DeliveryStatus deliveryStatus) {
        super(refNo, recipient, messageContent, sentDate, sentTime);
        this.deliveryStatus = deliveryStatus;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
