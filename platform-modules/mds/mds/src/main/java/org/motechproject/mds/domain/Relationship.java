package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.util.Constants;

/**
 * A class representing a relationship type. This class is inherited by different types of relationships.
 * This class only represents the field type and provides some utility methods. It is not used in entities themselves.
 */
public class Relationship {

    public String getFieldType(FieldDto field, EntityType type) {
        return getRelatedClassName(field, type);
    }

    public String getGenericSignature(FieldDto field, EntityType type) {
        return null;
    }

    protected String getRelatedClassName(FieldDto field, EntityType type) {
        MetadataDto fmd = field.getMetadata(Constants.MetadataKeys.RELATED_CLASS);

        if (fmd == null) {
            throw new IllegalStateException(String.format("Unknown type for relationship %s",
                    field.getBasic().getName()));
        }

        return type.getClassName(fmd.getValue());
    }
}
