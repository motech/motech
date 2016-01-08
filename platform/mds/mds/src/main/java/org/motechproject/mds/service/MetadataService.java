package org.motechproject.mds.service;

/**
 * A service that allows access to JDO metadata for MDS. Allows lookups for
 * actual table names. Exposed by the generated MDS entities bundle, since it needs
 * it {@link javax.jdo.PersistenceManagerFactory} for retrieving accurate metadata.
 */
public interface MetadataService {

    /**
     * Returns the datastore table for the given combobox field in an entity.
     * @param entityClassName the class name of the entity
     * @param cbFieldName the name of the combobox field
     * @return the table name for the combobox
     * @throws IllegalArgumentException if there is no table name available in the metadta for this field
     */
    String getComboboxTableName(String entityClassName, String cbFieldName);
}
