package org.motechproject.sms;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.Date;

@TypeDiscriminator("doc.type === 'InboundSMS'")
public class InboundSMS extends SMS {
    @JsonProperty
    private String uuid;

    public InboundSMS() {
    }

    public InboundSMS(String originator, String messageContent, Date receivedTime, String uuid) {
        super(originator, messageContent, receivedTime);
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
