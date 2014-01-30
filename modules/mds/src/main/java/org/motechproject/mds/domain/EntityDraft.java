package org.motechproject.mds.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.dto.EntityDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * This class represents a users draft of an Entity. A draft is a users work in progress from the UI. This shares
 * the table with its superclass, {@link EntityMapping}.
 */
@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class EntityDraft extends EntityMapping {

    @Persistent
    @Column(allowsNull = "false")
    private String draftOwnerUsername;

    @Persistent
    private DateTime lastModificationDate;

    @Persistent
    private EntityMapping parentEntity;

    @Persistent
    private Long parentVersion;

    @Persistent
    private Boolean changesMade;

    public String getDraftOwnerUsername() {
        return draftOwnerUsername;
    }

    public void setDraftOwnerUsername(String draftOwnerUsername) {
        this.draftOwnerUsername = draftOwnerUsername;
    }

    public DateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(DateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public EntityMapping getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(EntityMapping parentEntity) {
        this.parentEntity = parentEntity;
    }

    public Long getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(Long parentVersion) {
        this.parentVersion = parentVersion;
    }

    public Boolean getChangesMade() {
        return changesMade;
    }

    public void setChangesMade(Boolean changesMade) {
        this.changesMade = changesMade;
    }

    @Override
    public EntityDto toDto() {
        EntityDto dto = super.toDto();
        dto.setModified(getChangesMade() != null && getChangesMade());
        dto.setId(getParentEntity().getId());
        return dto;
    }

    @Override
    @NotPersistent
    public boolean isDraft() {
        return true;
    }
}
