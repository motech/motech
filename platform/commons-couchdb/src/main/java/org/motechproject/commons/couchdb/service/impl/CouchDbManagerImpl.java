package org.motechproject.commons.couchdb.service.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.commons.couchdb.service.DbConnectionException;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CouchDbManagerImpl implements CouchDbManager {

    private Map<String, CouchDbConnector> couchDbConnectors = new HashMap<>();
    private HttpClientFactoryBean httpClientFactoryBean;
    private CouchDbInstance couchDbInstance;

    public CouchDbManagerImpl() throws IOException, DbConnectionException {

        httpClientFactoryBean = new HttpClientFactoryBean();

        Properties couchDbProperties = new Properties();
        try {
            FileSystemResource resource;
            final String configFile = String.format("%s/.motech/config/couchdb.properties", System.getProperty("user.home"));
            if (new File(configFile).exists()) {
                resource = new FileSystemResource(configFile);
            } else {
                resource = new FileSystemResource("/etc/motech/couchdb.properties");
            }
            couchDbProperties.load(resource.getInputStream());
        } catch (FileNotFoundException e) {
            URL resource = getClass().getClassLoader().getResource("couchdb.properties");
            couchDbProperties.load(resource.openStream());
        }
        configureDb(couchDbProperties);
    }

    private void configureDb(Properties couchDbProperties) throws DbConnectionException {
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

    public CouchDbConnector getConnector(String dbName) {
        String prefixedDbName = getDbPrefix() + dbName;
        if (!couchDbConnectors.containsKey(prefixedDbName)) {
            couchDbConnectors.put(prefixedDbName, couchDbInstance.createConnector(prefixedDbName, true));
        }
        return couchDbConnectors.get(prefixedDbName);
    }

    private String getDbPrefix() {
        Properties motechProperties = new Properties();
        try {
            motechProperties.load(getClass().getClassLoader().getResourceAsStream("motech.properties"));
        } catch (Exception ignore) {
        }

        String appName = motechProperties.getProperty("motech.app.name", null);
        if (appName == null || appName.trim().isEmpty() ) {
            return "";
        }
        return appName + "_";
    }
}
