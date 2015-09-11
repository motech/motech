package org.motechproject.mds.service;

import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.query.SqlQueryExecution;
import org.springframework.transaction.support.TransactionCallback;

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
     * Retrieves all instances of the {@value T} type.
     *
     * @return all instances
     */
    List<T> retrieveAll();

    /**
     * Retrieves all instances of the {@value T} type, that match the provided
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
     * Retrieves an instance, that has been moved to trash, by its id. These instances are
     * not retrieved with other retrieve methods.
     *
     * @param instanceId id of the instance, that has been moved to trash
     * @param entityId id of the entity
     * @return instance of the given id, from trash
     */
    T findTrashInstanceById(Object instanceId, Object entityId);

    /**
     * Brings back instance from trash. This will be in fact a new instance, that has got
     * exactly the same values as previous instance, except of its id.
     *
     * @param newInstance new instance representation
     * @param trash instance from the trash
     */
    void revertFromTrash(Object newInstance, Object trash);

    /**
     * Gets the total number of instances.
     *
     * @return number of instances
     */
    long count();

    /**
     * Makes instance persistent and retrieves field values from that persisted instance.
     *
     * @param instance instance to retrieve field value from
     * @param fieldName name of the field to retrieve
     * @return value from the field
     */
    Object getDetachedField(T instance, String fieldName);

    /**
     * Retrieves all instances of type {@value T} from MDS, filtered using specified filters
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
     * Removes all instances of type {@value T} from MDS.
     */
    void deleteAll();

    /**
     * Allows to execute custom query in MDS. Users are supposed to implement the {@link QueryExecution}
     * interface and override its {@link QueryExecution#execute(javax.jdo.Query, org.motechproject.mds.util.InstanceSecurityRestriction)}
     * method with their custom behaviour.
     *
     * @param queryExecution implementation of the {@link QueryExecution}, with custom behaviour
     * @param <R> type that should be returned from the custom query
     * @return anything of type {@value R}. Left to the developer, implementing the custom query.
     */
    <R> R executeQuery(QueryExecution<R> queryExecution);

    /**
     * Retrieves instance of type {@value T} and given id from MDS.
     *
     * @param id id of the instance
     * @return instance with the given id
     */
    T findById(Long id);

    /**
     * Allows to wrap several instructions into a single transaction. Developers should implement
     * the {@link TransactionCallback} interface and override the
     * {@link TransactionCallback#doInTransaction(org.springframework.transaction.TransactionStatus)}
     * method with whatever should be done in the transaction.
     *
     * @param transactionCallback implementation of the {@link TransactionCallback}
     * @param <R> type that should be returned from the transaction
     * @return anything of type {@value R}. Left to the developer, implementing the transaction
     */
    <R> R doInTransaction(TransactionCallback<R> transactionCallback);

    /**
     * Allows to execute custom SQL query in MDS. Users should implement the {@link SqlQueryExecution} interface
     * and override its methods, defining their custom query.
     *
     * @param queryExecution implementation of the {@link SqlQueryExecution}
     * @param <R> type that should be returned by the custom sql query
     * @return anything of type {@value R}, left to the developer, implementing the custom sql query.
     */
    <R> R executeSQLQuery(SqlQueryExecution<R> queryExecution);

    /**
     * Returns class type assigned to this service.
     *
     * @return class type
     */
    Class<T> getClassType();

    /**
     * Returns the name of the version field. Version field is not required so this method can return null value.
     *
     * @return the name of the version field
     */
    String getVersionFieldName();
}
