package org.motechproject.mds.domain;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mds.dto.RestOptionsDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>RestOptionsMapping</code> class representing rest options of given entity. This class
 * is related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class RestOptions {

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

    public RestOptions() {
        this(null);
    }

    public RestOptions(Entity entity) {
        this.entity = entity;
    }

    public RestOptionsDto toDto() {
        RestOptionsDto dto = new RestOptionsDto();

        dto.setId(id);
        dto.setCreate(allowCreate);
        dto.setRead(allowRead);
        dto.setUpdate(allowUpdate);
        dto.setDelete(allowDelete);

        for (Lookup lookup : getLookups()) {
            dto.addLookup(lookup.getId());
        }

        for (Field field : getFields()) {
            dto.addField(field.getId());
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

    public List<Lookup> getLookups() {
        List<Lookup> lookups = new ArrayList<>(getEntity().getLookups());
        CollectionUtils.filter(lookups, new RestPredicate());

        return lookups;
    }

    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>(getEntity().getFields());
        CollectionUtils.filter(fields, new RestPredicate());

        return fields;
    }

    public final void update(RestOptionsDto restOptionsDto) {
        allowCreate = restOptionsDto.isCreate();
        allowRead = restOptionsDto.isRead();
        allowUpdate = restOptionsDto.isUpdate();
        allowDelete = restOptionsDto.isDelete();
    }

    public RestOptions copy() {
        RestOptions copy = new RestOptions();

        copy.setAllowCreate(this.allowCreate);
        copy.setAllowRead(this.allowRead);
        copy.setAllowUpdate(this.allowUpdate);
        copy.setAllowDelete(this.allowDelete);

        return copy;
    }

    private static class RestPredicate implements Predicate {
        private static final String PROPERTY_NAME = "exposedViaRest";

        @Override
        public boolean evaluate(Object object) {
            boolean match;

            try {
                Object propValue = PropertyUtils.getProperty(object, PROPERTY_NAME);
                String propValueAsString = String.valueOf(propValue);

                match = Boolean.parseBoolean(propValueAsString);
            } catch (Exception e) {
                // both classes that presents field and lookup in an entity have exposedViaRest
                // property so theoretically no exception will be thrown. But for safety in this
                // case we suppose that the object does not match the predicate.
                match = false;
            }

            return match;
        }

    }
}
