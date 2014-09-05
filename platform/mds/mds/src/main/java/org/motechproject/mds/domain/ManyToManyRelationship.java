package org.motechproject.mds.domain;

import javassist.bytecode.Descriptor;

import java.util.List;

/**
 * A specialization of the {@link Relationship} class.
 * Represents a many-to-many relationship.
 */
public class ManyToManyRelationship extends Relationship {

    @Override
    public String getFieldType(Field field, EntityType type) {
        return List.class.getName();
    }

    @Override
    public String getGenericSignature(Field field, EntityType type) {
        String elementClass = getRelatedClassName(field, type);
        String generic = Descriptor.of(elementClass);
        String listJvmName = Descriptor.toJvmName(List.class.getName());

        return String.format("L%s<%s>;", listJvmName, generic);
    }
}
