package org.motechproject.mds.service;

import org.motechproject.mds.domain.EntityMapping;

/**
 * This interface provide method to build a class for a given entity. The implementation of this
 * interface should also construct other classes like repository, service interface and
 * implementation for this service interface.
 */
public interface MDSConstructor {

    void constructEntity(EntityMapping mapping);

    void generateAllEntities();
}
