package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.util.ClassName;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.mds.constants.Constants.Util.TRUE;

/**
 * The <code>EntityMapping</code> class contains basic information about an entity. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION",
        extensions = {@Extension(vendorName = "datanucleus", key = "field-name", value = "entityVersion")})
@Unique(name="DRAFT_USER_IDX", members={"parentEntity", "draftOwnerUsername"})
public class EntityMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private String className;

    @Persistent
    private String name;

    @Persistent
    private String module;

    @Persistent
    private String namespace;

    @Persistent(mappedBy = "entity")
    @Element(dependent = TRUE)
    private List<LookupMapping> lookups;

    @Persistent(mappedBy = "entity")
    @Element(dependent = TRUE)
    private List<FieldMapping> fields;

    @Persistent(mappedBy = "parentEntity")
    @Element(dependent = TRUE)
    private List<EntityDraft> drafts;

    private Long entityVersion;

    public EntityMapping() {
        this(null);
    }

    public EntityMapping(String className) {
        this(className, null, null);
    }

    public EntityMapping(String className, String module, String namespace) {
        this(className, ClassName.getSimpleName(className), module, namespace);
    }

    public EntityMapping(String className, String name, String module, String namespace) {
        this.className = className;
        this.name = name;
        this.module = module;
        this.namespace = namespace;
    }

    public EntityDto toDto() {
        return new EntityDto(id, className, getName(), module, namespace);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return defaultIfBlank(name, ClassName.getSimpleName(className));
    }

    public void setName(String name) {
        this.name = defaultIfBlank(name, ClassName.getSimpleName(className));
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<LookupDto> getLookupsDtos() {
        List<LookupDto> dtos = new ArrayList<>();

        for (LookupMapping mapping : lookups) {
            dtos.add(mapping.toDto());
        }
        return dtos;
    }

    public List<LookupMapping> getLookups() {
        if (lookups == null) {
            lookups = new ArrayList<>();
        }
        return lookups;
    }

    public void setLookups(List<LookupMapping> lookups) {
        this.lookups = lookups;
    }


    public LookupMapping getLookup(Long lookupId) {
        for (LookupMapping lookup : lookups) {
            if (Objects.equals(lookup.getId(), lookupId)) {
                return lookup;
            }
        }
        return null;
    }

    public List<EntityDraft> getDrafts() {
        if (drafts == null) {
            drafts = new ArrayList<>();
        }
        return drafts;
    }

    public void setDrafts(List<EntityDraft> drafts) {
        this.drafts = drafts;
    }

    public Long getEntityVersion() {
        return entityVersion;
    }

    public void setEntityVersion(Long entityVersion) {
        this.entityVersion = entityVersion;
    }

    @NotPersistent
    public boolean isReadOnly() {
        return isNotBlank(module) || isNotBlank(namespace);
    }

    public List<FieldMapping> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<FieldMapping> fields) {
        this.fields = fields;
    }

    public FieldMapping getField(Long id) {
        for (FieldMapping field : getFields()) {
            if (field.getId().equals(id)) {
                return field;
            }
        }

        return null;
    }

    public FieldMapping getField(String name) {
        for (FieldMapping field : getFields()) {
            if (StringUtils.equals(name, field.getName())) {
                return field;
            }
        }
        return null;
    }

    public void removeField(Long fieldId) {
        for (Iterator<FieldMapping> it = getFields().iterator(); it.hasNext(); ) {
            FieldMapping field = it.next();
            if (Objects.equals(field.getId(), fieldId)) {
                it.remove();
                break;
            }
        }
    }

    public void addField(FieldMapping field) {
        getFields().add(field);
    }

    public void addLookup(LookupMapping lookup) {
        getLookups().add(lookup);
    }

    public void removeLookup(Long lookupId) {
        for (Iterator<LookupMapping> it = getLookups().iterator(); it.hasNext(); ) {
            LookupMapping lookup = it.next();
            if (Objects.equals(lookup.getId(), lookupId)) {
                it.remove();
                break;
            }
        }
    }

    public LookupMapping getLookupById(Long lookupId) {
        for (LookupMapping lookup : getLookups()) {
            if (Objects.equals(lookupId, lookup.getId())) {
                return lookup;
            }
        }
        return null;
    }

    public void updateFromDraft(EntityDraft draft) {
        getFields().clear();
        for (FieldMapping field : draft.getFields()) {
            addField(field.copy());
        }

        getLookups().clear();
        for (LookupMapping lookup : draft.getLookups()) {
            LookupMapping copy = lookup.copy();
            copy.setEntity(this);
            addLookup(copy);
        }
    }

    @NotPersistent
    public boolean isDraft() {
        return false;
    }

    @NotPersistent
    public AdvancedSettingsDto advancedSettingsDto() {
        AdvancedSettingsDto advancedSettingsDto = new AdvancedSettingsDto();

        List<LookupDto> indexes = new ArrayList<>();
        for (LookupMapping lookup : getLookups()) {
            indexes.add(lookup.toDto());
        }

        advancedSettingsDto.setIndexes(indexes);
        advancedSettingsDto.setEntityId(getId());

        return advancedSettingsDto;
    }

    public void updateAdvancedSetting(AdvancedSettingsDto advancedSettings) {
        // deletion
        for (Iterator<LookupMapping> it = getLookups().iterator(); it.hasNext(); ) {
            LookupMapping lookup = it.next();

            boolean inNewList = false;
            for (LookupDto lookupDto : advancedSettings.getIndexes()) {
                if (Objects.equals(lookup.getId(), lookupDto.getId())) {
                    inNewList = true;
                    break;
                }
            }

            if (!inNewList) {
                it.remove();
            }
        }

        for (LookupDto lookupDto : advancedSettings.getIndexes()) {
            LookupMapping lookup = getLookupById(lookupDto.getId());
            if (lookup == null) {
                LookupMapping newLookup = new LookupMapping(lookupDto);
                addLookup(newLookup);
            } else {
                lookup.update(lookupDto);
            }
        }
    }
}
