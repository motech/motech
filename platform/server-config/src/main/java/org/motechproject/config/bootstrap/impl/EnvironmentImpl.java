package org.motechproject.config.bootstrap.impl;

import org.motechproject.config.bootstrap.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentImpl implements Environment {
    @Override
    public String getConfigDir() {
        return getValue(MOTECH_CONFIG_DIR);
    }

    @Override
    public String getDBUrl() {
        return getValue(MOTECH_DB_URL);
    }

    @Override
    public String getDBUsername() {
        return getValue(MOTECH_DB_USERNAME);
    }

    @Override
    public String getDBPassword() {
        return getValue(MOTECH_DB_PASSWORD);
    }

    @Override
    public String getTenantId() {
        return getValue(MOTECH_TENANT_ID);
    }

    @Override
    public String getConfigSource() {
        return getValue(MOTECH_CONFIG_SOURCE);
    }

    String getValue(String variableName) {
        return System.getenv(variableName);
    }
}
