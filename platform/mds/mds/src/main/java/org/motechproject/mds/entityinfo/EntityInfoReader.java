package org.motechproject.mds.entityinfo;

import java.util.Collection;

/**
 * Responsible for reading entity schema info. Should be used instead of going to the database and
 * creating needless transactions, which slow down startup.
 */
public interface EntityInfoReader {

    /**
     * Reads entity info representing the MDS schema for a given entity.
     *
     * @param entityClassName the class name of the entity
     * @return the entity schema info
     */
    EntityInfo getEntityInfo(String entityClassName);

    /**
     * Reads entity info representing the MDS schema for a given entity.
     *
     * @param entityId the id of the entity
     * @return the entity schema info
     */
    EntityInfo getEntityInfo(Long entityId);

    /**
     * Returns entities class names list.
     *
     * @return the list of class names
     */
    Collection<String> getEntitiesClassNames();
}
