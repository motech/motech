package org.motechproject.mds.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Set;

import static org.motechproject.mds.util.Constants.MetadataKeys.OWNING_SIDE;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;

/**
 * The main purpose of this class is to find out how cascade should be used for the given field with
 * relationship type.
 */
public class RelationshipHolder extends FieldHolder {
    private Type fieldType;
    private EntityType entityType;
    private String fieldName;

    public RelationshipHolder(Field field) {
        this(null, field);
    }

    public RelationshipHolder(ClassData data, Field field) {
        super(field);
        this.fieldType = field.getType();
        this.entityType = null == data ? EntityType.STANDARD : data.getType();
        this.fieldName = field.getName();
    }

    /**
     * If this returns true, it means that either: the relation is uni-directional or the relation is
     * bi-directional, and we should expect related class to define which fields are related
     *
     * @return true if relation is uni-directional or bi-directional without defined related field; false otherwise
     */
    public boolean hasUnresolvedRelation() {
        return !StringUtils.isEmpty(getRelatedClass()) && StringUtils.isEmpty(getRelatedField());
    }

    public String getRelatedClass() {
        return entityType.getName(getMetadata(RELATED_CLASS));
    }

    public String getRelatedField() {
        return entityType.getName(getMetadata(RELATED_FIELD));
    }

    public boolean isOwningSide() {
        return Constants.Util.TRUE.equals(getMetadata(OWNING_SIDE));
    }

    public boolean isOneToMany() {
        return OneToManyRelationship.class.isAssignableFrom(fieldType.getTypeClass());
    }

    public boolean isOneToOne() {
        return OneToOneRelationship.class.isAssignableFrom(fieldType.getTypeClass());
    }

    public boolean isManyToMany() {
        return ManyToManyRelationship.class.isAssignableFrom(fieldType.getTypeClass());
    }

    public boolean isManyToOne() {
        return ManyToOneRelationship.class.isAssignableFrom(fieldType.getTypeClass());
    }

    public boolean isCascadePersist() {
        return getSettingAsBoolean(Constants.Settings.CASCADE_PERSIST);
    }

    public boolean isCascadeUpdate() {
        return getSettingAsBoolean(Constants.Settings.CASCADE_UPDATE);
    }

    public boolean isCascadeDelete() {
        return getSettingAsBoolean(Constants.Settings.CASCADE_DELETE);
    }

    public boolean isBiDirectional() {
        return isManyToMany() || getRelatedField() != null;
    }

    public boolean isSetManyToMany() {
        return isManyToMany() && Set.class.getName().equals(getCollectionClassName());
    }

    public boolean isListManyToMany() {
        return isManyToMany() && List.class.getName().equals(getCollectionClassName());
    }

    public String getCollectionClassName() {
        return getMetadata(Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
