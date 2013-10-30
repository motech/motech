package org.motechproject.commons.couchdb.service;

import org.ektorp.CouchDbConnector;

public interface CouchDbManager {
    /**
     * Gets the couchdb connector
     *
     * @param dbName name of the db
     * @return couchdb connector.
     * @throws DbConnectionException if connection to the given db fails.
     */
    CouchDbConnector getConnector(String dbName);
}
