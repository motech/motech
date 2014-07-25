package org.motechproject.commons.couchdb.service.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.commons.api.Tenant;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.commons.couchdb.service.DbConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CouchDbManagerImpl implements CouchDbManager {
    private static final Logger LOG = LoggerFactory.getLogger(CouchDbManagerImpl.class);

    private static final String DB_URL = "url";
    private static final String DB_USERNAME = "username";

    private final Logger logger = LoggerFactory.getLogger(CouchDbManagerImpl.class);
    private Map<String, CouchDbConnector> couchDbConnectors = new HashMap<>();
    private HttpClientFactoryBean httpClientFactoryBean;
    private CouchDbInstance couchDbInstance;
    private Properties couchDbProperties;

    public CouchDbManagerImpl(Properties couchDbProperties) {
        this.couchDbProperties = couchDbProperties;
        this.httpClientFactoryBean = new HttpClientFactoryBean();

        configureDb();
    }

    @Override
    public CouchDbConnector getConnector(String dbName) {
        String prefixedDbName = getDbPrefix() + dbName;
        if (!couchDbConnectors.containsKey(prefixedDbName)) {
            couchDbConnectors.put(prefixedDbName, couchDbInstance.createConnector(prefixedDbName, true));
        }
        return couchDbConnectors.get(prefixedDbName);
    }

    private void configureDb() {
        LOG.info("Configuring couchDb connection to " + couchDbProperties.get(DB_URL));

        httpClientFactoryBean.setProperties(couchDbProperties);
        httpClientFactoryBean.setTestConnectionAtStartup(true);
        httpClientFactoryBean.setCaching(false);
        try {
            httpClientFactoryBean.afterPropertiesSet();
            couchDbConnectors.clear();
            couchDbInstance = new StdCouchDbInstance(httpClientFactoryBean.getObject());
        } catch (Exception e) {
            final String message = String.format("Failed to connect to couch DB. DB Url: %s using the username: %s.",
                    couchDbProperties.get(DB_URL),
                    couchDbProperties.get(DB_USERNAME));
            logger.error(message, e);
            throw new DbConnectionException(message, e);
        }
    }

    private String getDbPrefix() {
        return Tenant.current().getSuffixedId();
    }
}
