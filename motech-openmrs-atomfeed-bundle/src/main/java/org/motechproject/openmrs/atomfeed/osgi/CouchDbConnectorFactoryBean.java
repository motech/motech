package org.motechproject.openmrs.atomfeed.osgi;

import org.ektorp.CouchDbConnector;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class CouchDbConnectorFactoryBean implements FactoryBean<CouchDbConnector> {

    private final PlatformSettingsService platformSettingsService;
    private CouchDbConnector couchDbConnector;

    @Autowired
    public CouchDbConnectorFactoryBean(PlatformSettingsService platformSettingsService) {
        this.platformSettingsService = platformSettingsService;
    }

    @Override
    public CouchDbConnector getObject() throws Exception {
        if (couchDbConnector == null) {
            couchDbConnector = platformSettingsService.getCouchConnector("motech-openmrs-atomfeed");
        }

        return couchDbConnector;
    }

    @Override
    public Class<?> getObjectType() {
        return CouchDbConnector.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
