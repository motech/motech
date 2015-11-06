package org.motechproject.mds.domain;

/**
 * A specialization of the {@link Relationship} class.
 * Represents a one-to-one relationship.
 */
public class OneToOneRelationship extends Relationship {

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
