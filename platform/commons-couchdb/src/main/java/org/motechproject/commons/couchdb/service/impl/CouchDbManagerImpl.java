package org.motechproject.commons.couchdb.service.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.commons.api.Tenant;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.commons.couchdb.service.DbConnectionException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CouchDbManagerImpl implements CouchDbManager {

    private static final String DB_URL = "url";
    private static final String DB_USERNAME = "username";
    private static final String DB_PASSWORD = "password";
    private final Logger logger = LoggerFactory.getLogger(CouchDbManagerImpl.class);

    private Map<String, CouchDbConnector> couchDbConnectors = new HashMap<>();
    private HttpClientFactoryBean httpClientFactoryBean;

    private CouchDbInstance couchDbInstance;
    private Properties couchdbProperties;
    private CoreConfigurationService coreConfigurationService;
    private boolean couchDBInitialized = false;

    public CouchDbManagerImpl() {
    }

    public CouchDbManagerImpl(CoreConfigurationService coreConfigurationService, Properties couchdbProperties) {
        this.coreConfigurationService = coreConfigurationService;
        this.couchdbProperties = couchdbProperties;
        httpClientFactoryBean = new HttpClientFactoryBean();
    }

    @Override
    public CouchDbConnector getConnector(String dbName) {
        return getTargetConnector(dbName);
    }

    public synchronized CouchDbConnector getTargetConnector(String dbName) {
        if (!couchDBInitialized) {
            configureDb();
        }
        String prefixedDbName = getDbPrefix() + dbName;
        if (!couchDbConnectors.containsKey(prefixedDbName)) {
            couchDbConnectors.put(prefixedDbName, couchDbInstance.createConnector(prefixedDbName, true));
        }
        return couchDbConnectors.get(prefixedDbName);
    }

    private void configureDb() {
        final Properties mergedCouchdbProps = getCouchdbProperties();
        httpClientFactoryBean.setProperties(mergedCouchdbProps);
        httpClientFactoryBean.setTestConnectionAtStartup(true);
        httpClientFactoryBean.setCaching(false);
        try {
            httpClientFactoryBean.afterPropertiesSet();
            couchDbConnectors.clear();
            couchDbInstance = new StdCouchDbInstance(httpClientFactoryBean.getObject());
            couchDBInitialized = true;
        } catch (Exception e) {
            final String message = String.format("Failed to connect to couch DB. DB Url: %s using the username: %s.",
                    mergedCouchdbProps.get(BootstrapConfig.DB_URL),
                    mergedCouchdbProps.get(BootstrapConfig.DB_USERNAME));
            logger.error(message, e);
            throw new DbConnectionException(message, e);
        }
    }

    private Properties getCouchdbProperties() {
        DBConfig dbConfig = coreConfigurationService.loadBootstrapConfig().getDbConfig();
        Properties mergedProps = new Properties();
        mergedProps.putAll(couchdbProperties);
        mergedProps.setProperty(DB_URL, dbConfig.getUrl());
        mergedProps.setProperty(DB_USERNAME, dbConfig.getUsername());
        mergedProps.setProperty(DB_PASSWORD, dbConfig.getPassword());
        return mergedProps;
    }

    private String getDbPrefix() {
        return Tenant.current().getSuffixedId();
    }
}
