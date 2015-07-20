package org.motechproject.mds.domain;

import org.motechproject.mds.dto.TrackingDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import java.util.Objects;

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

    @Persistent
    private boolean modifiedByUser;

    @Persistent
    private boolean nonEditable;

    public Tracking() {
        this(null);
    }

    public Tracking(Entity entity) {
        this.entity = entity;
        this.allowCreateEvent = true;
        this.allowUpdateEvent = true;
        this.allowDeleteEvent = true;
    }

    public TrackingDto toDto() {
        TrackingDto dto = new TrackingDto();

        dto.setRecordHistory(recordHistory);
        dto.setAllowCreateEvent(allowCreateEvent);
        dto.setAllowUpdateEvent(allowUpdateEvent);
        dto.setAllowDeleteEvent(allowDeleteEvent);
        dto.setModifiedByUser(modifiedByUser);
        dto.setNonEditable(nonEditable);

        return dto;
    }

    public void update(TrackingDto trackingDto) {
        allowCreateEvent = trackingDto.isAllowCreateEvent();
        allowDeleteEvent = trackingDto.isAllowDeleteEvent();
        allowUpdateEvent = trackingDto.isAllowUpdateEvent();
        recordHistory = trackingDto.isRecordHistory();
        modifiedByUser = trackingDto.isModifiedByUser();
        nonEditable = trackingDto.isNonEditable();
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

    public boolean isModifiedByUser() {
        return modifiedByUser;
    }

    public void setModifiedByUser(boolean modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public boolean isNonEditable() {
        return nonEditable;
    }

    public void setNonEditable(boolean nonEditable) {
        this.nonEditable = nonEditable;
    }

    public Tracking copy() {
        Tracking copy = new Tracking();

        copy.setRecordHistory(recordHistory);
        copy.setAllowCreateEvent(allowCreateEvent);
        copy.setAllowUpdateEvent(allowUpdateEvent);
        copy.setAllowDeleteEvent(allowDeleteEvent);
        copy.setModifiedByUser(modifiedByUser);
        copy.setNonEditable(nonEditable);

        return copy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordHistory, allowCreateEvent, allowUpdateEvent, allowDeleteEvent, nonEditable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Tracking other = (Tracking) obj;

        return Objects.equals(this.recordHistory, other.recordHistory) &&
                Objects.equals(this.allowCreateEvent, other.allowCreateEvent) &&
                Objects.equals(this.allowUpdateEvent, other.allowUpdateEvent) &&
                Objects.equals(this.allowDeleteEvent, other.allowDeleteEvent) &&
                Objects.equals(this.nonEditable, other.nonEditable);
    }
}
