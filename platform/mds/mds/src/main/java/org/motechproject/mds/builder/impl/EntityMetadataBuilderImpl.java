package org.motechproject.mds.builder.impl;

import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.RecordRelation;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.CollectionMetadata;
import javax.jdo.metadata.ColumnMetadata;
import javax.jdo.metadata.ElementMetadata;
import javax.jdo.metadata.FieldMetadata;
import javax.jdo.metadata.InheritanceMetadata;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.metadata.JoinMetadata;
import javax.jdo.metadata.MapMetadata;
import javax.jdo.metadata.MemberMetadata;
import javax.jdo.metadata.PackageMetadata;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.mds.util.Constants.MetadataKeys.DATABASE_COLUMN_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;
import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;
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
    private static final String[] FIELD_VALUE_GENERATOR = new String[]{
            CREATOR_FIELD_NAME, OWNER_FIELD_NAME, CREATION_DATE_FIELD_NAME,
            MODIFIED_BY_FIELD_NAME, MODIFICATION_DATE_FIELD_NAME
    };

    private AllEntities allEntities;

    @Override
    public void addEntityMetadata(JDOMetadata jdoMetadata, Entity entity) {
        String className = (entity.isDDE()) ? entity.getClassName() : ClassName.getEntityName(entity.getClassName());
        String packageName = ClassName.getPackage(className);
        String tableName = getTableName(
                entity.getClassName(), entity.getModule(), entity.getNamespace()
        );

        PackageMetadata pmd = getPackageMetadata(jdoMetadata, packageName);
        ClassMetadata cmd = getClassMetadata(pmd, ClassName.getSimpleName(ClassName.getEntityName(entity.getClassName())));

        cmd.setTable(tableName);
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.APPLICATION);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        InheritanceMetadata imd = cmd.newInheritanceMetadata();
        imd.setCustomStrategy("complete-table");

        if (!entity.isSubClassOfMdsEntity()) {
            addIdField(cmd, entity);
        }

        addMetadataForFields(cmd, null, entity, EntityType.STANDARD);
    }

    @Override
    public void addHelperClassMetadata(JDOMetadata jdoMetadata, ClassData classData, Entity entity,
                                       EntityType entityType) {
        String packageName = ClassName.getPackage(classData.getClassName());
        String simpleName = ClassName.getSimpleName(classData.getClassName());
        String tableName = getTableName(
                classData.getClassName(), classData.getModule(), classData.getNamespace()
        );

        PackageMetadata pmd = getPackageMetadata(jdoMetadata, packageName);
        ClassMetadata cmd = getClassMetadata(pmd, simpleName);

        cmd.setTable(tableName);
        cmd.setDetachable(true);
        cmd.setIdentityType(IdentityType.APPLICATION);
        cmd.setPersistenceModifier(ClassPersistenceModifier.PERSISTENCE_CAPABLE);

        InheritanceMetadata imd = cmd.newInheritanceMetadata();
        imd.setCustomStrategy("complete-table");

        addIdField(cmd, classData.getClassName());

        if (entity != null) {
            addMetadataForFields(cmd, classData, entity, entityType);
        }
    }

    @Override
    @Transactional
    public void fixEnhancerIssuesInMetadata(JDOMetadata jdoMetadata) {
        for (PackageMetadata pmd : jdoMetadata.getPackages()) {
            for (ClassMetadata cmd : pmd.getClasses()) {
                String className = String.format("%s.%s", pmd.getName(), cmd.getName());
                String trimmedClassName = ClassName.trimTrashHistorySuffix(className);
                Entity entity = allEntities.retrieveByClassName(trimmedClassName);
                EntityType entityType = EntityType.forClassName(className);

                if (null != entity) {
                    for (MemberMetadata mmd : cmd.getMembers()) {
                        CollectionMetadata collMd = mmd.getCollectionMetadata();
                        Field field = entity.getField(mmd.getName());

                        if (null != field && field.getType().isRelationship()) {
                            fixRelationMetadata(mmd, collMd, field, entityType);
                        }

                        if (null != collMd) {
                            fixCollectionMetadata(collMd);
                        }
                    }
                }
            }
        }
    }

    private void fixCollectionMetadata(CollectionMetadata collMd) {
        String elementType = collMd.getElementType();
        String trimmedElementType = ClassName.trimTrashHistorySuffix(elementType);

        if (null != MotechClassPool.getEnhancedClassData(trimmedElementType)) {
            collMd.setEmbeddedElement(false);
        }
    }

    private void fixRelationMetadata(MemberMetadata mmd, CollectionMetadata collMd, Field field, EntityType entityType) {
        RelationshipHolder holder = new RelationshipHolder(field);

        if ((holder.isOneToMany() || holder.isManyToMany()) && null != collMd) {
            collMd.setDependentElement(holder.isCascadeDelete() || entityType == EntityType.TRASH);
        } else if (holder.isOneToOne()) {
            mmd.setDependent(holder.isCascadeDelete());
        }
    }

    @Override
    public void addBaseMetadata(JDOMetadata jdoMetadata, ClassData classData, EntityType entityType) {
        addHelperClassMetadata(jdoMetadata, classData, null, entityType);
    }

    private void addMetadataForFields(ClassMetadata cmd, ClassData classData, Entity entity, EntityType entityType) {
        for (Field field : entity.getFields()) {
            // Metadata for ID field has been added earlier in addIdField() method
            if (!field.getName().equals(ID_FIELD_NAME)) {
                FieldMetadata fmd = null;

                if (checkIfFieldIsNotInherited(field.getName(), entity)) {
                    fmd = setFieldMetadata(cmd, classData, entity, entityType, field);
                }
                // when field is in Lookup, we set field metadata indexed to retrieve instance faster
                if (!field.getLookups().isEmpty() && entityType.equals(EntityType.STANDARD)) {
                    if (fmd == null) {
                        String inheritedFieldName = ClassName.getSimpleName(entity.getSuperClass()) + "." + field.getName();
                        fmd = cmd.newFieldMetadata(inheritedFieldName);
                    }
                    fmd.setIndexed(true);
                }
                if (fmd != null) {
                    setColumnParameters(fmd, field);
                    // Check whether the field is required and set appropriate metadata
                    fmd.setNullValue(field.isRequired() ? NullValue.EXCEPTION : NullValue.NONE);
                }
            }
        }
    }

    private boolean checkIfFieldIsNotInherited(String fieldName, Entity entity) {
        if (entity.isSubClassOfMdsEntity() && (ArrayUtils.contains(FIELD_VALUE_GENERATOR, fieldName))) {
            return false;
        } else {
            // return false if it is inherited field from superclass
            return entity.isBaseEntity() || !isFieldFromSuperClass(entity.getSuperClass(), fieldName);
        }
    }

    private boolean isFieldFromSuperClass(String className, String fieldName) {
        Entity entity = allEntities.retrieveByClassName(className);
        return entity.getField(fieldName) != null;
    }

    private FieldMetadata setFieldMetadata(ClassMetadata cmd, ClassData classData, Entity entity,
                                           EntityType entityType, Field field) {
        String name = field.getName();

        Type type = field.getType();
        Class<?> typeClass = type.getTypeClass();

        if (ArrayUtils.contains(FIELD_VALUE_GENERATOR, name)) {
            return setAutoGenerationMetadata(cmd, name);
        } else if (type.isCombobox()) {
            return setComboboxMetadata(cmd, entity, field);
        } else if (type.isRelationship()) {
            return setRelationshipMetadata(cmd, classData, field, entityType);
        } else if (Map.class.isAssignableFrom(typeClass)) {
            return setMapMetadata(cmd, field);
        } else if (Time.class.isAssignableFrom(typeClass)) {
            return setTimeMetadata(cmd, name);
        }
        return cmd.newFieldMetadata(name);
    }

    private void setColumnParameters(FieldMetadata fmd, Field field) {
        if ((field.getMetadata(DATABASE_COLUMN_NAME) != null || field.getSettingByName(Constants.Settings.STRING_MAX_LENGTH) != null
                || field.getSettingByName(Constants.Settings.STRING_TEXT_AREA) != null)) {
            FieldSetting maxLengthSetting = field.getSettingByName(Constants.Settings.STRING_MAX_LENGTH);

            ColumnMetadata colMd = fmd.newColumnMetadata();
            // only set the metadata if the setting is different from default
            if (maxLengthSetting != null && !StringUtils.equals(maxLengthSetting.getValue(),
                    maxLengthSetting.getDetails().getDefaultValue())) {
                colMd.setLength(Integer.parseInt(maxLengthSetting.getValue()));
            }

            // if TextArea then change length
            if (field.getSettingByName(Constants.Settings.STRING_TEXT_AREA) != null &&
                    "true".equalsIgnoreCase(field.getSettingByName(Constants.Settings.STRING_TEXT_AREA).getValue())) {
                fmd.setIndexed(false);
                colMd.setSQLType("CLOB");
            }
            if (field.getMetadata(DATABASE_COLUMN_NAME) != null) {
                colMd.setName(field.getMetadata(DATABASE_COLUMN_NAME).getValue());
            }
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

    private FieldMetadata setMapMetadata(ClassMetadata cmd, Field field) {
        FieldMetadata fmd = cmd.newFieldMetadata(field.getName());

        org.motechproject.mds.domain.FieldMetadata keyMetadata = field.getMetadata(MAP_KEY_TYPE);
        org.motechproject.mds.domain.FieldMetadata valueMetadata = field.getMetadata(MAP_VALUE_TYPE);
        boolean serialized = keyMetadata != null && valueMetadata != null &&
                (!keyMetadata.getValue().equals(String.class.getName()) || !valueMetadata.getValue().equals(String.class.getName()));

        // Depending on the types of key and value of the map we either serialize the map or create a separate table for it
        fmd.setSerialized(serialized);
        fmd.setDefaultFetchGroup(true);

        MapMetadata mmd = fmd.newMapMetadata();

        if (serialized) {
            mmd.setSerializedKey(true);
            mmd.setSerializedValue(true);
        } else {
            mmd.setKeyType(String.class.getName());
            mmd.setValueType(String.class.getName());

            fmd.setTable(getTableName(cmd.getTable(), field.getName()));
            fmd.newJoinMetadata();
        }
        return fmd;
    }

    private FieldMetadata setRelationshipMetadata(ClassMetadata cmd, ClassData classData, Field field,
                                         EntityType entityType) {
        if (entityType == EntityType.STANDARD) {
            return regularRelationshipMetadata(cmd, classData, field);
        } else {
            return recordRelationshipMetadata(cmd, classData, field);
        }
    }

    private FieldMetadata regularRelationshipMetadata(ClassMetadata cmd, ClassData classData, Field field) {
        RelationshipHolder holder = new RelationshipHolder(classData, field);
        String relatedClass = holder.getRelatedClass();

        FieldMetadata fmd = cmd.newFieldMetadata(field.getName());
        fmd.setDefaultFetchGroup(true);

        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-persist", Boolean.toString(holder.isCascadePersist()));
        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-update", Boolean.toString(holder.isCascadeUpdate()));

        if (holder.isOneToMany() || holder.isManyToMany()) {
            CollectionMetadata colMd = getOrCreateCollectionMetadata(fmd);
            colMd.setElementType(relatedClass);
            colMd.setEmbeddedElement(false);
            colMd.setSerializedElement(false);
            colMd.setDependentElement(holder.isCascadeDelete());
        } else if (holder.isOneToOne()) {
            fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
            fmd.setDependent(holder.isCascadeDelete());
        }

        if (holder.isManyToMany()) {
            if (holder.isOwningSide()) {
                fmd.setTable(getJoinTableName(field.getEntity().getModule(), field.getEntity().getNamespace(), field.getName(), holder.getRelatedField()));

                JoinMetadata jmd = fmd.newJoinMetadata();
                jmd.setOuter(false);
                String oID = (ClassName.getSimpleName(field.getEntity().getClassName()) + "_ID").toUpperCase();
                jmd.setColumn(oID);

                ElementMetadata emd = fmd.newElementMetadata();
                String eID = (ClassName.getSimpleName(ClassName.trimTrashHistorySuffix(holder.getRelatedClass()) + "_ID")).toUpperCase();
                emd.setColumn(eID);
            }
        }
        return fmd;
    }

    private FieldMetadata recordRelationshipMetadata(ClassMetadata cmd, ClassData classData, Field field) {
        RelationshipHolder holder = new RelationshipHolder(classData, field);

        FieldMetadata fmd = cmd.newFieldMetadata(field.getName());
        fmd.setDefaultFetchGroup(true);

        //For history and trash classes, we set cascades to proxies to true
        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-persist", TRUE);
        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-update", TRUE);

        // 1:N to proxy using a join table
        if (holder.isOneToMany() || holder.isManyToMany()) {
            CollectionMetadata colMd = getOrCreateCollectionMetadata(fmd);
            colMd.setElementType(RecordRelation.class.getName());
            colMd.setDependentElement(true);

            JoinMetadata joinMd = fmd.newJoinMetadata();
            joinMd.setColumn(ID_FIELD_NAME);

            ElementMetadata elementMd = fmd.newElementMetadata();
            elementMd.setColumn(Constants.Util.OBJECT_ID_COLUMN);
        }

        return fmd;
    }

    private FieldMetadata setComboboxMetadata(ClassMetadata cmd, Entity entity, Field field) {
        ComboboxHolder holder = new ComboboxHolder(entity, field);
        FieldMetadata fmd = cmd.newFieldMetadata(field.getName());

        if (holder.isStringList() || holder.isEnumList()) {
            fmd.setDefaultFetchGroup(true);
            fmd.setTable(getTableName(cmd.getTable(), field.getName()));

            JoinMetadata jm = fmd.newJoinMetadata();
            jm.setColumn(field.getName() + "_OID");
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

    private static String getTableName(String table, String suffix) {
        String tableName = table;

        if (isNotBlank(suffix)) {
            tableName += "_" + suffix;
        }

        return tableName.replace('-', '_').replace(' ', '_').toUpperCase();
    }

    public static String getTableName(Entity entity, EntityType type) {
        String tableName = getTableName(entity.getClassName(), entity.getModule(), entity.getNamespace());
        if (type == EntityType.STANDARD) {
            return tableName;
        }
        return getTableName(tableName, "_" + type.toString());
    }

    public static String getTableName(String className, String module, String namespace) {
        String simpleName = ClassName.getSimpleName(className);
        String mod = defaultIfBlank(module, "MDS");

        StringBuilder builder = new StringBuilder();
        builder.append(mod).append("_");

        if (isNotBlank(namespace)) {
            builder.append(namespace).append("_");
        }

        builder.append(simpleName);

        return builder.toString().replace('-', '_').replace(' ', '_').toUpperCase();
    }

    private void addIdField(ClassMetadata cmd, Entity entity) {
        boolean containsID = null != entity.getField(ID_FIELD_NAME);
        boolean isBaseClass = entity.isBaseEntity();

        if (containsID && isBaseClass) {
            FieldMetadata metadata = cmd.newFieldMetadata(ID_FIELD_NAME);
            metadata.setValueStrategy(IdGeneratorStrategy.INCREMENT);
            metadata.setPrimaryKey(true);
            metadata.setIndexed(true);
        }
    }

    private void addIdField(ClassMetadata cmd, String className) {
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
            metadata.setValueStrategy(IdGeneratorStrategy.INCREMENT);
            metadata.setPrimaryKey(true);
            metadata.setIndexed(true);
        }
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
                append(ClassName.trimTrashHistorySuffix(inversedSideNameWithSuffix)).append("_").
                append(owningSideName).
                append(ClassName.getEntityTypeSuffix(inversedSideNameWithSuffix));

        return builder.toString().replace('-', '_').replace(' ', '_').toUpperCase();
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
