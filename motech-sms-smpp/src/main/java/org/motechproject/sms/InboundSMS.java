package org.motechproject.sms;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;

@TypeDiscriminator("doc.type === 'InboundSMS'")
public class InboundSMS extends SMS {

    public InboundSMS() {
    }

    public InboundSMS(String originator, String messageContent, LocalDate receivedDate, Time receivedTime) {
        super(null, originator, messageContent, receivedDate, receivedTime);
    }
}
