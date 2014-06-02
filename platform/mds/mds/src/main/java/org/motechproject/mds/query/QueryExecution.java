package org.motechproject.mds.query;

import javax.jdo.Query;

/**
 * Allows users to execute custom queries through Motech Data Services.
 * Implementations need only to implement the execute method, which can operate
 * directly on the {@link javax.jdo.Query} object. The return value type is left to
 * the implementation.
 */
public interface QueryExecution {

    Object execute(Query query);
}
