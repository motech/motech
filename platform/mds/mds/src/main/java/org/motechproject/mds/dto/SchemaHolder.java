package org.motechproject.mds.dto;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.util.TypeHelper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class that holds the MDS schema - entities, types, fields, lookups, advanced settings and so on.
 * Used during MDS processing in order to avoid repeatedly querying the database.
 */
public class SchemaHolder {

    private Map<String, EntityHolder> entityMap = new HashMap<>();
    private Map<String, TypeDto> types = new HashMap<>();
    private Map<String, List<TypeValidationDto>> typeValidations = new HashMap<>();

    public void addEntity(EntityDto entity, AdvancedSettingsDto advancedSettings,
                          List<FieldDto> fields) {
        EntityHolder entityHolder = new EntityHolder(entity, advancedSettings, fields);
        entityMap.put(entityHolder.getEntityClassName(), entityHolder);
    }

    public void addType(TypeDto type) {
        types.put(type.getTypeClass(), type);
    }

    public void addTypeValidation(TypeDto type, List<TypeValidationDto> validations) {
        typeValidations.put(type.getTypeClass(), validations);
    }

    public TypeDto getType(Class typeClass) {
        String className = TypeHelper.getClassNameForMdsType(typeClass);
        return types.get(className);
    }

    public TypeDto getType(String typeClass) {
        return types.get(typeClass);
    }

    public List<EntityDto> getAllEntities() {
        List<EntityDto> entities = new ArrayList<>();
        for (EntityHolder entityHolder : entityMap.values()) {
            entities.add(entityHolder.getEntity());
        }
        return entities;
    }

    public EntityDto getEntityByClassName(String className) {
        EntityHolder entityHolder = entityMap.get(className);
        return entityHolder == null ? null : entityHolder.getEntity();
    }

    public AdvancedSettingsDto getAdvancedSettings(EntityDto entity) {
        return getAdvancedSettings(entity.getClassName());
    }

    public AdvancedSettingsDto getAdvancedSettings(String className) {
        EntityHolder entityHolder = entityMap.get(className);
        return entityHolder == null ? null : entityHolder.getAdvancedSettings();
    }

    public List<FieldDto> getFields(EntityDto entity) {
        return getFields(entity.getClassName());
    }

    public List<FieldDto> getFields(String entityClassName) {
        EntityHolder entityHolder = entityMap.get(entityClassName);
        return entityHolder == null ? null : entityHolder.getFields();
    }

    public List<LookupDto> getLookups(EntityDto entity) {
        return getLookups(entity.getClassName());
    }

    public List<LookupDto> getLookups(String entityClassName) {
        EntityHolder entityHolder = entityMap.get(entityClassName);
        return entityHolder == null ? null : entityHolder.getLookups();
    }

    public FieldDto getFieldByName(EntityDto entity, String fieldName) {
        return getFieldByName(entity.getClassName(), fieldName);
    }

    public FieldDto getFieldByName(String entityClassName, String fieldName) {
        List<FieldDto> fields = getFields(entityClassName);

        if (fields == null) {
            throw new EntityNotFoundException(entityClassName);
        }

        for (FieldDto field : fields) {
            if (StringUtils.equals(fieldName, field.getBasic().getName())) {
                return field;
            }
        }

        return null;
    }

    public List<TypeValidationDto> findValidations(String typeClass, Class<? extends Annotation> aClass) {
        List<TypeValidationDto> result = new ArrayList<>();

        if (typeValidations.containsKey(typeClass)) {
            for (TypeValidationDto validation : typeValidations.get(typeClass)) {
                if (validation.getAnnotations().contains(aClass)) {
                    result.add(validation);
                }
            }
        }

        return result;
    }

    private class EntityHolder {

        private EntityDto entity;
        private AdvancedSettingsDto advancedSettings;
        private List<FieldDto> fields;

        public EntityHolder(EntityDto entity, AdvancedSettingsDto advancedSettings,
                            List<FieldDto> fields) {
            this.entity = entity;
            this.advancedSettings = advancedSettings;
            this.fields = fields;
        }

        public EntityDto getEntity() {
            return entity;
        }

        public void setEntity(EntityDto entity) {
            this.entity = entity;
        }

        public AdvancedSettingsDto getAdvancedSettings() {
            return advancedSettings;
        }

        public void setAdvancedSettings(AdvancedSettingsDto advancedSettings) {
            this.advancedSettings = advancedSettings;
        }

        public List<FieldDto> getFields() {
            return fields;
        }

        public void setFields(List<FieldDto> fields) {
            this.fields = fields;
        }

        public final String getEntityClassName() {
            return entity.getClassName();
        }

        public final Long getEntityId() {
            return entity.getId();
        }

        public List<LookupDto> getLookups() {
            return getAdvancedSettings().getIndexes();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EntityHolder)) {
                return false;
            }

            EntityHolder that = (EntityHolder) o;

            return StringUtils.equals(getEntityClassName(), that.getEntityClassName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getEntityClassName());
        }
    }
}
