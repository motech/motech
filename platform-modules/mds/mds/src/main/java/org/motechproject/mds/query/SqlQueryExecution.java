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

    T execute(Query query);

    String getSqlQuery();
}
