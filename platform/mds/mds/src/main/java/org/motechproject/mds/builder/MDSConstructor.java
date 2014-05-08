package org.motechproject.mds.builder;

import java.util.Map;

/**
 * This interface provide method to create a class for the given entity. The implementation of this
 * interface should also construct other classes like repository, service interface and
 * implementation for this service interface.
 */
public interface MDSConstructor {

    /**
     * Creates a class definition and insert it into the MDS class loader based on data from
     * database. The implementation of this method should also create a repository, interface (when
     * it's necessary) and implementation of this interface.
     * <p/>
     * After executing this method, there should be possible to create an instance of the given
     * class definition and save it to database by {@link javax.jdo.PersistenceManager} provided by
     * DataNucleus.
     * <p/>
     * An interface related with class definition should be created only for entities from outside
     * bundles and if the bundle does not define its interface.
     *
     * @param buildDDE {@code true} if class definitions for entities from outside bundles should
     *                 also be created; otherwise {@code false}.
     * @return {@code true} if there were entities for which class definitions should be created;
     * otherwise {@code false}.
     */
    boolean constructEntities(boolean buildDDE);

    void updateFields(Long id, Map<String, String> fieldNameChanges);
}
