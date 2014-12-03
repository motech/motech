package org.motechproject.mds.domain;

import org.motechproject.mds.util.Constants;

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

    public RelationshipHolder(Field field) {
        this(null, field);
    }

    public RelationshipHolder(ClassData data, Field field) {
        super(field);
        this.fieldType = field.getType();
        this.entityType = null == data ? EntityType.STANDARD : data.getType();
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

}
