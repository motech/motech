package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldDto;

/**
 * A specialization of the {@link Relationship} class.
 * Represents a one-to-one relationship.
 */
public class OneToOneRelationship extends Relationship {

    @Override
    public String getFieldType(FieldDto field, EntityType type) {
        if (type == EntityType.STANDARD) {
            return getRelatedClassName(field, type);
        } else {
            return Long.class.getName();
        }
    }

    @Override
    public String getGenericSignature(FieldDto field, EntityType type) {
        return null;
    }
}
