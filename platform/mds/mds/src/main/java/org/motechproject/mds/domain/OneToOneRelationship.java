package org.motechproject.mds.domain;

/**
 * A specialization of the {@link Relationship} class.
 * Represents a one-to-one relationship.
 */
public class OneToOneRelationship extends Relationship {

    @Override
    public String getFieldType(Field field, EntityType type) {
        return getRelatedClassName(field, type);
    }

    @Override
    public String getGenericSignature(Field field, EntityType type) {
        return null;
    }
}
