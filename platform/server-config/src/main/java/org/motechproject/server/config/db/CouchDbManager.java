package org.motechproject.server.config.db;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class CouchDbManager {

    private HttpClientFactoryBean httpClientFactoryBean = new HttpClientFactoryBean();
    private CouchDbInstance couchDbInstance;
    private Map<String, CouchDbConnector> couchDbConnectors = new HashMap<>();

    public CouchDbManager() {
        // do nothing
    }

    public CouchDbManager(CouchDbInstance couchDbInstance) {
        this.couchDbInstance = couchDbInstance;
    }

    public CouchDbManager(Properties couchDbProperties) throws DbConnectionException {
        configureDb(couchDbProperties);
    }

    public void setCouchDbInstance(CouchDbInstance couchDbInstance) {
        this.couchDbInstance = couchDbInstance;
    }

    public CouchDbConnector getConnector(String dbName, boolean createIfNotExists) {
        if (!couchDbConnectors.containsKey(dbName)) {
            CouchDbConnector connector = couchDbInstance.createConnector(dbName, createIfNotExists);
            couchDbConnectors.put(dbName, connector);
        }
        return couchDbConnectors.get(dbName);
    }

    public void configureDb(Properties couchDbProperties) throws DbConnectionException {
        httpClientFactoryBean.setProperties(couchDbProperties);
        httpClientFactoryBean.setTestConnectionAtStartup(true);
        httpClientFactoryBean.setCaching(false);

        try {
            httpClientFactoryBean.afterPropertiesSet();
            couchDbConnectors.clear();
            couchDbInstance = new StdCouchDbInstance(httpClientFactoryBean.getObject());
        } catch (Exception e) {
            throw new DbConnectionException("Failed to connect to DB", e);
        }
    }
}
