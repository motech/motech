package org.motechproject.commons.sql.service.impl;

import org.apache.commons.lang.text.StrSubstitutor;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Default implementation of the {@link org.motechproject.commons.sql.service.SqlDBManager}
 */
@Component
public class SqlDBManagerImpl implements SqlDBManager {

    private Properties sqlProperties;
    private CoreConfigurationService coreConfigurationService;

    public SqlDBManagerImpl() {
        sqlProperties = new Properties();
    }

    public SqlDBManagerImpl(CoreConfigurationService coreConfigurationService, Properties sqlProperties) {
        this.coreConfigurationService = coreConfigurationService;
        this.sqlProperties = sqlProperties;
        setSqlProperties();
    }

    @Override
    public Properties getSqlProperties(Properties propertiesToUpdate) throws IOException {
        Properties propertiesAfterUpdate = new Properties();
        propertiesAfterUpdate.load(new StringReader(StrSubstitutor.replace(getPropertiesAsString(propertiesToUpdate), sqlProperties)));
        return propertiesAfterUpdate;
    }

    @Override
    public void updateSqlProperties() {
        setSqlProperties();
    }

    private void setSqlProperties() {
        SQLDBConfig sqlConfig = coreConfigurationService.loadBootstrapConfig().getSqlConfig();
        String sqlUrl = sqlConfig.getUrl();
        sqlProperties.setProperty("sql.url", sqlUrl);

        String sqlUser = sqlConfig.getUsername();
        if (sqlUser != null) {
            sqlProperties.setProperty("sql.user", sqlUser);
        }

        String sqlPassword = sqlConfig.getPassword();
        if (sqlPassword != null) {
            sqlProperties.setProperty("sql.password", sqlPassword);
        }
    }

    private static String getPropertiesAsString(Properties prop) {
        StringWriter writer = new StringWriter();
        try {
            prop.store(writer, "");
        } catch (IOException e) {
        }
        return writer.getBuffer().toString();
    }
}
