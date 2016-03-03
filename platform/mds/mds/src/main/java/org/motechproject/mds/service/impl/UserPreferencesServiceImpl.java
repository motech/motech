package org.motechproject.mds.service.impl;

import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.UserPreferences;
import org.motechproject.mds.dto.UserPreferencesDto;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.exception.field.FieldNotFoundException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllUserPreferences;
import org.motechproject.mds.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link org.motechproject.mds.service.UserPreferencesService}.
 */
@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private AllUserPreferences allUserPreferences;

    private AllEntities allEntities;

    private SettingsService settingsService;

    @Override
    public List<UserPreferencesDto> getEntityPreferences(Long id) {
        Entity entity = getEntity(id);
        List<UserPreferences> userPreferences =  allUserPreferences.retrieveByClassName(entity.getClassName());
        List<UserPreferencesDto> dtos = new ArrayList<>();
        Set<String> displayableFields = getDisplayableFields(entity);

        if (userPreferences != null) {
            Integer defaultGridSize = settingsService.getGridSize();
            for (UserPreferences preferences : userPreferences) {
                UserPreferencesDto dto = preferences.toDto(displayableFields);
                if (dto.getGridRowsNumber() == null) {
                    dto.setGridRowsNumber(defaultGridSize);
                }
                dtos.add(dto);
            }
        }

        return dtos;
    }

    @Override
    @Transactional
    public UserPreferencesDto getUserPreferences(Long id, String username) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        if (userPreferences != null) {
            UserPreferencesDto dto = userPreferences.toDto(getDisplayableFields(entity));
            if (dto.getGridRowsNumber() == null) {
                dto.setGridRowsNumber(settingsService.getGridSize());
            }
            return dto;
        }

        return null;
    }

    @Override
    @Transactional
    public void updateGridSize(Long id, String username, Integer newSize) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        if (userPreferences.getGridRowsNumber() != newSize) {
            userPreferences.setGridRowsNumber(newSize == null ? settingsService.getGridSize() : newSize);
            allUserPreferences.update(userPreferences);
        }
    }

    @Override
    @Transactional
    public void selectField(Long id, String username, String fieldName) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        Field field = entity.getField(fieldName);
        assertField(field, entity.getClassName(), fieldName);
        userPreferences.selectField(field);

        allUserPreferences.update(userPreferences);
    }

    @Override
    @Transactional
    public void unselectField(Long id, String username, String fieldName) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        Field field = entity.getField(fieldName);
        assertField(field, entity.getClassName(), fieldName);
        userPreferences.unselectField(field);

        allUserPreferences.update(userPreferences);
    }

    @Override
    @Transactional
    public void selectFields(Long id, String username) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        Set<Field> fields = new HashSet<>();
        fields.addAll(entity.getFields());
        userPreferences.setSelectedFields(fields);
        userPreferences.setUnselectedFields(new HashSet<Field>());

        allUserPreferences.update(userPreferences);
    }

    @Override
    @Transactional
    public void unselectFields(Long id, String username) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        Set<Field> fields = new HashSet<>();
        fields.addAll(entity.getFields());
        userPreferences.setUnselectedFields(fields);
        userPreferences.setSelectedFields(new HashSet<Field>());

        allUserPreferences.update(userPreferences);
    }

    @Override
    @Transactional
    public void removeUserPreferences(Long id, String username) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        if (userPreferences != null) {
            allUserPreferences.delete(userPreferences);
        }
    }

    public void assertField(Field field, String className, String fieldName) {
        if (field == null) {
            throw new FieldNotFoundException(className, fieldName);
        }
    }

    private Entity getEntity(Long id) {
        Entity entity = allEntities.retrieveById(id);

        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    private Set<String> getDisplayableFields(Entity entity) {
        Set<String> displayFields = new HashSet<>();
        for (Field field : entity.getFields()) {
            if (field.isUIDisplayable() && !field.isNonDisplayable()) {
                displayFields.add(field.getName());
            }
        }

        return displayFields;
    }

    private UserPreferences checkPreferences(UserPreferences userPreferences, Entity entity, String username) {
        if (userPreferences == null) {
            return allUserPreferences.create(new UserPreferences(username, entity.getClassName(), null, new HashSet<Field>(), new HashSet<Field>()));
        }
        return userPreferences;
    }
    @Autowired
    public void setAllUserPreferences(AllUserPreferences allUserPreferences) {
        this.allUserPreferences = allUserPreferences;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

}
