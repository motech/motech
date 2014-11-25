package org.motechproject.mds.domain;

import org.motechproject.mds.dto.TrackingDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import static org.motechproject.mds.util.Constants.Util;

/**
 * The <code>Tracking</code> contains properties that describe the audit settings of an Entity,
 * such as whether to record history or publish CRUD events for a given Entity.
 * This class is related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = Util.TRUE)
public class Tracking {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Entity entity;

    @Persistent
    private boolean recordHistory;

    @Persistent
    private boolean allowCreateEvent;

    @Persistent
    private boolean allowUpdateEvent;

    @Persistent
    private boolean allowDeleteEvent;

    public Tracking() {
        this(null);
    }

    public Tracking(Entity entity) {
        this.entity = entity;
    }

    public TrackingDto toDto() {
        TrackingDto dto = new TrackingDto();

        dto.setRecordHistory(recordHistory);
        dto.setAllowCreateEvent(allowCreateEvent);
        dto.setAllowUpdateEvent(allowUpdateEvent);
        dto.setAllowDeleteEvent(allowDeleteEvent);

        return dto;
    }

    public void update(TrackingDto trackingDto) {
        allowCreateEvent = trackingDto.isAllowCreateEvent();
        allowDeleteEvent = trackingDto.isAllowDeleteEvent();
        allowUpdateEvent = trackingDto.isAllowUpdateEvent();
        recordHistory = trackingDto.isRecordHistory();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isRecordHistory() {
        return recordHistory;
    }

    public void setRecordHistory(boolean recordHistory) {
        this.recordHistory = recordHistory;
    }

    public boolean isAllowCreateEvent() {
        return allowCreateEvent;
    }

    public void setAllowCreateEvent(boolean allowCreateEvent) {
        this.allowCreateEvent = allowCreateEvent;
    }

    public boolean isAllowUpdateEvent() {
        return allowUpdateEvent;
    }

    public void setAllowUpdateEvent(boolean allowUpdateEvent) {
        this.allowUpdateEvent = allowUpdateEvent;
    }

    public boolean isAllowDeleteEvent() {
        return allowDeleteEvent;
    }

    public void setAllowDeleteEvent(boolean allowDeleteEvent) {
        this.allowDeleteEvent = allowDeleteEvent;
    }

    public Tracking copy() {
        Tracking copy = new Tracking();

        copy.setRecordHistory(recordHistory);
        copy.setAllowCreateEvent(allowCreateEvent);
        copy.setAllowUpdateEvent(allowUpdateEvent);
        copy.setAllowDeleteEvent(allowDeleteEvent);

        return copy;
    }
}
