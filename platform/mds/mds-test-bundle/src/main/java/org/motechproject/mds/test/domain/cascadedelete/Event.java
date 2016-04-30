package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Event extends MdsVersionedEntity {

    @Field
    private String message;

    @Field
    private EventLog eventLog;

    @Field
    private Map<String, String> data;

    public Event(String message) {
        this.message = message;
        this.data = new HashMap<>();
    }


    public EventLog getEventLog() {
        return eventLog;
    }

    public void setEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Event event = (Event) o;

        return !(message != null ? !message.equals(event.message) : event.message != null);

    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }
}
