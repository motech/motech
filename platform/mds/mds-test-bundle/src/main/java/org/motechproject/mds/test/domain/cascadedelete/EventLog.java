package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EventLog extends MdsVersionedEntity {

    @Field
    private String name;

    @Field
    @Persistent(mappedBy = "eventLog")
    @Cascade(delete = true)
    private List<Event> events;


    public EventLog(String name) {
        this.name = name;
        this.events = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventLog eventLog = (EventLog) o;

        return !(name != null ? !name.equals(eventLog.name) : eventLog.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
