package org.motechproject.mds.builder;

import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.osgi.framework.Bundle;

import java.util.List;

/**
 * An entity builder is responsible for building the entity class from an Entity schema.
 */
public interface EntityBuilder {

    /**
     * Builds a class definition for a given entity. The class is not registered with any classloader.
     * @param entity the entity schema
     * @param fields the field schema for this entity
     * @return bytes of the newly constructed class
     */
    ClassData build(EntityDto entity, List<FieldDto> fields);

    /**
     * Builds Developer Defined Entity. The main difference between regular build method and this one, is that
     * this method fetches the class definition from the given bundle, and injects members to the constructed
     * class from there, if possible, rather than building everything from scratch.
     *
     * @param entity the entity schema
     * @param fields the field schema for this entity
     * @param bundle the bundle to fetch class definition from
     * @return bytes of the constructed class
     */
    ClassData buildDDE(EntityDto entity, List<FieldDto> fields, Bundle bundle);

    /**
     * Builds History class definition for the given entity. The history class definition contains
     * the same members as the entity class, plus some fields history-exclusive, like schema version.
     *
     * @param entity the entity schema
     * @param fields the field schema for this entity
     * @return bytes of the constructed class
     */
    ClassData buildHistory(EntityDto entity, List<FieldDto> fields);

    /**
     * Builds Trash class definition for the given entity. The trash class definition contains
     * the same members as the entity class, plus some fields trash-exclusive.
     *
     * @param entity the entity schema
     * @param fields the field schema for this entity
     * @return bytes of the constructed class
     */
    ClassData buildTrash(EntityDto entity, List<FieldDto> fields);

    /**
     * Builds empty history class definition for the given entity and adds it to the class pool.
     *
     * @param entity the entity schema
     */
    void prepareHistoryClass(EntityDto entity);

    /**
     * Builds empty trash class definition for the given entity and adds it to the class pool.
     *
     * @param entity the entity schema
     */
    void prepareTrashClass(EntityDto entity);
}
