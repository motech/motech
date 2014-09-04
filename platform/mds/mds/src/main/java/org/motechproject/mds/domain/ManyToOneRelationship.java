package org.motechproject.mds.domain;

/**
 * A specialization of the {@link org.motechproject.mds.domain.Relationship} class.
 * Represents a many-to-one relationship.
 */
public class ManyToOneRelationship extends Relationship {

    @Override
    public String getFieldType(Field field, EntityType type) {
        return getRelatedClassName(field, type);
    }

    @Override
    public String getGenericSignature(Field field, EntityType type) {
        return null;
    }
}
