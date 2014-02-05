package org.motechproject.mds.builder;


import org.motechproject.mds.domain.EntityMapping;

/**
 * An entity builder is responsible for building the entity class from an Entity schema.
 */
public interface EntityBuilder {

    /**
     * Builds a class definition for a given entity. The class is not registered with any classloader.
     * @param entityMapping the entity schema
     * @return bytes of the newly constructured class
     */
    ClassData build(EntityMapping entityMapping);
}
