package org.motechproject.mds.test.domain.setofenumandstring;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(recordHistory = true)
public class Message {

    @Field
    private String subject;

    @Field
    private String content;

    @Field
    private Set<Channel> broadcastChannels;

    @Field
    private Set<String> recipients;

    public Message(String subject, String content) {
        this(subject, content, new HashSet<Channel>(), new HashSet<String>());
    }

    public Message(String subject, String content, Set<Channel> broadcastChannels, Set<String> recipients) {
        this.subject = subject;
        this.content = content;
        this.broadcastChannels = broadcastChannels;
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Set<Channel> getBroadcastChannels() {
        return broadcastChannels;
    }

    public void setBroadcastChannels(Set<Channel> broadcastChannels) {
        this.broadcastChannels = broadcastChannels;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return Objects.equals(subject, message.subject) &&
                Objects.equals(content, message.content) &&
                Objects.equals(broadcastChannels, message.broadcastChannels) &&
                Objects.equals(recipients, message.recipients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, content, broadcastChannels, recipients);
    }
}
