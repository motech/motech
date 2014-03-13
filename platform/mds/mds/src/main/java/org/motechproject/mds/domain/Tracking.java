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

import static org.motechproject.mds.util.Constants.Util;

/**
 * The <code>Tracking</code> contains information about which fields and what kind of actions
 * should be logged. This class is related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = Util.TRUE)
public class Tracking {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Entity entity;

    @Persistent
    private boolean allowCreate;

    @Persistent
    private boolean allowRead;

    @Persistent
    private boolean allowUpdate;

    @Persistent
    private boolean allowDelete;

    public Tracking() {
        this(null);
    }

    public Tracking(Entity entity) {
        this.entity = entity;
    }

    public TrackingDto toDto() {
        TrackingDto dto = new TrackingDto();

        // add tracked fields to dto
        for (Field field : getFields()) {
            dto.addField(field.getId());
        }

        // set correct actions
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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>(getEntity().getFields());
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

    public Tracking copy() {
        Tracking copy = new Tracking();

        copy.setAllowCreate(allowCreate);
        copy.setAllowRead(allowRead);
        copy.setAllowUpdate(allowUpdate);
        copy.setAllowDelete(allowDelete);

        return copy;
    }

    private static class TrackingPredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof Field && ((Field) object).isTracked();
        }

    }

}
