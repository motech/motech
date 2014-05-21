package org.motechproject.commons.couchdb.service;

import org.ektorp.CouchDbConnector;

/**
 * @deprecated As of release 0.24, MDS replaces CouchDB for persistence
 */
@Deprecated
public interface CouchDbManager {
    CouchDbConnector getConnector(String dbName);
}
