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

    T create(T object);

    T retrieve(String primaryKeyName, Object value);

    List<T> retrieveAll();

    List<T> retrieveAll(QueryParams queryParams);

    T update(T object);

    T updateFromTransient(T transientObject);

    T updateFromTransient(T transientObject, Set<String> fieldsToUpdate);

    void delete(T object);

    void deleteById(long id);

    void delete(String primaryKeyName, Object value);

    T findTrashInstanceById(Object instanceId, Object entityId);

    void revertFromTrash(Object newInstance, Object trash);

    long count();

    Object getDetachedField(T instance, String fieldName);

    List<T> filter(Filters filters, QueryParams queryParams);

    long countForFilters(Filters filters);

    void deleteAll();

    <R> R executeQuery(QueryExecution<R> queryExecution);

    T findById(Long id);

    <R> R doInTransaction(TransactionCallback<R> transactionCallback);

    <R> R executeSQLQuery(SqlQueryExecution<R> queryExecution);

    Class<T> getClassType();
}
