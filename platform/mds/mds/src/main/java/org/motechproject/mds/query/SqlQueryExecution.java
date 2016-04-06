package org.motechproject.mds.query;

import javax.jdo.Query;

/**
 * Allows users to execute custom SQL queries through Motech Data Services.
 * Implementations need to implement the execute method, which can operate
 * directly on the {@link javax.jdo.Query} object and {@link #getSqlQuery()} should return
 * the sql query that will be executed. The return value type is left to the implementation.
 * It is not advised to rely on raw SQL, however some use cases may require it.
 *
 * @param <T> the type that will be returned from this query
 */
public interface SqlQueryExecution<T> {

    /**
     * The implementation of this method should prepare the {@link Query} object for the
     * SQL query execution (eg. by inserting the necessary params).
     *
     * @param query query object, that will be used during query execution
     * @return defining the return type is left to whoever implements this interface
     */
    T execute(Query query);

    /**
     * @return raw SQL query that will be executed
     */
    String getSqlQuery();
}
