package org.motechproject.mds.entityinfo;

/**
 * Responsible for reading entity schema info. Should be used instead of going to the database and
 * creating needless transactions, which slow down startup.
 */
public interface EntityInfoReader {

    /**
     * Reads entity info representing the MDS schema for a given entity.
     * @param entityClassName the class name of the entity
     * @return the entity schema info
     */
    EntityInfo getEntityInfo(String entityClassName);
}
