package org.motechproject.mds.service.impl;

import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.UserPreferences;
import org.motechproject.mds.dto.UserPreferencesDto;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.field.FieldNotFoundException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllUserPreferences;
import org.motechproject.mds.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        List<String> displayableFields = getDisplayableFields(entity);

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
        assertField(field);
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
        assertField(field);
        userPreferences.unselectField(field);

        allUserPreferences.update(userPreferences);
    }

    @Override
    @Transactional
    public void selectFields(Long id, String username) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        List<Field> fields = new ArrayList<>();
        fields.addAll(entity.getFields());
        userPreferences.setSelectedFields(fields);
        userPreferences.setUnselectedFields(new ArrayList<Field>());

        allUserPreferences.update(userPreferences);
    }

    @Override
    @Transactional
    public void unselectFields(Long id, String username) {
        Entity entity = getEntity(id);
        UserPreferences userPreferences =  allUserPreferences.retrieveByClassNameAndUsername(entity.getClassName(), username);
        userPreferences = checkPreferences(userPreferences, entity, username);

        List<Field> fields = new ArrayList<>();
        fields.addAll(entity.getFields());
        userPreferences.setUnselectedFields(fields);
        userPreferences.setSelectedFields(new ArrayList<Field>());

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

    public void assertField(Field field) {
        if (field == null) {
            throw new FieldNotFoundException();
        }
    }

    private Entity getEntity(Long id) {
        Entity entity = allEntities.retrieveById(id);

        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    private List<String> getDisplayableFields(Entity entity) {
        List<String> displayFields = new ArrayList<>();
        for (Field field : entity.getFields()) {
            if (field.isUIDisplayable() && !field.isNonDisplayable()) {
                displayFields.add(field.getName());
            }
        }

        return displayFields;
    }

    private UserPreferences checkPreferences(UserPreferences userPreferences, Entity entity, String username) {
        if (userPreferences == null) {
            return allUserPreferences.create(new UserPreferences(username, entity.getClassName(), null, new ArrayList<Field>(), new ArrayList<Field>()));
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
