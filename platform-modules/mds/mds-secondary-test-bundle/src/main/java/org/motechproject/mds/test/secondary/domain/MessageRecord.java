package org.motechproject.mds.test.secondary.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.Objects;

@Entity
public class MessageRecord {

    @Field
    private String author;

    @Field
    private CallStatus callStatus;

    @Field
    private String message;

    public MessageRecord(String author, CallStatus callStatus, String message) {
        this.author = author;
        this.callStatus = callStatus;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageRecord that = (MessageRecord) o;
        return Objects.equals(author, that.author) &&
                Objects.equals(callStatus, that.callStatus) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, callStatus, message);
    }
}
