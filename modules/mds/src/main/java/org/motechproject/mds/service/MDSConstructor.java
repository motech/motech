package org.motechproject.mds.service;

import org.motechproject.mds.domain.EntityMapping;

import java.io.IOException;

/**
 * This interface provide method to create a class for the given entity. The implementation of this
 * interface should also construct other classes like repository, service interface and
 * implementation for this service interface.
 */
public interface MDSConstructor {

    void constructEntity(EntityMapping mapping) throws IOException;

}
