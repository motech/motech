package org.motechproject.server.voxeo.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.service.CallRequest;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Phone call data entity represents entire call from dial/recieve to hangup/disconnect,
 * used to store in data-store AllPhoneCalls. This is aggregates all phone events though-out the call duration
 */
@TypeDiscriminator("doc.type === 'PhoneCall'")
public class PhoneCall extends MotechBaseDataObject {
    /**
     * Call direction, Incoming and Outgoing
     */
    public enum Direction {
        INCOMING, OUTGOING
    }

    @JsonProperty
    private Date startDate;
    @JsonProperty
    private Date endDate;
    @JsonProperty
    private Date answerDate;
    @JsonProperty
    private CallDetailRecord.Disposition disposition;
    @JsonProperty
    private Direction direction;
    @JsonProperty
    private CallRequest callRequest;
    @JsonProperty
    private Set<PhoneCallEvent> events;
    @JsonProperty
    private String sessionId;

    public PhoneCall() {
    }

    public PhoneCall(CallRequest callRequest) {
        this.callRequest = callRequest;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(Date answerDate) {
        this.answerDate = answerDate;
    }

    @JsonIgnore
    public Integer getDuration() {
        if (null == endDate || null == startDate) {
            return null;
        }

        return (int) ((endDate.getTime() - startDate.getTime()) / 1000);
    }

    public CallRequest getCallRequest() {
        return callRequest;
    }

    public void setCallRequest(CallRequest callRequest) {
        this.callRequest = callRequest;
    }

    public CallDetailRecord.Disposition getDisposition() {
        return disposition;
    }

    public void setDisposition(CallDetailRecord.Disposition disposition) {
        this.disposition = disposition;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void addEvent(PhoneCallEvent phoneCallEvent) {
        if (null == events) {
            events = new HashSet<PhoneCallEvent>();
        }
        events.add(phoneCallEvent);
    }

    public Set<PhoneCallEvent> getEvents() {
        Set<PhoneCallEvent> ret = events;
        if (null == events) {
            ret = Collections.emptySet();
        }

        return ret;
    }

    public PhoneCallEvent getEvent(PhoneCallEvent.Status status) {
        for (PhoneCallEvent event : events) {
            if (status == event.getStatus()) {
                return event;
            }
        }

        return null;
    }
}
