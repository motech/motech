package org.motechproject.mds.domain;

import org.motechproject.mds.dto.RestOptionsDto;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdGeneratorStrategy;

/**
 * The <code>RestOptionsMapping</code> class representing rest options of given entity. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class RestOptionsMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private boolean allowCreate;

    @Persistent
    private boolean allowRead;

    @Persistent
    private boolean allowUpdate;

    @Persistent
    private boolean allowDelete;

    public RestOptionsMapping() {
        this(false, false, false, false);
    }

    public RestOptionsMapping(boolean allowCreate, boolean allowRead, boolean allowUpdate, boolean allowDelete) {
        this.allowCreate = allowCreate;
        this.allowRead = allowRead;
        this.allowUpdate = allowUpdate;
        this.allowDelete = allowDelete;
    }

    public RestOptionsMapping(RestOptionsDto restOptions) {
        update(restOptions);
    }

    public RestOptionsDto toDto() {
        return new RestOptionsDto(id, allowCreate, allowRead, allowUpdate, allowDelete);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public final void update(RestOptionsDto restOptionsDto) {
        allowCreate = restOptionsDto.isCreate();
        allowRead = restOptionsDto.isRead();
        allowUpdate = restOptionsDto.isUpdate();
        allowDelete = restOptionsDto.isDelete();
    }

    public RestOptionsMapping copy() {
        RestOptionsMapping copy = new RestOptionsMapping();

        copy.setAllowCreate(this.allowCreate);
        copy.setAllowRead(this.allowRead);
        copy.setAllowUpdate(this.allowUpdate);
        copy.setAllowDelete(this.allowDelete);

        return copy;
    }
}
