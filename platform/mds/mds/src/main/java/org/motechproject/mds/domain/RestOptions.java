package org.motechproject.mds.domain;

import org.motechproject.mds.dto.RestOptionsDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.List;
import java.util.Objects;

/**
 * The <code>RestOptions</code> class representing rest options of given entity. This class
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

    @Persistent
    private boolean modifiedByUser;

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
        dto.setModifiedByUser(modifiedByUser);

        for (Lookup lookup : getLookups()) {
            dto.addLookup(lookup.getLookupName());
        }

        for (Field field : getFields()) {
            dto.addField(field.getName());
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

    public boolean isModifiedByUser() {
        return modifiedByUser;
    }

    public void setModifiedByUser(boolean modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public List<Lookup> getLookups() {
        return entity.getLookupsExposedByRest();
    }

    public List<Field> getFields() {
        return entity.getFieldsExposedByRest();
    }

    public final void update(RestOptionsDto restOptionsDto) {
        allowCreate = restOptionsDto.isCreate();
        allowRead = restOptionsDto.isRead();
        allowUpdate = restOptionsDto.isUpdate();
        allowDelete = restOptionsDto.isDelete();
        modifiedByUser = restOptionsDto.isModifiedByUser();
    }

    public RestOptions copy() {
        RestOptions copy = new RestOptions();

        copy.setAllowCreate(this.allowCreate);
        copy.setAllowRead(this.allowRead);
        copy.setAllowUpdate(this.allowUpdate);
        copy.setAllowDelete(this.allowDelete);
        copy.setModifiedByUser(this.modifiedByUser);

        return copy;
    }

    @NotPersistent
    public boolean supportsAnyOperation() {
        return allowRead || allowCreate || allowUpdate || allowDelete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowCreate, allowDelete, allowRead, allowUpdate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RestOptions other = (RestOptions) obj;

        return Objects.equals(this.allowCreate, other.allowCreate) &&
                Objects.equals(this.allowDelete, other.allowDelete) &&
                Objects.equals(this.allowRead, other.allowRead) &&
                Objects.equals(this.allowUpdate, other.allowUpdate);
    }
}
