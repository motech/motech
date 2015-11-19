package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldDto;

/**
 * A specialization of the {@link org.motechproject.mds.domain.Relationship} class.
 * Represents a many-to-one relationship.
 */
public class ManyToOneRelationship extends Relationship {

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
