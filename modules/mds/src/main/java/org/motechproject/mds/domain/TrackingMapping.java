package org.motechproject.mds.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mds.dto.TrackingDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.constants.Constants.Util;

/**
 * The <code>TrackingMapping</code> contains information about which fields and what kind of actions
 * should be logged. This class is related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = Util.TRUE)
public class TrackingMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private EntityMapping entity;

    @Persistent
    private boolean allowCreate;

    @Persistent
    private boolean allowRead;

    @Persistent
    private boolean allowUpdate;

    @Persistent
    private boolean allowDelete;

    public TrackingDto toDto() {
        TrackingDto dto = new TrackingDto();

        for (FieldMapping field : getFields()) {
            dto.addField(field.getId());
        }

        if (allowCreate) {
            dto.addAction("CREATE");
        }

        if (allowRead) {
            dto.addAction("READ");
        }

        if (allowUpdate) {
            dto.addAction("UPDATE");
        }

        if (allowDelete) {
            dto.addAction("DELETE");
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityMapping getEntity() {
        return entity;
    }

    public void setEntity(EntityMapping entity) {
        this.entity = entity;
    }

    public List<FieldMapping> getFields() {
        List<FieldMapping> fields = new ArrayList<>(getEntity().getFields());
        CollectionUtils.filter(fields, new TrackingPredicate());

        return fields;
    }

    public boolean isAllowCreate() {
        return allowCreate;
    }

    public void setAllowCreate(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    public boolean isAllowRead() {
        return allowRead;
    }

    public void setAllowRead(boolean allowRead) {
        this.allowRead = allowRead;
    }

    public boolean isAllowUpdate() {
        return allowUpdate;
    }

    public void setAllowUpdate(boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public boolean isAllowDelete() {
        return allowDelete;
    }

    public void setAllowDelete(boolean allowDelete) {
        this.allowDelete = allowDelete;
    }

    public void addAction(String action) {
        switch (action.toUpperCase()) {
            case "CREATE":
                allowCreate = true;
                break;
            case "READ":
                allowRead = true;
                break;
            case "UPDATE":
                allowUpdate = true;
                break;
            case "DELETE":
                allowDelete = true;
                break;
            default:
                // never happen
        }
    }

    public void removeActions() {
        allowCreate = false;
        allowRead = false;
        allowUpdate = false;
        allowDelete = false;
    }

    private static class TrackingPredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof FieldMapping && ((FieldMapping) object).isTracking();
        }

    }

}
