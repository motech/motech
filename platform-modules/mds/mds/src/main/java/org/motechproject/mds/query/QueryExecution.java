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

    T execute(Query query, InstanceSecurityRestriction restriction);
}
