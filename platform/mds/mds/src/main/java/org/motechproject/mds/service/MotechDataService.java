package org.motechproject.mds.service;

import org.motechproject.mds.exception.audit.HistoryInstanceNotFoundException;
import org.motechproject.mds.exception.audit.TrashInstanceNotFoundException;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.SqlQueryExecution;
import org.springframework.transaction.support.TransactionCallback;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This is a basic service interface with CRUD operations. Mainly it is used as super interface to
 * create service interface related with the given entity schema in
 * {@link org.motechproject.mds.builder.EntityInfrastructureBuilder} but it can be also used by
 * other service interfaces inside this package.
 *
 * @param <T> the type of entity schema.
 */
public interface MotechDataService<T> {

    /**
     * Creates the given instance in MDS.
     *
     * @param object instance to create
     * @return created instance
     */
    T create(T object);

    /**
     * Retrieves instance from MDS based on the value of the given primary key.
     *
     * @param primaryKeyName datastore primary key name
     * @param value value of the primary key
     * @return instance with the given value for the specified primary key
     */
    T retrieve(String primaryKeyName, Object value);

    /**
     * Retrieves all instances of the {@param <T>} type.
     *
     * @return all instances
     */
    List<T> retrieveAll();

    /**
     * Retrieves all instances of the {@param <T>} type, that match the provided
     * parameters.
     *
     * @param queryParams query parameters to be used retrieving instances
     * @return all isntances matching query parameters
     */
    List<T> retrieveAll(QueryParams queryParams);

    /**
     * Updates the given instance in MDS.
     *
     * @param object instance to update
     * @return updated instance
     */
    T update(T object);

    /**
     * Updates the given instance in MDS if it exists (checks the presence of the instances id to verify that)
     * or creates a new one if it doesn't.
     *
     * @param object instance to update or create
     * @return updated or created instance
     */
    T createOrUpdate(T object);

    /**
     * Returns the persistent instance, updated with the values from the transient instance. If there's
     * no instance of the id from the transient instance, it will create one.
     *
     * @param transientObject transient object, from which an update will take place
     * @return persistent instance, updated with the values from the transient instance
     */
    T updateFromTransient(T transientObject);

    /**
     * Returns the persistent instance, updated with the values from the transient instance. If there's
     * no instance of the id from the transient instance, it will create one. Only fields with the names
     * passed to the method will be updated.
     *
     * @param transientObject transient object, from which an update will take place
     * @param fieldsToUpdate set of field names that should be updated
     * @return persistent instance, updated with the values from the transient instance
     */
    T updateFromTransient(T transientObject, Set<String> fieldsToUpdate);

    /**
     * Deletes given instance from MDS.
     *
     * @param object instance to delete
     */
    void delete(T object);

    /**
     * Deletes instance from MDS, by its id.
     *
     * @param id id of the instance to delete.
     */
    void deleteById(long id);

    /**
     * Deletes instance from MDS, by its primary key value.
     *
     * @param primaryKeyName datastore primary key name
     * @param value value of the primary key
     */
    void delete(String primaryKeyName, Object value);

    /**
     * Finds a trash instance for the entity by ID.
     * @param trashId the id of the trash instance
     * @return the instance from trash
     */
    Object findTrashInstanceById(Long trashId);

    /**
     * Brings an instance back from trash.
     * @param trashId the id of the trash instance
     * @return the brought back instance
     * @throws TrashInstanceNotFoundException if the trash instance with the given id was not found
     */
    T revertFromTrash(Long trashId);

    /**
     * Reverts data from a historical revision of the given instance.
     * @param instanceId the id of the instance which will be reverted
     * @param historicalId the id of the historical revision that we are reverting to
     * @return the reverted instance
     * @throws HistoryInstanceNotFoundException if the historical instance with historicalId was not found
     * @throws org.motechproject.mds.exception.object.ObjectNotFoundException if entity with instanceId was not found
     */
    T revertToHistoricalRevision(Long instanceId, Long historicalId);

    /**
     * Gets the total number of instances.
     *
     * @return number of instances
     */
    long count();

    /**
     * Returns detached copy of the given object
     *
     * @param object the object to be detached
     * @return the detached copy of the given object
     */
    T detachedCopy(T object);

    /**
     * Returns detached copies list of the given objects
     *
     * @param objects the objects to be detached
     * @return the detached copies list of the given objects
     */
    List<T> detachedCopyAll(List<T> objects);

    /**
     * Makes instance persistent and retrieves field values from that persisted instance.
     *
     * @param instance instance to retrieve field value from
     * @param fieldName name of the field to retrieve
     * @return value from the field
     */
    Object getDetachedField(T instance, String fieldName);

    /**
     * Retrieves all instances of type {@param <T>} from MDS, filtered using specified filters
     * and query params.
     *
     * @param filters filters to use filtering instances
     * @param queryParams query parameters to use filtering instances
     * @return a list of instances, filtered using specified parameters
     */
    List<T> filter(Filters filters, QueryParams queryParams);

    /**
     * Gets a total number of instances, after being filtered by the given filter.
     *
     * @param filters filters to use
     * @return number of filtered instances
     */
    long countForFilters(Filters filters);

    /**
     * Removes all instances of type {@param <T>} from MDS.
     */
    void deleteAll();

    /**
     * Allows to execute custom query in MDS. Users are supposed to implement the {@link QueryExecution}
     * interface and override its {@link QueryExecution#execute(javax.jdo.Query, org.motechproject.mds.util.InstanceSecurityRestriction)}
     * method with their custom behaviour.
     *
     * @param queryExecution implementation of the {@link QueryExecution}, with custom behaviour
     * @param <R> type that should be returned from the custom query
     * @return anything of type {@param <R>}. Left to the developer, implementing the custom query.
     */
    <R> R executeQuery(QueryExecution<R> queryExecution);

    /**
     * Evicts all cached entities. This affects all entities.
     */
    void evictAllCache();

    /**
     * Evicts cache for a single entity instance.
     *
     * @param instance the instance to clear the cache for
     */
    void evictCacheForInstance(T instance);

    /**
     * Evicts cache for the entity class of this data service.
     * @param withSubclasses if true, the cache for subclasses of the entity will be also cleared
     */
    void evictEntityCache(boolean withSubclasses);

    /**
     * Retrieves instance of type {@param <T>} and given id from MDS.
     *
     * @param id id of the instance
     * @return instance with the given id
     */
    T findById(Long id);

    /**
     * Retrieves multiple instances of type {@param <T>} and given ids from MDS. It will not fail
     * if it is unable to find an instance for one or more IDs and will return a collection of these
     * instances that could be found. If null is passed as keys, it will return an empty list.
     *
     * @param ids a collection of ids to find
     * @return a collection of instances with the given ids
     */
    List<T> findByIds(Collection<Long> ids);

    /**
     * Allows to wrap several instructions into a single transaction. Developers should implement
     * the {@link TransactionCallback} interface and override the
     * {@link TransactionCallback#doInTransaction(org.springframework.transaction.TransactionStatus)}
     * method with whatever should be done in the transaction.
     *
     * @param transactionCallback implementation of the {@link TransactionCallback}
     * @param <R> type that should be returned from the transaction
     * @return anything of type {@param <R>}. Left to the developer, implementing the transaction
     */
    <R> R doInTransaction(TransactionCallback<R> transactionCallback);

    /**
     * Allows to execute custom SQL query in MDS. Users should implement the {@link SqlQueryExecution} interface
     * and override its methods, defining their custom query.
     *
     * @param queryExecution implementation of the {@link SqlQueryExecution}
     * @param <R> type that should be returned by the custom sql query
     * @return anything of type {@param <R>}, left to the developer, implementing the custom sql query.
     */
    <R> R executeSQLQuery(SqlQueryExecution<R> queryExecution);

    /**
     * Returns class type assigned to this service.
     *
     * @return class type
     */
    Class<T> getClassType();

    /**
     * Returns the name of the version field for this entity.
     *
     * @return the name of the version field
     */
    String getVersionFieldName();


    /**
     * Returns the schema version for this service's entity.
     * @return the schema version
     */
    Long getSchemaVersion();

    /**
     * Checks whether the entity for this service has history recording enabled.
     * @return true if the entity has history recording enabled, false otherwise
     */
    boolean recordHistory();
}
