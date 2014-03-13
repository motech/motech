package org.motechproject.mds.builder;

import org.motechproject.mds.domain.Entity;
import org.osgi.framework.Bundle;

/**
 * An entity builder is responsible for building the entity class from an Entity schema.
 */
public interface EntityBuilder {

    /**
     * Builds a class definition for a given entity. The class is not registered with any classloader.
     * @param entity the entity schema
     * @return bytes of the newly constructed class
     */
    ClassData build(Entity entity);

    ClassData buildDDE(Entity entity, Bundle bundle);

    ClassData buildHistory(Entity entity);
}
