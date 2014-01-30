package org.motechproject.mds.domain;

import org.motechproject.mds.dto.LookupDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * The <code>LookupMapping</code> class contains information about single lookup
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class LookupMapping {

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
    private EntityMapping entity;

    public LookupMapping(String lookupName, boolean singleObjectReturn, boolean exposedViaRest) {
        this.lookupName = lookupName;
        this.singleObjectReturn = singleObjectReturn;
        this.exposedViaRest = exposedViaRest;
    }

    public LookupMapping(String lookupName, boolean singleObjectReturn, boolean exposedViaRest, EntityMapping entity) {
        this(lookupName, singleObjectReturn, exposedViaRest);
        this.entity = entity;
    }

    public LookupDto toDto() {
        return new LookupDto(id, lookupName, singleObjectReturn);
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

    public EntityMapping getEntity() {
        return entity;
    }

    public void setEntity(EntityMapping entity) {
        this.entity = entity;
    }
}
