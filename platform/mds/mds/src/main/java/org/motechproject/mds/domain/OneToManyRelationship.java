package org.motechproject.mds.domain;

import javassist.bytecode.Descriptor;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE;

/**
 * A specialization of the {@link Relationship} class.
 * Represents a one-to-many relationship.
 */
public class OneToManyRelationship extends Relationship {

    @Override
    public String getFieldType(Field field, EntityType type) {
        if (isNotBlank(field.getMetadataValue(RELATIONSHIP_COLLECTION_TYPE))) {
            return field.getMetadataValue(RELATIONSHIP_COLLECTION_TYPE);
        }
        return List.class.getName();
    }

    @Override
    public String getGenericSignature(Field field, EntityType type) {
        String elementClass = getRelatedClassName(field, type);
        String generic = Descriptor.of(elementClass);
        String collectionJvmName = Descriptor.toJvmName(getFieldType(field, type));

        return String.format("L%s<%s>;", collectionJvmName, generic);
    }
}
