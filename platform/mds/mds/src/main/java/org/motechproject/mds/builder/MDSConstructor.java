package org.motechproject.mds.builder;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.SchemaHolder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This interface provides methods to create a class for the given entity. The implementation of this
 * interface should also construct other classes like repository, service interface and
 * implementation for this service interface.
 */
public interface MDSConstructor {

    /**
     * Creates a class definition and inserts it into the MDS class loader, based on data from
     * database. The implementation of this method should also create a repository, interface (when
     * it's necessary) and implementation of this interface.
     * <p/>
     * After executing this method, it should be possible to create an instance of the given
     * class definition and save it to the database by {@link javax.jdo.PersistenceManager} provided by
     * DataNucleus.
     * <p/>
     * An interface related with class definition should be created only for entities from outside
     * bundles and if the bundle does not define its own interface.
     *
     * @return {@code true} if there were entities for which class definitions should be created;
     * otherwise {@code false}.
     */
    boolean constructEntities(SchemaHolder schemaHolder);

    /**
     * Updates the field names of an entity. This method alters the database schema by changing
     * column names to the new value. This is done for the entity instances, history instances
     * and trash instances.
     *
     * @param entity the entity to update
     * @param fieldNameChanges A map, indexed by current field names and values being updated field names.
     */
    void updateFields(Entity entity, Map<String, String> fieldNameChanges);

    void updateRequired(Entity entity, Map<String, String> fieldNameRequired);

    void removeFields(Entity entity, Set<String> fieldsToRemove);

    /**
     * Removes unique indexes from an entity table.
     * @param entity the entity to process
     * @param fields the names of fields that should lose uniques
     */
    void removeUniqueIndexes(Entity entity, Collection<String> fields);
}
