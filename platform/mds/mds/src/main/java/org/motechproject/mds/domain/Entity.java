package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.ex.LookupNameIsRepeatedException;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.mds.util.ValidationUtil;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.mds.util.Constants.Util.ENTITY;
import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * The <code>Entity</code> class contains information about an entity. Also it contains
 * information about advanced settings related with the entity.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Unique(name = "DRAFT_USER_IDX", members = { "parentEntity", "draftOwnerUsername" })
public class Entity {

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

    @Persistent
    private SecurityMode securityMode;

    @Persistent(mappedBy = ENTITY)
    @Element(dependent = TRUE)
    private List<Lookup> lookups;

    @Persistent(mappedBy = ENTITY)
    @Element(dependent = TRUE)
    private List<Field> fields;

    @Persistent(mappedBy = ENTITY)
    @Element(dependent = TRUE)
    private Tracking tracking;

    @Persistent(mappedBy = "parentEntity")
    @Element(dependent = TRUE)
    private List<EntityDraft> drafts;

    private Long entityVersion = 1L;

    @Persistent(mappedBy = ENTITY, dependent = TRUE)
    private RestOptions restOptions;

    @Join(column = "Entity_OID")
    @Element(column = "SecurityMember")
    private Set<String> securityMembers;

    public Entity() {
        this(null);
    }

    public Entity(String className) {
        this(className, null, null, null);
    }

    public Entity(String className, String module, String namespace, SecurityMode securityMode) {
        this(className, ClassName.getSimpleName(className), module, namespace, securityMode, null);
    }

    public Entity(String className, String name, String module, String namespace, SecurityMode securityMode, Set<String> securityMembers) {
        this.className = className;
        this.module = module;
        this.namespace = namespace;
        this.securityMode = securityMode != null ? securityMode : SecurityMode.EVERYONE;
        this.securityMembers = securityMembers != null ? securityMembers : new HashSet<String>();
        setName(name);
    }

    public EntityDto toDto() {
        return new EntityDto(id, className, getName(), module, namespace, securityMode, securityMembers);
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

    public final void setName(String name) {
        ValidationUtil.validateNoJavaKeyword(name);
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

    public List<Lookup> getLookups() {
        if (lookups == null) {
            lookups = new ArrayList<>();
        }
        return lookups;
    }

    public List<LookupDto> getLookupsDtos() {
        List<LookupDto> dtos = new ArrayList<>();

        for (Lookup lookup : lookups) {
            dtos.add(lookup.toDto());
        }
        return dtos;
    }

    public void setLookups(List<Lookup> lookups) {
        this.lookups = lookups;
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

    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(SecurityMode securityMode) {
        this.securityMode = securityMode != null ? securityMode : SecurityMode.EVERYONE;
    }

    public Set<String> getSecurityMembers() {
        return securityMembers;
    }

    public void setSecurityMembers(Set<String> securityMembers) {
        this.securityMembers = securityMembers;
    }

    @NotPersistent
    public boolean isDDE() {
        return isNotBlank(module) || isNotBlank(namespace);
    }

    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Field getField(Long id) {
        for (Field field : getFields()) {
            if (field.getId().equals(id)) {
                return field;
            }
        }

        return null;
    }

    public Field getField(String name) {
        for (Field field : getFields()) {
            if (StringUtils.equals(name, field.getName())) {
                return field;
            }
        }
        return null;
    }

    public void removeField(Long fieldId) {
        Iterator<Field> it = getFields().iterator();

        while (it.hasNext()) {
            Field field = it.next();
            if (Objects.equals(field.getId(), fieldId)) {
                it.remove();
                break;
            }
        }
    }

    public void addField(Field field) {
        Field existing = getField(field.getName());

        if (existing == null) {
            getFields().add(field);
        } else {
            existing.update(field.toDto());
        }
    }

    public void addLookup(Lookup lookup) {
        Lookup existing = getLookupById(lookup.getId());
        if (getLookupByName(lookup.getLookupName()) != null) {
            throw new LookupNameIsRepeatedException();
        }

        if (existing == null) {
            getLookups().add(lookup);
        } else {
            LookupDto lookupDto = lookup.toDto();
            List<Field> lookupFields = new ArrayList<>();

            for (Long fieldId : lookupDto.getFieldList()) {
                lookupFields.add(getField(fieldId));
            }

            existing.update(lookupDto, lookupFields);
        }
    }

    public void removeLookup(Long lookupId) {
        Iterator<Lookup> it = getLookups().iterator();

        while (it.hasNext()) {
            Lookup lookup = it.next();
            if (Objects.equals(lookup.getId(), lookupId)) {
                it.remove();
                break;
            }
        }
    }

    public Lookup getLookupById(Long lookupId) {
        for (Lookup lookup : getLookups()) {
            if (lookupId != null && Objects.equals(lookupId, lookup.getId())) {
                return lookup;
            }
        }
        return null;
    }

    public Lookup getLookupByName(String lookupName) {
        for (Lookup lookup : getLookups()) {
            if (StringUtils.equals(lookupName, lookup.getLookupName())) {
                return lookup;
            }
        }
        return null;
    }

    public void updateFromDraft(EntityDraft draft) {
        getFields().clear();
        for (Field field : draft.getFields()) {
            addField(field.copy());
        }

        getLookups().clear();
        for (Lookup lookup : draft.getLookups()) {
            Lookup copy = lookup.copy(getFields());
            copy.setEntity(this);
            addLookup(copy);
        }

        if (draft.getRestOptions() != null) {
            restOptions = draft.getRestOptions().copy();
            restOptions.setEntity(this);
        }

        if (draft.getTracking() != null) {
            tracking = draft.getTracking().copy();
            tracking.setEntity(this);
        }

        entityVersion += 1;
        securityMode = draft.getSecurityMode();

        if (draft.getSecurityMembers() != null) {
            securityMembers = new HashSet(draft.getSecurityMembers());
        }
    }

    @NotPersistent
    public boolean isDraft() {
        return false;
    }

    @NotPersistent
    public AdvancedSettingsDto advancedSettingsDto() {
        AdvancedSettingsDto advancedSettingsDto = new AdvancedSettingsDto();

        RestOptionsDto restDto = (restOptions == null)
                ? new RestOptionsDto()
                : restOptions.toDto();

        List<LookupDto> indexes = new ArrayList<>();
        for (Lookup lookup : getLookups()) {
            indexes.add(lookup.toDto());
        }

        Tracking trackingMapping = getTracking();
        TrackingDto trackingDto = (trackingMapping == null)
                ? new TrackingDto()
                : trackingMapping.toDto();

        advancedSettingsDto.setIndexes(indexes);
        advancedSettingsDto.setEntityId(getId());
        advancedSettingsDto.setBrowsing(getBrowsingSettings().toDto());
        advancedSettingsDto.setRestOptions(restDto);
        advancedSettingsDto.setTracking(trackingDto);

        return advancedSettingsDto;
    }

    public void updateAdvancedSetting(AdvancedSettingsDto advancedSettings) {
        updateIndexes(advancedSettings);
        updateBrowsingSettings(advancedSettings);
        updateRestOptions(advancedSettings);
        updateTracking(advancedSettings);
    }

    private void updateRestOptions(AdvancedSettingsDto advancedSettings) {
        RestOptionsDto dto = advancedSettings.getRestOptions();

        if (null != dto) {
            if (null == restOptions) {
                restOptions = new RestOptions(this);
            }

            restOptions.update(dto);

            for (Lookup lookup : getLookups()) {
                boolean isExposedViaRest = dto.containsLookupId(lookup.getId());
                lookup.setExposedViaRest(isExposedViaRest);
            }

            for (Field field : getFields()) {
                boolean isExposedViaRest = dto.containsFieldId(field.getId());
                field.setExposedViaRest(isExposedViaRest);
            }
        }
    }

    private void updateIndexes(AdvancedSettingsDto advancedSettings) {
        // deletion
        Iterator<Lookup> it = getLookups().iterator();

        while (it.hasNext()) {
            Lookup lookup = it.next();

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
            Lookup lookup = getLookupById(lookupDto.getId());
            List<Field> lookupFields = new ArrayList<>();
            for (Long fieldId : lookupDto.getFieldList()) {
                lookupFields.add(getField(fieldId));
            }

            if (lookup == null) {
                Lookup newLookup = new Lookup(lookupDto, lookupFields);
                addLookup(newLookup);
            } else {
                lookup.update(lookupDto, lookupFields);
            }
        }
    }

    public RestOptions getRestOptions() {
        return restOptions;
    }

    public void setRestOptions(RestOptions restOptions) {
        this.restOptions = restOptions;
    }

    private void updateBrowsingSettings(AdvancedSettingsDto advancedSettings) {
        BrowsingSettingsDto dto = advancedSettings.getBrowsing();

        if (null == dto) {
            dto = new BrowsingSettingsDto();
        }

        for (Field field : getFields()) {
            Long fieldId = field.getId();
            boolean isDisplayed = dto.containsDisplayedField(fieldId);
            boolean isFilterable = dto.containsFilterableField(fieldId);

            field.setUIDisplayable(isDisplayed);
            field.setUIFilterable(isFilterable);

            if (isDisplayed) {
                long position = dto.indexOfDisplayedField(fieldId);
                field.setUIDisplayPosition(position);
            }
        }
    }

    private void updateTracking(AdvancedSettingsDto advancedSettings) {
        TrackingDto trackingDto = advancedSettings.getTracking();

        if (null != trackingDto) {
            if (null == tracking) {
                tracking = new Tracking(this);
            }

            tracking.setAllowCreate(trackingDto.isAllowCreate());
            tracking.setAllowRead(trackingDto.isAllowRead());
            tracking.setAllowUpdate(trackingDto.isAllowUpdate());
            tracking.setAllowDelete(trackingDto.isAllowDelete());

            for (Field field : getFields()) {
                boolean isTracked = trackingDto.getFields().contains(field.getId());
                field.setTracked(isTracked);
            }
        }
    }

    @NotPersistent
    public BrowsingSettings getBrowsingSettings() {
        return new BrowsingSettings(this);
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public void setSecurity(SecurityMode securityMode, List<String> securityMembersList) {
        setSecurityMode(securityMode);

        if (securityMembersList == null) {
            securityMembers = null;
        } else {
            securityMembers = new HashSet(securityMembersList);
        }
    }
}
