package org.motechproject.commons.sql.service.impl;

import org.apache.commons.lang.text.StrSubstitutor;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.commons.sql.util.Drivers;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.motechproject.config.core.domain.BootstrapConfig.SQL_DRIVER;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_PASSWORD;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_USER;

/**
 * Default implementation of the {@link org.motechproject.commons.sql.service.SqlDBManager}
 */
@Component
public class SqlDBManagerImpl implements SqlDBManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlDBManagerImpl.class);
    private static final String MYSQL_DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

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

    @Override
    public String getChosenSQLDriver() {
        return sqlProperties.get(SQL_DRIVER).toString();
    }

    @Override
    public boolean createDatabase(String dbName) {
        String name = prepareDatabaseName(dbName);
        boolean created = false;

        // check if database already exists - if yes then there's no need to create it again
        if (checkForDatabase(name)) {
            return false;
        }

        try {
            Class.forName(getChosenSQLDriver());
        } catch (ClassNotFoundException e) {
            LOGGER.error(getChosenSQLDriver() + " class not found.", e);
        }
        try (Connection conn = DriverManager.getConnection(sqlProperties.get(SQL_URL).toString(), sqlProperties.get(SQL_USER).toString(),
                sqlProperties.get(SQL_PASSWORD).toString());
            Statement stmt = conn.createStatement()) {
            LOGGER.info("Creating database " + name);
            String sql = "CREATE DATABASE " + name;
            stmt.executeUpdate(sql);
            LOGGER.info("Database " + name + " created");
            created = true;
        } catch (SQLException e) {
            LOGGER.error("Error while creating database " + name, e);
        }
        return created;
    }

    public boolean checkForDatabase(String dbName) {
        boolean exist = false;
        String name = prepareDatabaseName(dbName);

        try {
            Class.forName(getChosenSQLDriver());
        } catch (ClassNotFoundException e) {
            LOGGER.error(getChosenSQLDriver() + " class not found.", e);
        }
        try (Connection conn = DriverManager.getConnection(sqlProperties.get(SQL_URL).toString(), sqlProperties.get(SQL_USER).toString(),
                sqlProperties.get(SQL_PASSWORD).toString()); Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            if (MYSQL_DRIVER_CLASSNAME.equalsIgnoreCase(getChosenSQLDriver())) {
                sb = sb.append("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '").append(name).append("'");
            } else {
                sb = sb.append("SELECT datname FROM pg_catalog.pg_database WHERE datname = '").append(name).append("'");
            }
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                exist = true;
            }
        } catch (SQLException e) {
            LOGGER.error("Error while checking for database", e);
        }
        return exist;
    }

    public boolean hasColumn(String database, String table, String column) throws SQLException {
        boolean hasColumn;

        try (Connection conn = DriverManager.getConnection(sqlProperties.get(SQL_URL).toString() + database, sqlProperties.get(SQL_USER).toString(),
                sqlProperties.get(SQL_PASSWORD).toString())) {

            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getColumns(null, null, table, column);

            hasColumn = rs.next();
        }

        return hasColumn;
    }

    private String prepareDatabaseName(String dbName) {
        String name;
        if (dbName.contains("/")) {
            name = dbName.substring(dbName.lastIndexOf('/') + 1, dbName.length());
        } else if (dbName.contains("${")) {
            name = dbName.substring(dbName.indexOf('}') + 1, dbName.length());
        } else {
            name = dbName;
        }
        return name;
    }

    private void setSqlProperties() {
        SQLDBConfig sqlConfig = coreConfigurationService.loadBootstrapConfig().getSqlConfig();
        String sqlUrl = sqlConfig.getUrl();
        sqlProperties.setProperty(SQL_URL, sqlUrl);

        String sqlUser = sqlConfig.getUsername();
        if (sqlUser != null) {
            sqlProperties.setProperty(SQL_USER, sqlUser);
        }

        String sqlPassword = sqlConfig.getPassword();
        if (sqlPassword != null) {
            sqlProperties.setProperty(SQL_PASSWORD, sqlPassword);
        }

        String sqlDriver = sqlConfig.getDriver();
        if (sqlDriver != null) {
            sqlProperties.setProperty("sql.driver", sqlDriver);
        }

        String quartzDelegate = getQuartzDriverDeletegate(sqlDriver);
        sqlProperties.setProperty("sql.quartz.delegateClass", quartzDelegate);
    }

    private static String getPropertiesAsString(Properties prop) {
        StringWriter writer = new StringWriter();
        try {
            prop.store(writer, "");
        } catch (IOException e) {
            LOGGER.error("Unable to get properties as String", e);
        }
        return writer.getBuffer().toString();
    }

    public void setSqlProperties(Properties sqlProperties) {
        this.sqlProperties = sqlProperties;
    }

    public void setCoreConfigurationService(CoreConfigurationService coreConfigurationService) {
        this.coreConfigurationService = coreConfigurationService;
    }

    private String getQuartzDriverDeletegate(String sqlDriver) {
        if (Drivers.POSTGRESQL_DRIVER.equals(sqlDriver)) {
            return Drivers.QUARTZ_POSTGRESQL_DELEGATE;
        } else {
            return Drivers.QUARTZ_STD_JDBC_DELEGATE;
        }
    }
}
