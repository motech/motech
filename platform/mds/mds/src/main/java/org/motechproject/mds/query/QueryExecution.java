package org.motechproject.mds.query;

import org.motechproject.mds.util.InstanceSecurityRestriction;

import javax.jdo.Query;

/**
 * Allows users to execute custom queries through Motech Data Services.
 * Implementations need only to implement the execute method, which can operate
 * directly on the {@link javax.jdo.Query} object. The return value type is left to
 * the implementation.
 *
 * @param <T> the type that will be returned from this query
 */
public interface QueryExecution<T> {

    /**
     * The implementation of this method is supposed to run the JDO query on the
     * provided {@link Query} object.
     * @param query the query object that is supposed to be executed
     * @param restriction the entity instances restrictions put on this query
     * @return defining the return type is left to whoever implements this interface
     */
    T execute(Query query, InstanceSecurityRestriction restriction);
}
