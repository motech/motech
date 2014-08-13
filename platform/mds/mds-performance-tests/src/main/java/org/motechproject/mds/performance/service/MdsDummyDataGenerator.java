package org.motechproject.mds.performance.service;

import org.motechproject.mds.service.MotechDataService;
import org.osgi.framework.BundleContext;

import java.io.IOException;

public interface MdsDummyDataGenerator {

    /**
     * Generate a given amount of empty entities
     *
     * @param numberOfEntities   How many entities should be generated
     * @param regenerateBundle   Should the data bundle be regenerated after entities are created
     * @throws IOException
     */
    void generateDummyEntities(int numberOfEntities, boolean regenerateBundle) throws IOException;

    /**
     * Generate a given amount of entities
     *
     * @param numberOfEntities   How many entities should be generated
     * @param fieldsPerEntity    How many fields should each entity have - type of field is assigned randomly
     * @param lookupsPerEntity   How many lookups should each entity have
     * @param regenerateBundle   Should the data bundle be regenerated after entities are created
     * @throws IOException
     */
    void generateDummyEntities(int numberOfEntities, int fieldsPerEntity, int lookupsPerEntity, boolean regenerateBundle) throws IOException;

    /**
     * Generates a given amount of instances. For most common field types, values will be automatically assigned.
     *
     * @param entityId             Id of an entity, for which we want to generate instances
     * @param numberOfInstances    How many instances should be generated
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    void generateDummyInstances(Long entityId, int numberOfInstances)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException;

    /**
     * Generates a given amount of instances, historical revisions and deleted instances.
     * For most common field types, values will be automatically assigned.
     *
     * @param entityId                      Id of an entity, for which we want to generate instances
     * @param numberOfInstances             How many instances should be generated
     * @param numberOfHistoricalRevisions   How many historical revisions should be generated
     * @param numberOfTrashInstances        How many instances in trash should be generated
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    void generateDummyInstances(Long entityId, int numberOfInstances,
                                    int numberOfHistoricalRevisions, int numberOfTrashInstances)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException;


    /**
     * Makes a dummy instance for a given entity. It won't insert the created instance into the
     * database, so it can be used to prepare a large dataset of random instances (for example,
     * to insert them manually and measure time)
     *
     * @param entityId    Id of an entity, for which we want to generate instance
     * @return            Generated instance, with randomly assigned fields
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    Object makeDummyInstance(Long entityId) throws IllegalAccessException, InstantiationException, ClassNotFoundException;

    /**
     * Prepares a dummy security context.
     */
    void setUpSecurityContext();

    void clearEntities();

    MotechDataService getService(BundleContext bundleContext, String className);

    String getEntityPrefix();

    /**
     * Sets a prefix for the generated entities. Defaults to "Entity"
     *
     * @param entityPrefix  The prefix for generated entities
     */
    void setEntityPrefix(String entityPrefix);

    String getLookupPrefix();

    /**
     * Sets a prefix for the generated lookups. Defaults to "Lookup"
     *
     * @param lookupPrefix  The prefix for generated lookups
     */
    void setLookupPrefix(String lookupPrefix);

    String getFieldPrefix();

    /**
     * Sets the prefix for the generated fields. Defaults to "field"
     *
     * @param fieldPrefix   The prefix for generated fields
     */
    void setFieldPrefix(String fieldPrefix);
}
