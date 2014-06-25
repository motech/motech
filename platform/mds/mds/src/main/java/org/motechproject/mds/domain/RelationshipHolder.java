package org.motechproject.mds.domain;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;

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

    public boolean isOneToMany() {
        return OneToManyRelationship.class.isAssignableFrom(fieldType.getTypeClass());
    }

    public boolean isCascadePersist() {
        return getSettingAsBoolean("mds.form.label.cascadePersist");
    }

    public boolean isCascadeUpdate() {
        return getSettingAsBoolean("mds.form.label.cascadeUpdate");
    }

    public boolean isCascadeDelete() {
        return getSettingAsBoolean("mds.form.label.cascadeDelete");
    }

}
