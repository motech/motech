package org.motechproject.mds.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;
import static org.motechproject.mds.util.Constants.Util.TRUE;
import static org.motechproject.mds.util.Constants.Util.VALUE_GENERATOR;
/**
 * The <code>MdsEntity</code> is an optional class for all domain classes acting
 * as Motech data services Entities. This class stores and allows access to all
 * default fields like id, creator or modification date. All classes annotated
 * {@link org.motechproject.mds.annotations.Entity} can extend this base class.
 */
@Entity
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = TRUE)
public abstract class MdsEntity {

    @Field
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
    @Index
    private Long id;

    @Field (displayName = "Owner")
    @Persistent(defaultFetchGroup = TRUE)
    @Extension(key = VALUE_GENERATOR, value = "ovg.owner", vendorName = DATANUCLEUS)
    private String owner;

    @Field (displayName = "Created By")
    @Persistent(defaultFetchGroup = TRUE)
    @Extension(key = VALUE_GENERATOR, value = "ovg.creator", vendorName = DATANUCLEUS)
    private String creator;

    @Field (displayName = "Creation Date")
    @Persistent(defaultFetchGroup = TRUE)
    @Extension(key = VALUE_GENERATOR, value = "ovg.creationDate", vendorName = DATANUCLEUS)
    private DateTime creationDate;

    @Field (displayName = "Modification Date")
    @Persistent(defaultFetchGroup = TRUE)
    @Extension(key = VALUE_GENERATOR, value = "ovg.modificationDate", vendorName = DATANUCLEUS)
    private DateTime modificationDate;

    @Field (displayName = "Modified By")
    @Persistent(defaultFetchGroup = TRUE)
    @Extension(key = VALUE_GENERATOR, value = "ovg.modifiedBy", vendorName = DATANUCLEUS)
    private String modifiedBy;

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getCreator() {
        return creator;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
