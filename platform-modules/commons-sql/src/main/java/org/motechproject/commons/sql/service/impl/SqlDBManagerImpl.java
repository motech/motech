package org.motechproject.commons.sql.service.impl;

import org.apache.commons.lang.text.StrSubstitutor;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.commons.sql.util.Drivers;
import org.motechproject.commons.sql.util.JdbcUrl;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import static org.motechproject.config.core.domain.BootstrapConfig.SQL_DRIVER;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_PASSWORD;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_USER;

/**
 * Default implementation of the {@link org.motechproject.commons.sql.service.SqlDBManager}
 */
@Service("sqlDbManager")
public class SqlDBManagerImpl implements SqlDBManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlDBManagerImpl.class);
    private static final String MYSQL_DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION_URL_KEY = "javax.jdo.option.ConnectionURL";
    private static final String SQL_VARIABLE = "${sql.url}";

    private Properties sqlProperties;
    private CoreConfigurationService coreConfigurationService;

    /**
     * Constructs the instance using a {@link org.motechproject.config.core.service.CoreConfigurationService}
     * instance. The sql properties are retrieved from bootstrap configuration
     * @param coreConfigurationService the core configuration service for the platform
     */
    @Autowired
    public SqlDBManagerImpl(CoreConfigurationService coreConfigurationService) {
        this.coreConfigurationService = coreConfigurationService;
        loadSqlProperties();
    }

    /**
     * Constructs the instance with a predefined set of properties.
     * @param sqlProperties the properties to be used
     */
    public SqlDBManagerImpl(Properties sqlProperties) {
        this.sqlProperties = sqlProperties;
    }

    @Override
    public Properties getSqlProperties(Properties propertiesToUpdate) throws IOException {
        Properties propertiesAfterUpdate = new Properties();
        propertiesAfterUpdate.putAll(propertiesToUpdate);

        replaceProperties(propertiesAfterUpdate);

        return propertiesAfterUpdate;
    }

    @Override
    public String getChosenSQLDriver() {
        return sqlProperties.get(SQL_DRIVER).toString();
    }

    @Override
    public boolean createDatabase(String connectionUrl) {
        boolean created = false;

        loadSQLDriverClass();

        JdbcUrl jdbcUrl = prepareConnectionUri(connectionUrl);
        String dbName = jdbcUrl.getDbName();
        String serverUrl = jdbcUrl.getUrlForDbServer();

        // check if database already exists - if yes then there's no need to create it again
        if (checkForDatabase(connectionUrl)) {
            return false;
        }

        try (Connection conn = buildConnection(serverUrl); Statement stmt = conn.createStatement()) {
            LOGGER.info("Creating database " + dbName);
            String sql = "CREATE DATABASE " + dbName;
            stmt.executeUpdate(sql);
            LOGGER.info("Database " + dbName + " created");
            created = true;
        } catch (SQLException e) {
            LOGGER.error("Error while creating database " + dbName, e);
        }

        return created;
    }

    @Override
    public boolean checkForDatabase(String connectionUrl) {
        boolean exist = false;

        JdbcUrl jdbcUrl = prepareConnectionUri(connectionUrl);
        String dbName = jdbcUrl.getDbName();
        String serverUrl = jdbcUrl.getUrlForDbServer();

        loadSQLDriverClass();

        try (Connection conn = buildConnection(serverUrl); Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            if (MYSQL_DRIVER_CLASSNAME.equalsIgnoreCase(getChosenSQLDriver())) {
                sb = sb.append("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '").append(dbName).append("'");
            } else {
                sb = sb.append("SELECT datname FROM pg_catalog.pg_database WHERE datname = '").append(dbName).append("'");
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

    @Override
    public boolean hasColumn(String connectionUrl, String table, String column) throws SQLException {
        boolean hasColumn;

        try (Connection conn = buildConnection(connectionUrl)) {
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getColumns(null, null, table, column);

            hasColumn = rs.next();
        }

        return hasColumn;
    }

    @Override
    public JdbcUrl prepareConnectionUri(String connectionUrl) {
        String parsedConnection = StrSubstitutor.replace(parseConnectionString(connectionUrl), sqlProperties);
        try {
            return new JdbcUrl(parsedConnection);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid connection url " + connectionUrl, e);
        }
    }

    private void loadSqlProperties() {
        sqlProperties = new Properties();
        SQLDBConfig sqlConfig = coreConfigurationService.loadBootstrapConfig().getSqlConfig();

        String sqlUrl = sqlConfig.getUrl();
        if (!sqlUrl.endsWith("/")) {
            sqlUrl.concat("/");
        }
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
            sqlProperties.setProperty(SQL_DRIVER, sqlDriver);
        }

        String quartzDelegate = getQuartzDriverDeletegate(sqlDriver);
        sqlProperties.setProperty("sql.quartz.delegateClass", quartzDelegate);
    }

    private void loadSQLDriverClass() {
        try {
            Class.forName(getChosenSQLDriver());
        } catch (ClassNotFoundException e) {
            LOGGER.error(getChosenSQLDriver() + " class not found.", e);
        }
    }

    private String parseConnectionString(String connectionString) {
        if (connectionString.startsWith(SQL_VARIABLE) && connectionString.length() > SQL_VARIABLE.length()
                && connectionString.charAt(SQL_VARIABLE.length()) == '/') {
            return connectionString.replaceFirst("/", "");
        }

        return connectionString;
    }

    private void replaceProperties(Properties props) {
        StrSubstitutor substitutor = new StrSubstitutor(sqlProperties);

        // we must delete slash(it is added to the ${sql.url}) from connection string -> ${sql.url}/database
        if (props.getProperty(CONNECTION_URL_KEY) != null) {
            String connectionUrl = parseConnectionString(props.getProperty(CONNECTION_URL_KEY));
            props.put(CONNECTION_URL_KEY, connectionUrl);
        }

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            if (entry.getValue() instanceof String) {
                String substituted = substitutor.replace(entry.getValue());
                entry.setValue(substituted);
            }
        }
    }

    private String getQuartzDriverDeletegate(String sqlDriver) {
        if (Drivers.POSTGRESQL_DRIVER.equals(sqlDriver)) {
            return Drivers.QUARTZ_POSTGRESQL_DELEGATE;
        } else {
            return Drivers.QUARTZ_STD_JDBC_DELEGATE;
        }
    }

    private Connection buildConnection(String jdbcUrl) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, sqlProperties.getProperty(SQL_USER),
                sqlProperties.getProperty(SQL_PASSWORD));
    }
}
