package org.motechproject.mds.domain;

import org.motechproject.mds.util.Constants;

/**
 * A class representing a relationship type. This class is inherited by different types of relationships.
 * This class only represents the field type and provides some utility methods. It is not used in entities themselves.
 */
public class Relationship {

    public String getFieldType(Field field, EntityType type) {
        return getRelatedClassName(field, type);
    }

    public String getGenericSignature(Field field, EntityType type) {
        return null;
    }

    protected String getRelatedClassName(Field field, EntityType type) {
        FieldMetadata fmd = field.getMetadata(Constants.MetadataKeys.RELATED_CLASS);

        if (fmd == null) {
            throw new IllegalStateException(String.format("Unknown type for relationship %s in entity %s",
                    field.getName(), field.getEntity().getName()));
        }

        return type.getName(fmd.getValue());
    }
}
