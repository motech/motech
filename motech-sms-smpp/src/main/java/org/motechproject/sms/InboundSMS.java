package org.motechproject.sms;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

@TypeDiscriminator("doc.type === 'InboundSMS'")
public class InboundSMS extends SMS {

    public InboundSMS() {
    }

    public InboundSMS(String originator, String messageContent, DateTime receivedTime) {
        super(null, originator, messageContent, receivedTime);
    }
}
