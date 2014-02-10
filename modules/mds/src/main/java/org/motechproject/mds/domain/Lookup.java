package org.motechproject.mds.domain;

import org.motechproject.mds.dto.LookupDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * The <code>Lookup</code> class contains information about single lookup
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class Lookup {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private String lookupName;

    @Persistent
    private boolean singleObjectReturn;

    @Persistent
    private boolean exposedViaRest;

    @Persistent
    private Entity entity;

    public Lookup() {
        this(null, false, false);
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
    }

    public Lookup(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, Entity entity) {
        this(lookupName, singleObjectReturn, exposedViaRest);
        this.entity = entity;
    }

    public Lookup(LookupDto lookupDto) {
        update(lookupDto);
    }

    public LookupDto toDto() {
        return new LookupDto(id, lookupName, singleObjectReturn, exposedViaRest);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLookupName() {
        return lookupName;
    }

    public void setLookupName(String lookupName) {
        this.lookupName = lookupName;
    }

    public boolean isSingleObjectReturn() {
        return singleObjectReturn;
    }

    public void setSingleObjectReturn(boolean singleObjectReturn) {
        this.singleObjectReturn = singleObjectReturn;
    }

    public boolean isExposedViaRest() {
        return exposedViaRest;
    }

    public void setExposedViaRest(boolean exposedViaRest) {
        this.exposedViaRest = exposedViaRest;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Lookup copy() {
        return new Lookup(lookupName, singleObjectReturn, exposedViaRest);
    }

    public final void update(LookupDto lookupDto) {
        singleObjectReturn = lookupDto.isSingleObjectReturn();
        exposedViaRest = lookupDto.isExposedViaRest();
        lookupName = lookupDto.getLookupName();
    }
}
