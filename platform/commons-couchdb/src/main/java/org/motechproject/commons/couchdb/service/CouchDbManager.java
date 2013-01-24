package org.motechproject.commons.couchdb.service;

import org.ektorp.CouchDbConnector;

public interface CouchDbManager {
    CouchDbConnector getConnector(String dbName);
}
