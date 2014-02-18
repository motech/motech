package org.motechproject.mds.builder;

import org.motechproject.mds.domain.Entity;

/**
 * This interface provide method to create a class for the given entity. The implementation of this
 * interface should also construct other classes like repository, service interface and
 * implementation for this service interface.
 */
public interface MDSConstructor {

    void constructEntity(Entity entity);

    void constructAllEntities();
}
