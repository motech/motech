package org.motechproject.mds.domain;

import javassist.bytecode.Descriptor;
import org.motechproject.mds.util.Constants;

import java.util.List;

/**
 * A specialization of the {@link Relationship} class.
 * Represents a many-to-many relationship.
 */
public class ManyToManyRelationship extends Relationship {

    @Override
    public String getFieldType(Field field, EntityType type) {
        FieldMetadata fieldMetadata = field.getMetadata(Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE);
        return fieldMetadata != null ? fieldMetadata.getValue() : List.class.getName();
    }

    @Override
    public String getGenericSignature(Field field, EntityType type) {
        String elementClass = type == EntityType.STANDARD ?
                getRelatedClassName(field, type) :
                Long.class.getName();

        String generic = Descriptor.of(elementClass);
        String collectionSignatureName = Descriptor.toJvmName(getFieldType(field, type));

        return String.format("L%s<%s>;", collectionSignatureName, generic);
    }

}
