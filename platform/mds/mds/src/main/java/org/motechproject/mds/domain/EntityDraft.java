package org.motechproject.mds.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Key;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Value;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents user drafts of an Entity. A draft is user's work in progress from the
 * UI. This shares the table with its superclass, {@link Entity}.
 */
@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class EntityDraft extends Entity {
    private static final String ID_OID = "id_OID";

    @Persistent
    @Column(allowsNull = "false")
    private String draftOwnerUsername;

    @Persistent
    private DateTime lastModificationDate;

    @Persistent
    private Entity parentEntity;

    @Persistent
    private Long parentVersion;

    @Persistent
    private boolean changesMade;

    @Join(table = "EntityDraft_fieldNameChanges", column = ID_OID)
    @Key(column = "oldName")
    @Value(column = "newName")
    private Map<String, String> fieldNameChanges = new HashMap<>();

    @Join(table = "EntityDraft_uniqueIndexesToDropV2", column = ID_OID)
    @Element(column = "fieldName")
    private Set<String> uniqueIndexesToDrop = new HashSet<>();

    @Join(table = "EntityDraft_fieldsToRemove", column = ID_OID)
    @Element(column = "fieldName")
    private Set<String> fieldsToRemove = new HashSet<>();

    @Join(table = "EntityDraft_fieldNameRequired", column = ID_OID)
    @Key(column = "fieldName")
    @Value(column = "required")
    private Map<String, String> fieldNameRequired = new HashMap<>();

    @Override
    public void updateAdvancedSetting(AdvancedSettingsDto advancedSettings) {
        updateIndexes(advancedSettings.getIndexes());
        updateBrowsingSettings(advancedSettings, true);
        updateRestOptions(advancedSettings);
        updateTracking(advancedSettings);
    }

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

    public Entity getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(Entity parentEntity) {
        this.parentEntity = parentEntity;
    }

    public Long getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(Long parentVersion) {
        this.parentVersion = parentVersion;
    }

    public boolean isChangesMade() {
        return changesMade;
    }

    public void setChangesMade(boolean changesMade) {
        this.changesMade = changesMade;
    }

    public Map<String, String> getFieldNameChanges() {
        return fieldNameChanges;
    }

    public void setFieldNameChanges(Map<String, String> fieldNameChanges) {
        this.fieldNameChanges = fieldNameChanges;
    }

    public Set<String> getUniqueIndexesToDrop() {
        return uniqueIndexesToDrop;
    }

    public void setUniqueIndexesToDrop(Set<String> uniqueIndexesToDrop) {
        this.uniqueIndexesToDrop = uniqueIndexesToDrop;
    }

    public Set<String> getFieldsToRemove() {
        return fieldsToRemove;
    }

    public void setFieldsToRemove(Set<String> fieldsToRemove) {
        this.fieldsToRemove = fieldsToRemove;
    }

    public void addFieldToRemove(String fieldName) {
        this.fieldsToRemove.add(fieldName);
    }

    public Map<String, String> getFieldNameRequired() {
        return fieldNameRequired;
    }

    public void setFieldNameRequired(Map<String, String> fieldNameRequired) {
        this.fieldNameRequired = fieldNameRequired;
    }

    public void addUniqueToRemove(String fieldName) {
        String actualName = getFieldNameChanges().containsKey(fieldName) ?
                getFieldNameChanges().get(fieldName) :
                fieldName;
        getUniqueIndexesToDrop().add(actualName);
    }

    public void addRequiredToChange(String fieldName, boolean newValue) {
        String actualName = getFieldNameChanges().containsKey(fieldName) ?
                getFieldNameChanges().get(fieldName) :
                fieldName;

        this.fieldNameRequired.put(actualName, Boolean.toString(newValue));
    }

    public void addFieldNameChange(String originalName, String newName) {
        if (getUniqueIndexesToDrop().contains(originalName)) {
            getUniqueIndexesToDrop().remove(originalName);
            getUniqueIndexesToDrop().add(newName);
        }

        //Checking if field name was previously changed and updating new name in map or adding new entry
        if (getFieldNameChanges().containsValue(originalName)) {
            for (String key : getFieldNameChanges().keySet()) {
                if (originalName.equals(getFieldNameChanges().get(key))) {
                    getFieldNameChanges().put(key, newName);
                }
            }
        } else {
            getFieldNameChanges().put(originalName, newName);
        }
    }

    @Override
    public EntityDto toDto() {
        EntityDto dto = super.toDto();
        dto.setModified(isChangesMade());
        dto.setOutdated(isOutdated());
        dto.setId(getParentEntity().getId());
        return dto;
    }

    @Override
    @NotPersistent
    public boolean isDraft() {
        return true;
    }

    @NotPersistent
    public boolean isOutdated() {
        return !Objects.equals(getParentVersion(), getParentEntity().getEntityVersion());
    }

    @Override
    public boolean isActualEntity() {
        return false;
    }
}
