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
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.metadata.ClassMetadata;
import javax.jdo.metadata.ClassPersistenceModifier;
import javax.jdo.metadata.CollectionMetadata;
import javax.jdo.metadata.ColumnMetadata;
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
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.DATANUCLEUS;
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
    private MetadataHolder metadataHolder;

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

        addMetadataForFields(cmd, null, entity);
    }

    @Override
    public void addHelperClassMetadata(JDOMetadata jdoMetadata, ClassData classData, Entity entity) {
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
            addMetadataForFields(cmd, classData, entity);
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

                if (null != entity) {
                    for (MemberMetadata mmd : cmd.getMembers()) {
                        CollectionMetadata collMd = mmd.getCollectionMetadata();
                        Field field = entity.getField(mmd.getName());

                        if (null != field && field.getType().isRelationship()) {
                            fixRelationMetadata(mmd, collMd, field);
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

    private void fixRelationMetadata(MemberMetadata mmd, CollectionMetadata collMd, Field field) {
        RelationshipHolder holder = new RelationshipHolder(field);

        if (holder.isOneToMany() && null != collMd) {
            collMd.setDependentElement(holder.isCascadeDelete());
        } else if (holder.isOneToOne()) {
            mmd.setDependent(holder.isCascadeDelete());
        }
    }

    @Override
    public void addBaseMetadata(JDOMetadata jdoMetadata, ClassData classData) {
        addHelperClassMetadata(jdoMetadata, classData, null);
    }

    private void addMetadataForFields(ClassMetadata cmd, ClassData classData, Entity entity) {
        for (Field field : entity.getFields()) {
            String name = field.getName();

            Type type = field.getType();
            Class<?> typeClass = type.getTypeClass();

            if (ArrayUtils.contains(FIELD_VALUE_GENERATOR, name)) {
                if (entity.isBaseEntity() && !entity.isSubClassOfMdsEntity()) {
                    setAutoGenerationMetadata(cmd, name);
                }
            } else if (type.isCombobox()) {
                setComboboxMetadata(cmd, entity, field);
            } else if (type.isRelationship()) {
                setRelationshipMetadata(cmd, classData, entity, field);
            } else if (Map.class.isAssignableFrom(typeClass)) {
                setMapMetadata(cmd, field);
            } else if (Time.class.isAssignableFrom(typeClass)) {
                setTimeMetadata(cmd, field.getName());
            } else if (String.class.isAssignableFrom(typeClass)) {
                setStringMetadata(cmd, field);
            }
        }
    }

    private void setStringMetadata(ClassMetadata cmd, Field field) {
        FieldSetting maxLengthSetting = field.getSettingByName(Constants.Settings.STRING_MAX_LENGTH);

        // only set the metadata if the setting is different from default
        if (maxLengthSetting != null && !StringUtils.equals(maxLengthSetting.getValue(),
                maxLengthSetting.getDetails().getDefaultValue())) {

            FieldMetadata fmd = cmd.newFieldMetadata(field.getName());
            ColumnMetadata colMd = fmd.newColumnMetadata();
            colMd.setLength(Integer.parseInt(maxLengthSetting.getValue()));
        }
    }

    private void setTimeMetadata(ClassMetadata cmd, String name) {
        // for time we register our converter which persists as string
        FieldMetadata fmd = cmd.newFieldMetadata(name);

        fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
        fmd.setDefaultFetchGroup(true);
        fmd.newExtensionMetadata(DATANUCLEUS, "type-converter-name", "dn.time-string");
    }

    private void setMapMetadata(ClassMetadata cmd, Field field) {
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
    }

    private void setRelationshipMetadata(ClassMetadata cmd, ClassData classData, Entity entity, Field field) {
        RelationshipHolder holder = new RelationshipHolder(classData, field);
        String relatedClass = holder.getRelatedClass();

        FieldMetadata fmd = cmd.newFieldMetadata(field.getName());
        fmd.setDefaultFetchGroup(true);

        //For history and trash classes, we always set persist and update cascades to true
        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-persist",
                classData == null ? Boolean.toString(holder.isCascadePersist()) : TRUE);
        fmd.newExtensionMetadata(DATANUCLEUS, "cascade-update",
                classData == null ? Boolean.toString(holder.isCascadeUpdate()) : TRUE);

        if (holder.isOneToMany()) {
            CollectionMetadata colMd = getOrCreateCollectionMetadata(fmd);
            colMd.setElementType(relatedClass);
            colMd.setEmbeddedElement(false);
            colMd.setSerializedElement(false);
            colMd.setDependentElement(holder.isCascadeDelete());
        } else if (holder.isOneToOne()) {
            String className = classData == null ? entity.getClassName() : classData.getClassName();
            org.motechproject.mds.domain.FieldMetadata metadata = field.getMetadata(Constants.MetadataKeys.RELATED_FIELD);
            String relatedField = metadata == null ? null : metadata.getValue();

            if (relatedField != null && !metadataHolder.isRelationProcessed(relatedClass)) {
                // if related field exists in another class we create
                // bi-directional relation by adding mapped-by
                fmd.setMappedBy(relatedField);

                // We don't want to add mapped-by attribute again to the same relation
                metadataHolder.addProcessedRelation(className);
            }

            fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
            fmd.setDependent(holder.isCascadeDelete());
        }
    }

    private void setComboboxMetadata(ClassMetadata cmd, Entity entity, Field field) {
        ComboboxHolder holder = new ComboboxHolder(entity, field);

        if (holder.isStringList() || holder.isEnumList()) {
            FieldMetadata fmd = cmd.newFieldMetadata(field.getName());

            fmd.setDefaultFetchGroup(true);
            fmd.setTable(getTableName(cmd.getTable(), field.getName()));

            JoinMetadata jm = fmd.newJoinMetadata();
            jm.setColumn(field.getName() + "_OID");
        }
    }

    private void setAutoGenerationMetadata(ClassMetadata cmd, String name) {
        FieldMetadata fmd = cmd.newFieldMetadata(name);
        fmd.setPersistenceModifier(PersistenceModifier.PERSISTENT);
        fmd.setDefaultFetchGroup(true);
        fmd.newExtensionMetadata(DATANUCLEUS, VALUE_GENERATOR, "ovg." + name);
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
        boolean containsID = null != entity.getField("id");
        boolean isBaseClass = entity.isBaseEntity();

        if (containsID && isBaseClass) {
            FieldMetadata metadata = cmd.newFieldMetadata("id");
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
            containsID = null != ctClass && null != ctClass.getField("id");
            isBaseClass = null != ctClass && (null == ctClass.getSuperclass() || Object.class.getName().equalsIgnoreCase(ctClass.getSuperclass().getName()));
        } catch (NotFoundException e) {
            containsID = false;
            isBaseClass = false;
        }

        if (containsID && isBaseClass) {
            FieldMetadata metadata = cmd.newFieldMetadata("id");
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

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setMetadataHolder(MetadataHolder metadataHolder) {
        this.metadataHolder = metadataHolder;
    }
}
