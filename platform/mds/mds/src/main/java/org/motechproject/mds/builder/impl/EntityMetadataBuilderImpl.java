package org.motechproject.mds.builder.impl;

import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SchemaHolder;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.exception.MdsException;
import org.motechproject.mds.helper.ClassTableName;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.ForeignKeyAction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Value;
import javax.jdo.annotations.Version;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.CollectionMetadata;
import javax.jdo.metadata.ColumnMetadata;
import javax.jdo.metadata.ElementMetadata;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.ForeignKeyMetadata;
import javax.jdo.metadata.IndexMetadata;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.JoinMetadata;
import javax.jdo.metadata.MapMetadata;
import javax.jdo.metadata.MemberMetadata;
import javax.jdo.metadata.PackageMetadata;
import javax.jdo.metadata.UniqueMetadata;
import javax.jdo.metadata.ValueMetadata;
import javax.jdo.metadata.VersionMetadata;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.mds.util.Constants.MetadataKeys.DATABASE_COLUMN_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;
import static org.motechproject.mds.util.Constants.Util.FALSE;
import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.INSTANCE_VERSION_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.TRUE;
import static org.motechproject.mds.util.Constants.Util.VALUE_GENERATOR;


/**
 * The <code>EntityMetadataBuilderImpl</code> class is responsible for building jdo metadata for an
 * entity class.
 */
@Component
public class EntityMetadataBuilderImpl implements EntityMetadataBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityMetadataBuilderImpl.class);

    private static final String[] FIELD_VALUE_GENERATOR = new String[]{
            CREATOR_FIELD_NAME, OWNER_FIELD_NAME, CREATION_DATE_FIELD_NAME,
            MODIFIED_BY_FIELD_NAME, MODIFICATION_DATE_FIELD_NAME
    };

    private static final String ID_SUFFIX = "_ID";

    @Override
    public void addEntityMetadata(JDOMetadata jdoMetadata, EntityDto entity, Class<?> definition, SchemaHolder schemaHolder) {
        String className = (entity.isDDE()) ? entity.getClassName() : ClassName.getEntityClassName(entity.getClassName());
        String packageName = ClassName.getPackage(className);
        String tableName = ClassTableName.getTableName(entity.getClassName(), entity.getModule(), entity.getNamespace(), entity.getTableName(), null);

        PackageMetadata pmd = getPackageMetadata(jdoMetadata, packageName);
        ClassMetadata cmd = getClassMetadata(pmd, ClassName.getSimpleName(ClassName.getEntityClassName(entity.getClassName())));

        cmd.setTable(tableName);
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.APPLICATION);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        addInheritanceMetadata(cmd, definition);

        if (!entity.isSubClassOfMdsEntity() && !entity.isSubClassOfMdsVersionedEntity()) {
            addIdField(cmd, entity, schemaHolder, definition);
            //we add versioning metadata only for Standard class.
            addVersioningMetadata(cmd, definition);
        }

        addMetadataForFields(cmd, null, entity, EntityType.STANDARD, definition, schemaHolder);
    }

    @Override
    public void addHelperClassMetadata(JDOMetadata jdoMetadata, ClassData classData,
                                       EntityDto entity, EntityType entityType, Class<?> definition,
                                       SchemaHolder schemaHolder) {
        String packageName = ClassName.getPackage(classData.getClassName());
        String simpleName = ClassName.getSimpleName(classData.getClassName());
        String tableName = ClassTableName.getTableName(classData.getClassName(), classData.getModule(), classData.getNamespace(),
                entity == null ? "" : entity.getTableName(), entityType);

        PackageMetadata pmd = getPackageMetadata(jdoMetadata, packageName);
        ClassMetadata cmd = getClassMetadata(pmd, simpleName);

        cmd.setTable(tableName);
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.APPLICATION);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        InheritanceMetadata imd = cmd.newInheritanceMetadata();
        imd.setCustomStrategy("complete-table");

        addIdField(cmd, classData.getClassName(), definition);

        if (entity != null) {
            addMetadataForFields(cmd, classData, entity, entityType, definition, schemaHolder);
        }
    }

    @Override
    public void fixEnhancerIssuesInMetadata(JDOMetadata jdoMetadata, SchemaHolder schemaHolder) {
        for (PackageMetadata pmd : jdoMetadata.getPackages()) {
            for (ClassMetadata cmd : pmd.getClasses()) {
                String className = String.format("%s.%s", pmd.getName(), cmd.getName());
                EntityType entityType = EntityType.forClassName(className);

                if (entityType == EntityType.STANDARD) {

                    EntityDto entity = schemaHolder.getEntityByClassName(className);

                    if (null != entity) {
                        for (MemberMetadata mmd : cmd.getMembers()) {
                            CollectionMetadata collMd = mmd.getCollectionMetadata();
                            FieldDto field = schemaHolder.getFieldByName(entity, mmd.getName());

                            if (null != collMd) {
                                fixCollectionMetadata(collMd, field);
                            }

                            //Defining column name for join and element results in setting it both as XML attribute and child element
                            fixDuplicateColumnDefinitions(mmd);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addBaseMetadata(JDOMetadata jdoMetadata, ClassData classData, EntityType entityType, Class<?> definition) {
        addHelperClassMetadata(jdoMetadata, classData, null, entityType, definition, null);
    }

    private void addVersioningMetadata(ClassMetadata cmd, Class<?> definition) {
        Class<Version> ann = ReflectionsUtil.getAnnotationClass(definition, Version.class);
        Version versionAnnotation = AnnotationUtils.findAnnotation(definition, ann);

        if (versionAnnotation != null) {
            VersionMetadata vmd = cmd.newVersionMetadata();
            vmd.setColumn(versionAnnotation.column());
            vmd.setStrategy(versionAnnotation.strategy());
            if (versionAnnotation.extensions().length == 0 || !"field-name".equals(versionAnnotation.extensions()[0].key())) {
                throw new MdsException(String.format("Cannot create metadata fo %s. Extension not found in @Version annotation.", cmd.getName()));
            }
            Extension extension = versionAnnotation.extensions()[0];
            vmd.newExtensionMetadata(DATANUCLEUS, "field-name", extension.value());
        }
    }


    private void fixCollectionMetadata(CollectionMetadata collMd, FieldDto field) {
        String elementType = collMd.getElementType();
        RelationshipHolder holder = new RelationshipHolder(field);

        if (null != MotechClassPool.getEnhancedClassData(elementType)) {
            collMd.setEmbeddedElement(false);
        }
        if (holder.isOneToMany()) {
            collMd.setDependentElement(holder.isCascadeDelete());
        }
    }

    private void fixDuplicateColumnDefinitions(MemberMetadata mmd) {
        JoinMetadata jmd = mmd.getJoinMetadata();
        ElementMetadata emd = mmd.getElementMetadata();

        if (jmd != null && ArrayUtils.isNotEmpty(jmd.getColumns()) && StringUtils.isNotEmpty(jmd.getColumn())) {
            jmd.setColumn(null);
        }

        if (emd != null && ArrayUtils.isNotEmpty(emd.getColumns()) && StringUtils.isNotEmpty(emd.getColumn())) {
            emd.setColumn(null);
        }
    }

    private void addInheritanceMetadata(ClassMetadata cmd, Class<?> definition) {
        Class<Inheritance> ann = ReflectionsUtil.getAnnotationClass(definition, Inheritance.class);
        Inheritance annotation = AnnotationUtils.findAnnotation(definition, ann);

        if (annotation == null) {
            InheritanceMetadata imd = cmd.newInheritanceMetadata();
            imd.setCustomStrategy("complete-table");
        }
    }

    private void addDefaultFetchGroupMetadata(FieldMetadata fmd, Class<?> definition) {
        java.lang.reflect.Field field = FieldUtils.getField(definition, fmd.getName(), true);

        if (field == null) {
            LOGGER.warn("Unable to retrieve field {} from class {}. Putting the field in the default fetch group by default.",
                    fmd.getName(), definition.getName());
            fmd.setDefaultFetchGroup(true);
        } else {
            Persistent persistentAnnotation = ReflectionsUtil.getAnnotationSelfOrAccessor(field, Persistent.class);

            // set to true, unless there is a JDO annotation that specifies otherwise
            if (persistentAnnotation == null || StringUtils.isBlank(persistentAnnotation.defaultFetchGroup())) {
                fmd.setDefaultFetchGroup(true);
            }
        }
    }

    private void addMetadataForFields(ClassMetadata cmd, ClassData classData, EntityDto entity,
                                      EntityType entityType, Class<?> definition, SchemaHolder schemaHolder) {
        List<FieldDto> fields = schemaHolder.getFields(entity);

        for (FieldDto field : fields) {
            if (field.isVersionField() && entityType != EntityType.STANDARD) {
                continue;
            }

            String fieldName = getNameForMetadata(field);
            processField(cmd, classData, entity, entityType, definition, fieldName, field, schemaHolder);
        }
    }

    public void processField(ClassMetadata cmd, ClassData classData, EntityDto entity, EntityType entityType,
                             Class<?> definition, String fieldName, FieldDto field, SchemaHolder schemaHolder) {
        // Metadata for ID field has been added earlier in addIdField() method
        if (!fieldName.equals(ID_FIELD_NAME)) {
            FieldMetadata fmd = null;

            if (isFieldNotInherited(fieldName, entity, schemaHolder)) {
                fmd = setFieldMetadata(cmd, classData, entity, entityType, field, definition);
            }
            // when field is in Lookup, we set field metadata indexed to retrieve instance faster
            if (!field.getLookups().isEmpty() && entityType.equals(EntityType.STANDARD)) {
                if (fmd == null) {
                    String inheritedFieldName = ClassName.getSimpleName(entity.getSuperClass()) + "." + fieldName;
                    fmd = cmd.newFieldMetadata(inheritedFieldName);
                }

                if (!isBlobOrClob(field)) {
                    IndexMetadata imd = getOrCreateIndexMetadata(fmd);
                    imd.setName(KeyNames.lookupIndexKeyName(entity.getName(), entity.getId(), fieldName, entityType));
                }

                fmd.setIndexed(false);
                for (LookupDto lookupDto : field.getLookups()) {
                    if (lookupDto.isIndexRequired()) {
                        fmd.setIndexed(true);
                        break;
                    }
                }

            }
            if (fmd != null) {
                customizeFieldMd(fmd, entity, field, entityType, definition);
            }
        }
    }

    private boolean isFieldRequired(FieldDto field, EntityType entityType) {
        return field.getBasic().isRequired() && !(entityType.equals(EntityType.TRASH) && field.getType().isRelationship());
    }

    private void customizeFieldMd(FieldMetadata fmd, EntityDto entity, FieldDto field, EntityType entityType,
                                  Class<?> definition) {
        setColumnParameters(fmd, field, definition);
        // Check whether the field is required and set appropriate metadata
        fmd.setNullValue(isFieldRequired(field, entityType) ? NullValue.EXCEPTION : NullValue.NONE);
        // Non DDE fields have controllable unique
        if (!field.isReadOnly() && entityType == EntityType.STANDARD && field.getBasic().isUnique()) {
            UniqueMetadata umd = fmd.newUniqueMetadata();
            // TODO: Move to KeyNames class (to be introduced in MOTECH-1991)
            umd.setName(KeyNames.uniqueKeyName(entity.getName(), getNameForMetadata(field)));
        }
    }

    private boolean isFieldNotInherited(String fieldName, EntityDto entity, SchemaHolder schemaHolder) {
        if ((entity.isSubClassOfMdsEntity() || entity.isSubClassOfMdsVersionedEntity()) && (ArrayUtils.contains(FIELD_VALUE_GENERATOR, fieldName))
                || isVersionFieldFromMdsVersionedEntity(entity, fieldName)) {
            return false;
        } else {
            // return false if it is inherited field from superclass
            return entity.isBaseEntity() || !isFieldFromSuperClass(entity.getSuperClass(), fieldName, schemaHolder);
        }
    }

    private boolean isVersionFieldFromMdsVersionedEntity(EntityDto entity, String fieldName) {
        return entity.isSubClassOfMdsVersionedEntity() && INSTANCE_VERSION_FIELD_NAME.equals(fieldName);
    }
    private boolean isFieldFromSuperClass(String className, String fieldName, SchemaHolder schemaHolder) {
        return schemaHolder.getFieldByName(className, fieldName) != null;
    }

    private FieldMetadata setFieldMetadata(ClassMetadata cmd, ClassData classData, EntityDto entity,
                                           EntityType entityType, FieldDto field, Class<?> definition) {
        String name = getNameForMetadata(field);

        TypeDto type = field.getType();
        Class<?> typeClass = type.getClassObjectForType();

        if (ArrayUtils.contains(FIELD_VALUE_GENERATOR, name)) {
            return setAutoGenerationMetadata(cmd, name);
        } else if (type.isCombobox()) {
            return setComboboxMetadata(cmd, entity, field, definition, entityType);
        } else if (type.isRelationship()) {
            return setRelationshipMetadata(cmd, classData, entity, field, entityType, definition);
        } else if (Map.class.isAssignableFrom(typeClass)) {
            return setMapMetadata(cmd, entity, field, definition, entityType);
        } else if (Time.class.isAssignableFrom(typeClass)) {
            return setTimeMetadata(cmd, name);
        }
        return cmd.newFieldMetadata(name);
    }

    private void setColumnParameters(FieldMetadata fmd, FieldDto field, Class<?> definition) {
        Value valueAnnotation = null;
        java.lang.reflect.Field fieldDefinition = FieldUtils.getDeclaredField(definition, field.getBasic().getName(), true);
        //@Value in datanucleus is used with maps.
        if (fieldDefinition != null && java.util.Map.class.isAssignableFrom(field.getType().getClassObjectForType())) {
            valueAnnotation = ReflectionsUtil.getAnnotationSelfOrAccessor(fieldDefinition, Value.class);
        }

        if ((field.getMetadata(DATABASE_COLUMN_NAME) != null || field.getSetting(Constants.Settings.STRING_MAX_LENGTH) != null
                || field.getSetting(Constants.Settings.STRING_TEXT_AREA) != null) || (valueAnnotation != null)) {
            addColumnMetadata(fmd, field, valueAnnotation);
        }
    }

    private void addColumnMetadata(FieldMetadata fmd, FieldDto field, Value valueAnnotation) {
        SettingDto maxLengthSetting = field.getSetting(Constants.Settings.STRING_MAX_LENGTH);
        ColumnMetadata colMd = fmd.newColumnMetadata();
        // text(clob) fields don't have length
        if (maxLengthSetting != null && !isClob(field)) {
            colMd.setLength(Integer.parseInt(maxLengthSetting.getValueAsString()));
        }

        // if TextArea then change length
        if (field.getSetting(Constants.Settings.STRING_TEXT_AREA) != null &&
                "true".equalsIgnoreCase(field.getSetting(Constants.Settings.STRING_TEXT_AREA).getValueAsString())) {
            fmd.setIndexed(false);
            colMd.setSQLType("CLOB");
        }
        if (field.getMetadata(DATABASE_COLUMN_NAME) != null) {
            colMd.setName(field.getMetadata(DATABASE_COLUMN_NAME).getValue());
        }

        if (valueAnnotation != null) {
            copyParametersFromValueAnnotation(fmd, valueAnnotation);
        }
    }

    private void copyParametersFromValueAnnotation(FieldMetadata fmd, Value valueAnnotation) {
        ValueMetadata valueMetadata = fmd.newValueMetadata();
        for (Column column : valueAnnotation.columns()) {
            ColumnMetadata colMd = valueMetadata.newColumnMetadata();
            colMd.setName(column.name());
            colMd.setLength(column.length());
            colMd.setAllowsNull(Boolean.parseBoolean(column.allowsNull()));
            colMd.setDefaultValue(column.defaultValue());
            colMd.setInsertValue(column.insertValue());
            colMd.setJDBCType(column.jdbcType());
            colMd.setSQLType(column.sqlType());
        }
    }

    private FieldMetadata setTimeMetadata(ClassMetadata cmd, String name) {
        // for time we register our converter which persists as string
        FieldMetadata fmd = cmd.newFieldMetadata(name);

        fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
        fmd.setDefaultFetchGroup(true);
        fmd.newExtensionMetadata(DATANUCLEUS, "type-converter-name", "dn.time-string");
        return fmd;
    }

    private FieldMetadata setMapMetadata(ClassMetadata cmd, EntityDto entity, FieldDto field,
                                         Class<?> definition, EntityType entityType) {
        FieldMetadata fmd = cmd.newFieldMetadata(getNameForMetadata(field));

        MetadataDto keyMetadata = field.getMetadata(MAP_KEY_TYPE);
        MetadataDto valueMetadata = field.getMetadata(MAP_VALUE_TYPE);

        boolean serialized = shouldSerializeMap(keyMetadata, valueMetadata);

        // Depending on the types of key and value of the map we either serialize the map or create a separate table for it
        fmd.setSerialized(serialized);

        addDefaultFetchGroupMetadata(fmd, definition);

        MapMetadata mmd = fmd.newMapMetadata();

        if (serialized) {
            mmd.setSerializedKey(true);
            mmd.setSerializedValue(true);
        } else {
            mmd.setKeyType(keyMetadata.getValue());
            mmd.setValueType(valueMetadata.getValue());

            fmd.setTable(ClassTableName.getTableName(cmd.getTable(), getNameForMetadata(field)));
            JoinMetadata jmd = fmd.newJoinMetadata();

            ForeignKeyMetadata fkmd = getOrCreateFkMetadata(jmd);
            fkmd.setDeleteAction(ForeignKeyAction.CASCADE);

            fkmd.setName(KeyNames.mapForeignKeyName(entity.getName(), entity.getId(), field.getBasic().getName(),
                    entityType));
        }
        return fmd;
    }

    private boolean shouldSerializeMap(MetadataDto keyMetadata, MetadataDto valueMetadata) {
        // If generics types of map are not supported in MDS, we serialized the field in DB.
        return keyMetadata == null || valueMetadata == null ||
                !(TypeHelper.isTypeSupportedInMap(keyMetadata.getValue(), true) &&
                        TypeHelper.isTypeSupportedInMap(valueMetadata.getValue(), false));
    }

    private FieldMetadata setRelationshipMetadata(ClassMetadata cmd, ClassData classData,
                                                  EntityDto entity, FieldDto field,
                                                  EntityType entityType, Class<?> definition) {

        RelationshipHolder holder = new RelationshipHolder(classData, field);
        FieldMetadata fmd = cmd.newFieldMetadata(getNameForMetadata(field));

        addDefaultFetchGroupMetadata(fmd, definition);

        if (entityType == EntityType.STANDARD) {
            processRelationship(fmd, holder, entity, field, definition);
        } else {
            processHistoryTrashRelationship(cmd, fmd, holder);
        }

        return fmd;
    }

    private void processRelationship(FieldMetadata fmd, RelationshipHolder holder,
                                     EntityDto entity, FieldDto field,
                                     Class<?> definition) {
        String relatedClass = holder.getRelatedClass();

        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-persist", holder.isCascadePersist() ? TRUE : FALSE);
        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-update", holder.isCascadeUpdate() ? TRUE : FALSE);

        if (holder.isOneToMany() || holder.isManyToMany()) {
            setUpCollectionMetadata(fmd, relatedClass, holder, EntityType.STANDARD);
        } else if (holder.isOneToOne()) {
            processOneToOneRelationship(fmd, holder);
        }
        
        if (shouldSetNullDelete(holder, field)) {
            ForeignKeyMetadata fkmd = getOrCreateRelFkMetadata(fmd, entity, field);
            fkmd.setDeleteAction(ForeignKeyAction.NULL);
        }

        if (holder.isManyToMany()) {
            addManyToManyMetadata(fmd, holder, entity, field, definition);
        }

        if (shouldSetCascadeDelete(holder, EntityType.STANDARD)) {
            ForeignKeyMetadata fkmd = getOrCreateFkMetadata(fmd);
            fkmd.setDeleteAction(ForeignKeyAction.CASCADE);
        }
    }

    private void processOneToOneRelationship(FieldMetadata fmd, RelationshipHolder holder) {
        fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
        fmd.setDependent(holder.isCascadeDelete());
    }

    private void processHistoryTrashRelationship(ClassMetadata cmd, FieldMetadata fmd, RelationshipHolder holder) {
        if (holder.isOneToOne() || holder.isManyToOne()) {
            fmd.setColumn(holder.getFieldName() + ID_SUFFIX);
        } else {
            fmd.setTable(cmd.getTable() + '_' + holder.getFieldName());

            CollectionMetadata collMd = fmd.newCollectionMetadata();
            collMd.setElementType(Long.class.getName());

            JoinMetadata joinMd = fmd.newJoinMetadata();
            ColumnMetadata joinColumnMd = joinMd.newColumnMetadata();
            joinColumnMd.setName(cmd.getName() + ID_SUFFIX);

            ElementMetadata elementMd = fmd.newElementMetadata();
            elementMd.setColumn(holder.getFieldName() + ID_SUFFIX);
        }
    }
    
    private void addManyToManyMetadata(FieldMetadata fmd, RelationshipHolder holder, EntityDto entity, FieldDto field,
                                       Class<?> definition) {
        // If tables and column names have been specified in annotations, do not set their metadata
        // Join metadata must be present at exactly one side of the M:N relation when using Sets
        // When using Lists join metadata must be present at two sides of M:N relation
        if (!holder.isOwningSide() || holder.isListManyToMany()) {
            java.lang.reflect.Field fieldDefinition = FieldUtils.getDeclaredField(definition, field.getBasic().getName(),
                    true);

            Join join = fieldDefinition.getAnnotation(Join.class);
            Persistent persistent = fieldDefinition.getAnnotation(Persistent.class);
            Element element = fieldDefinition.getAnnotation(Element.class);

            JoinMetadata jmd = null;

            if (join == null) {
                jmd = fmd.newJoinMetadata();
            }

            setTableNameMetadata(fmd, persistent, entity, field, holder, EntityType.STANDARD);
            setElementMetadata(fmd, element, holder, entity);

            if (join == null || StringUtils.isEmpty(join.column())) {
                setJoinMetadata(jmd, fmd, ClassName.getSimpleName(entity.getClassName()).toUpperCase() + ID_SUFFIX);
                ForeignKeyMetadata fkmd = getOrCreateFkMetadata(jmd);
                fkmd.setName(KeyNames.foreignKeyName(entity.getName(), entity.getId(), field.getBasic().getName(),
                        EntityType.STANDARD));
            }
        }
    }

    private void setElementMetadata(FieldMetadata fmd, Element element, RelationshipHolder holder, EntityDto entity) {
        if (element == null || StringUtils.isEmpty(element.column())) {
            ElementMetadata emd = fmd.newElementMetadata();
            emd.setColumn((ClassName.getSimpleName(holder.getRelatedClass()) + ID_SUFFIX).toUpperCase());
            ForeignKeyMetadata fkmd = emd.newForeignKeyMetadata();
            fkmd.setName(KeyNames.foreignKeyName(ClassName.getSimpleName(holder.getRelatedClass()), entity.getId(),
                    holder.getRelatedField(), EntityType.STANDARD));
        }
    }

    private void setJoinMetadata(JoinMetadata jmd, FieldMetadata fmd, String column) {
        JoinMetadata joinMetadata;
        if (jmd == null) {
            joinMetadata = fmd.newJoinMetadata();
            joinMetadata.setOuter(false);
        } else {
            joinMetadata = jmd;
        }

        joinMetadata.newColumnMetadata().setName(column);
    }

    private void setTableNameMetadata(FieldMetadata fmd, Persistent persistent, EntityDto entity, FieldDto field,
                                      RelationshipHolder holder, EntityType entityType) {
        if (persistent != null && StringUtils.isNotEmpty(persistent.table()) && entityType != EntityType.STANDARD) {
            fmd.setTable(entityType.getTableName(persistent.table()));
        } else if (persistent == null || StringUtils.isEmpty(persistent.table())) {
            fmd.setTable(getJoinTableName(entity.getModule(), entity.getNamespace(), field.getBasic().getName(),
                    holder.getRelatedField()));
        }
    }

    private void setUpCollectionMetadata(FieldMetadata fmd, String relatedClass, RelationshipHolder holder, EntityType entityType) {
        CollectionMetadata colMd = getOrCreateCollectionMetadata(fmd);
        colMd.setElementType(relatedClass);
        colMd.setEmbeddedElement(false);
        colMd.setSerializedElement(false);
       
        if (holder.isManyToMany()) {
            colMd.setDependentElement(holder.isCascadeDelete() || entityType.equals(EntityType.TRASH));
        }

        if (holder.isSetManyToMany() && !holder.isOwningSide() && entityType.equals(EntityType.STANDARD)) {
            fmd.setMappedBy(holder.getRelatedField());
        }
    }

    private FieldMetadata setComboboxMetadata(ClassMetadata cmd, EntityDto entity, FieldDto field,
                                              Class<?> definition, EntityType entityType) {
        ComboboxHolder holder = new ComboboxHolder(entity, field);
        String fieldName = getNameForMetadata(field);
        FieldMetadata fmd = cmd.newFieldMetadata(fieldName);

        if (holder.isCollection()) {
            addDefaultFetchGroupMetadata(fmd, definition);

            fmd.setTable(ClassTableName.getTableName(cmd.getTable(), fieldName));

            JoinMetadata jm = fmd.newJoinMetadata();

            ForeignKeyMetadata fkmd = getOrCreateFkMetadata(jm);
            fkmd.setName(KeyNames.cbForeignKeyName(entity.getName(), entity.getId(), fieldName, entityType));

            jm.setDeleteAction(ForeignKeyAction.CASCADE);
            jm.newColumnMetadata().setName(fieldName + "_OID");
        }
        return fmd;
    }

    private FieldMetadata setAutoGenerationMetadata(ClassMetadata cmd, String name) {
        FieldMetadata fmd = cmd.newFieldMetadata(name);
        fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
        fmd.setDefaultFetchGroup(true);
        fmd.newExtensionMetadata(DATANUCLEUS, VALUE_GENERATOR, "ovg." + name);
        return fmd;
    }

    private static ClassMetadata getClassMetadata(PackageMetadata pmd, String className) {
        ClassMetadata[] classes = pmd.getClasses();
        if (ArrayUtils.isNotEmpty(classes)) {
            for (ClassMetadata cmd : classes) {
                if (StringUtils.equals(className, cmd.getName())) {
                    return cmd;
                }
            }
        }
        return pmd.newClassMetadata(className);
    }

    private static PackageMetadata getPackageMetadata(JDOMetadata jdoMetadata, String packageName) {
        // first look for existing metadata
        PackageMetadata[] packages = jdoMetadata.getPackages();
        if (ArrayUtils.isNotEmpty(packages)) {
            for (PackageMetadata pkgMetadata : packages) {
                if (StringUtils.equals(pkgMetadata.getName(), packageName)) {
                    return pkgMetadata;
                }
            }
        }
        // if not found, create new
        return jdoMetadata.newPackageMetadata(packageName);
    }

    private void addIdField(ClassMetadata cmd, EntityDto entity, SchemaHolder schemaHolder, Class<?> definition) {
        boolean containsID = null != schemaHolder.getFieldByName(entity, ID_FIELD_NAME);
        boolean isBaseClass = entity.isBaseEntity();

        if (containsID && isBaseClass) {
            FieldMetadata metadata = cmd.newFieldMetadata(ID_FIELD_NAME);
            metadata.setValueStrategy(getIdGeneratorStrategy(metadata, definition));
            metadata.setPrimaryKey(true);
        }
    }

    private void addIdField(ClassMetadata cmd, String className, Class<?> definition) {
        boolean containsID;
        boolean isBaseClass;

        try {
            CtClass ctClass = MotechClassPool.getDefault().getOrNull(className);
            containsID = null != ctClass && null != ctClass.getField(ID_FIELD_NAME);
            isBaseClass = null != ctClass && (null == ctClass.getSuperclass() || Object.class.getName().equalsIgnoreCase(ctClass.getSuperclass().getName()));
        } catch (NotFoundException e) {
            containsID = false;
            isBaseClass = false;
        }

        if (containsID && isBaseClass) {
            FieldMetadata metadata = cmd.newFieldMetadata(ID_FIELD_NAME);
            metadata.setValueStrategy(getIdGeneratorStrategy(metadata, definition));
            metadata.setPrimaryKey(true);
        }
    }

    private IdGeneratorStrategy getIdGeneratorStrategy(FieldMetadata fmd, Class<?> definition) {
        java.lang.reflect.Field field = FieldUtils.getField(definition, fmd.getName(), true);
        if (field != null) {
            Persistent persistentAnnotation = ReflectionsUtil.getAnnotationSelfOrAccessor(field, Persistent.class);
            if (persistentAnnotation != null && persistentAnnotation.valueStrategy() != null
                    && !persistentAnnotation.valueStrategy().equals(IdGeneratorStrategy.UNSPECIFIED)) {
                return persistentAnnotation.valueStrategy();
            }
        }

        return IdGeneratorStrategy.NATIVE;
    }
    private CollectionMetadata getOrCreateCollectionMetadata(FieldMetadata fmd) {
        CollectionMetadata collMd = fmd.getCollectionMetadata();
        if (collMd == null) {
            collMd = fmd.newCollectionMetadata();
        }
        return collMd;
    }

    private String getJoinTableName(String module, String namespace, String owningSideName, String inversedSideNameWithSuffix) {
        String mod = defaultIfBlank(module, "MDS");

        StringBuilder builder = new StringBuilder();
        builder.append(mod).append("_");

        if (isNotBlank(namespace)) {
            builder.append(namespace).append("_");
        }

        builder.append("Join_").
                append(inversedSideNameWithSuffix).append("_").
                append(owningSideName).
                append(ClassName.getEntityTypeSuffix(inversedSideNameWithSuffix));

        return builder.toString().replace('-', '_').replace(' ', '_').toUpperCase();
    }

    private boolean shouldSetCascadeDelete(RelationshipHolder holder, EntityType entityType) {

        if (holder.isCascadeDelete() || entityType == EntityType.TRASH) {
            return (holder.isOneToOne() || holder.isOneToMany()) && holder.getRelatedField() != null;
        }

        return false;
    }

    private boolean shouldSetNullDelete(RelationshipHolder holder, FieldDto field) {

        if ((holder.isOneToOne() || holder.isManyToOne()) && !field.getBasic().isRequired()) {
            return true;
        }

        return false;
    }

    private IndexMetadata getOrCreateIndexMetadata(FieldMetadata fmd) {
        return fmd.getIndexMetadata() == null ? fmd.newIndexMetadata() : fmd.getIndexMetadata();
    }

    private ForeignKeyMetadata getOrCreateFkMetadata(MemberMetadata mmd) {
        return mmd.getForeignKeyMetadata() == null ? mmd.newForeignKeyMetadata() : mmd.getForeignKeyMetadata();
    }

    private ForeignKeyMetadata getOrCreateRelFkMetadata(MemberMetadata mmd, EntityDto entity, FieldDto field) {
        ForeignKeyMetadata fkmd = getOrCreateFkMetadata(mmd);
        fkmd.setName(KeyNames.foreignKeyName(entity.getName(), entity.getId(), field.getBasic().getName(),
                EntityType.STANDARD));
        return fkmd;
    }

    private ForeignKeyMetadata getOrCreateFkMetadata(JoinMetadata jmd) {
        return jmd.getForeignKeyMetadata() == null ? jmd.newForeignKeyMetadata() : jmd.getForeignKeyMetadata();
    }

    private String getNameForMetadata(FieldDto field) {
        return StringUtils.uncapitalize(field.getBasic().getName());
    }

    private boolean isBlobOrClob(FieldDto field) {
        return field.getType().isBlob() || isClob(field);
    }
    
    private boolean isClob(FieldDto field) {
        return Constants.Util.TRUE.equalsIgnoreCase(
                field.getSettingsValueAsString(Constants.Settings.STRING_TEXT_AREA));
    }
}
