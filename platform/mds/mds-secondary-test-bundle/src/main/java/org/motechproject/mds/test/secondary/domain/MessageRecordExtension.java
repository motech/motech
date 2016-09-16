package org.motechproject.mds.test.secondary.domain;


import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Field;


@EntityExtension
public class MessageRecordExtension extends MessageRecord {

    @Field
    private String receiver;

    public MessageRecordExtension(String author, CallStatus callStatus, String message, String receiver) {
        super(author, callStatus, message);
        this.receiver = receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }
}
