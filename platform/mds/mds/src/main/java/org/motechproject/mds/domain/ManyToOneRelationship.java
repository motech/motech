package org.motechproject.mds.domain;

/**
 * A specialization of the {@link org.motechproject.mds.domain.Relationship} class.
 * Represents a many-to-one relationship.
 */
public class ManyToOneRelationship extends Relationship {

    @Override
    public String getFieldType(Field field, EntityType type) {
        if (type == EntityType.STANDARD) {
            return getRelatedClassName(field, type);
        } else {
            return Long.class.getName();
        }
    }

    @Override
    public String getGenericSignature(Field field, EntityType type) {
        return null;
    }
}
