package org.motechproject.mds.service.impl.internal;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.FieldValidation;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.SecuritySettingsDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.motechproject.mds.ex.NoSuchTypeException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllEntityDrafts;
import org.motechproject.mds.repository.AllTypes;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSConstructor;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.FieldHelper;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mds.util.Constants.Packages;

/**
 * Default implementation of {@link org.motechproject.mds.service.EntityService} interface.
 */
@Service
public class EntityServiceImpl extends BaseMdsService implements EntityService {

    private AllEntities allEntities;
    private MDSConstructor constructor;
    private AllTypes allTypes;
    private AllEntityDrafts allEntityDrafts;

    // TODO remove this once everything is in db
    private ExampleData exampleData = new ExampleData();

    @Override
    @Transactional
    public EntityDto createEntity(EntityDto entityDto) throws IOException {
        String packageName = ClassName.getPackage(entityDto.getClassName());
        boolean fromUI = StringUtils.isEmpty(packageName);

        if (fromUI) {
            // in this situation entity.getName() returns a simple name of class
            String className = String.format("%s.%s", Packages.ENTITY, entityDto.getName());
            entityDto.setClassName(className);
        }

        if (allEntities.contains(entityDto.getClassName())) {
            throw new EntityAlreadyExistException();
        }

        Entity entity = allEntities.create(entityDto);

        if (fromUI) {
            constructor.constructEntity(entity);
        }

        return entity.toDto();
    }

    @Override
    @Transactional
    public boolean saveDraftEntityChanges(Long entityId, DraftData draftData) {
        EntityDraft draft = getEntityDraft(entityId);

        if (draftData.isCreate()) {
            createFieldForDraft(draft, draftData);
        } else if (draftData.isEdit()) {
            draftEdit(draft, draftData);
        } else if (draftData.isRemove()) {
            draftRemove(draft, draftData);
        }

        return draft.getChangesMade() != null && draft.getChangesMade();
    }


    private void draftEdit(EntityDraft draft, DraftData draftData) {
        if (draftData.isForAdvanced()) {
            editAdvancedForDraft(draft, draftData);
        } else if (draftData.isForField()) {
            editFieldForDraft(draft, draftData);
        }
    }

    private void editFieldForDraft(EntityDraft draft, DraftData draftData) {
        String fieldIdStr = draftData.getValue(DraftData.FIELD_ID).toString();

        if (StringUtils.isNotBlank(fieldIdStr)) {
            Long fieldId = Long.valueOf(fieldIdStr);
            Field field = draft.getField(fieldId);

            if (field != null) {
                String path = draftData.getPath();
                List value = (List) draftData.getValue(DraftData.VALUE);

                // Convert to dto for UI updates
                FieldDto dto = field.toDto();
                FieldHelper.setField(dto, path, value);

                // Perform update
                field.update(dto);
                allEntityDrafts.update(draft);
            }
        }
    }

    private void editAdvancedForDraft(EntityDraft draft, DraftData draftData) {
        AdvancedSettingsDto advancedDto = draft.advancedSettingsDto();
        String path = draftData.getPath();
        List value = (List) draftData.getValue(DraftData.VALUE);

        FieldHelper.setField(advancedDto, path, value);

        draft.updateAdvancedSetting(advancedDto);

        allEntityDrafts.update(draft);
    }

    private void createFieldForDraft(EntityDraft draft, DraftData draftData) {
        String typeClass = draftData.getValue(DraftData.TYPE_CLASS).toString();
        String displayName = draftData.getValue(DraftData.DISPLAY_NAME).toString();
        String name = draftData.getValue(DraftData.NAME).toString();

        Type type = allTypes.retrieveByClassName(typeClass);

        if (type == null) {
            throw new NoSuchTypeException();
        }


        Field field = new Field(draft, displayName, name);
        field.setType(type);

        if (type.hasSettings()) {
            for (TypeSetting setting : type.getSettings()) {
                field.addSetting(new FieldSetting(field, setting));
            }
        }

        if (type.hasValidation()) {
            for (TypeValidation validation : type.getValidations()) {
                field.addValidation(new FieldValidation(field, validation));
            }
        }

        draft.addField(field);

        allEntityDrafts.update(draft);
    }


    private void draftRemove(EntityDraft draft, DraftData draftData) {
        Long fieldId = Long.valueOf(draftData.getValue(DraftData.FIELD_ID).toString());
        draft.removeField(fieldId);
        allEntityDrafts.update(draft);
    }


    @Override
    @Transactional
    public void abandonChanges(Long entityId) {
        EntityDraft draft = getEntityDraft(entityId);
        if (draft != null) {
            allEntityDrafts.delete(draft);
        }
    }

    @Override
    @Transactional
    public void commitChanges(Long entityId) {
        EntityDraft draft = getEntityDraft(entityId);
        Entity parent = draft.getParentEntity();

        parent.updateFromDraft(draft);

        allEntityDrafts.delete(draft);
    }


    @Override
    @Transactional
    public List<EntityDto> listWorkInProgress() {
        String username = getUsername();

        if (username == null) {
            throw new AccessDeniedException("Cannot retrieve work in progress - no user");
        }

        List<EntityDraft> drafts = allEntityDrafts.retrieveAll(username);

        List<EntityDto> entityDtoList = new ArrayList<>();
        for (EntityDraft draft : drafts) {
            if (draft.getChangesMade() != null && draft.getChangesMade()) {
                entityDtoList.add(draft.toDto());
            }
        }

        return entityDtoList;
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecords(Long entityId) {
        return exampleData.getEntityRecordsById(entityId);
    }

    @Override
    @Transactional
    public AdvancedSettingsDto getAdvancedSettings(Long entityId) {
        return getAdvancedSettings(entityId, false);
    }

    @Override
    @Transactional
    public AdvancedSettingsDto getAdvancedSettings(Long entityId, boolean committed) {
        if (committed) {
            Entity entity = allEntities.retrieveById(entityId);
            return entity.advancedSettingsDto();
        } else {
            Entity entity = getEntityDraft(entityId);
            return entity.advancedSettingsDto();
        }
    }

    @Override
    @Transactional
    public void addLookupToEntity(Long entityId, LookupDto lookup) {
        Entity entity = allEntities.retrieveById(entityId);
        entity.addLookup(new Lookup(lookup));
    }

    @Override
    @Transactional
    public SecuritySettingsDto getSecuritySettings(Long entityId) {
        return exampleData.getSecurity(entityId);
    }

    @Override
    @Transactional
    public List<FieldInstanceDto> getInstanceFields(Long instanceId) {
        return exampleData.getInstanceFields(instanceId);
    }

    @Override
    @Transactional
    public List<HistoryRecord> getInstanceHistory(Long instanceId) {
        return exampleData.getInstanceHistoryRecordsById(instanceId);
    }

    @Override
    @Transactional
    public List<PreviousRecord> getPreviousRecords(Long instanceId) {
        return exampleData.getPreviousRecordsById(instanceId);
    }

    @Override
    @Transactional
    public void deleteEntity(Long entityId) {
        Entity entity = allEntities.retrieveById(entityId);

        assertWritableEntity(entity);

        if (entity.isDraft()) {
            entity = ((EntityDraft) entity).getParentEntity();
        }

        allEntityDrafts.deleteAll(entity);
        allEntities.delete(entity);
    }

    @Override
    @Transactional
    public List<EntityDto> listEntities() {
        List<EntityDto> entityDtos = new ArrayList<>();

        for (Entity entity : allEntities.retrieveAll()) {
            if (!entity.isDraft()) {
                entityDtos.add(entity.toDto());
            }
        }

        return entityDtos;
    }

    @Override
    @Transactional
    public EntityDto getEntity(Long entityId) {
        Entity entity = allEntities.retrieveById(entityId);
        return (entity == null) ? null : entity.toDto();
    }

    @Override
    @Transactional
    public EntityDto getEntityByClassName(String className) {
        Entity entity = allEntities.retrieveByClassName(className);
        return (entity == null) ? null : entity.toDto();
    }

    @Override
    @Transactional
    public List<FieldDto> getFields(Long entityId) {
        Entity entity = getEntityDraft(entityId);

        List<Field> fields = entity.getFields();

        List<FieldDto> fieldDtos = new ArrayList<>();
        for (Field field : fields) {
            fieldDtos.add(field.toDto());
        }

        return fieldDtos;
    }

    @Override
    @Transactional
    public FieldDto findFieldByName(Long entityId, String name) {
        Entity entity = getEntityDraft(entityId);

        Field field = entity.getField(name);

        if (field == null) {
            throw new FieldNotFoundException();
        }

        return field.toDto();
    }

    @Override
    @Transactional
    public EntityDto getEntityForEdit(Long entityId) {
        Entity draft = getEntityDraft(entityId);
        return draft.toDto();
    }

    private String getUsername() {
        String username = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User user = (User) auth.getPrincipal();
            if (user != null) {
                username = user.getUsername();
            }
        }

        return username;
    }

    public EntityDraft getEntityDraft(Long entityId) {
        Entity entity = allEntities.retrieveById(entityId);

        assertEntityExists(entity);

        if (entity instanceof EntityDraft) {
            return (EntityDraft) entity;
        }

        // get the user
        String username = getUsername();

        if (username == null) {
            throw new AccessDeniedException("Cannot save draft - no user");
        }

        // get the draft
        EntityDraft draft = allEntityDrafts.retrieve(entity, username);

        if (draft == null) {
            draft = allEntityDrafts.create(entity, username);
        }

        return draft;
    }

    @Override
    @Transactional
    public void addFields(EntityDto entityDto, List<FieldDto> fields) {
        Entity entity = allEntities.retrieveById(entityDto.getId());

        assertEntityExists(entity);

        for (FieldDto fieldDto : fields) {
            FieldBasicDto basic = fieldDto.getBasic();
            String typeClass = fieldDto.getType().getTypeClass();

            Type type = allTypes.retrieveByClassName(typeClass);
            Field field = new Field(
                    entity, basic.getDisplayName(), basic.getName(), basic.isRequired(),
                    (String) basic.getDefaultValue(), basic.getTooltip()
            );
            field.setType(type);

            entity.addField(field);
        }
    }

    private void assertEntityExists(Entity entity) {
        if (entity == null) {
            throw new EntityNotFoundException();
        }
    }

    private void assertWritableEntity(Entity entity) {
        assertEntityExists(entity);

        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setConstructor(MDSConstructor constructor) {
        this.constructor = constructor;
    }

    @Autowired
    public void setAllTypes(AllTypes allTypes) {
        this.allTypes = allTypes;
    }

    @Autowired
    public void setAllEntityDrafts(AllEntityDrafts allEntityDrafts) {
        this.allEntityDrafts = allEntityDrafts;
    }

}
