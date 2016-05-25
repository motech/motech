package org.motechproject.mds.config;

import org.datanucleus.PropertyNames;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.commons.sql.util.JdbcUrl;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.mds.util.Constants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class responsible for handling MDS configuration.
 * Since MDS does not use Server Config, everything connected
 * to the MDS configuration needs to be handled by the
 * module itself.
 */
public class MdsConfig {

    private static final String FLYWAY_MYSQL_MIGRATION_PATH = "db/migration/mysql";
    private static final String FLYWAY_JAVA_MIGRATION_PATH = "org/motechproject/mdsmigration/java";
    private static final String FLYWAY_DEFAULT_MIGRATION_PATH = "db/migration/default";
    private static final String CONNECTION_URL_KEY = "javax.jdo.option.ConnectionURL";
    private static final String QUARTZ_CONNECTION_URL_KEY = "org.quartz.dataSource.motechDS.URL";
    private static final String SCHEMA_CONFIG = "SCHEMA_CONFIG";
    private static final String QUARTZ_CONFIG = "QUARTZ_CONFIG";

    private Map<String, Properties> config = new HashMap<>();

    private SqlDBManager sqlDBManager;
    private CoreConfigurationService coreConfigurationService;
    private Properties mdsDataSqlProperties;
    private Properties mdsInternalSqlProperties;
    private Properties mdsQuartzSqlProperties;

    public MdsConfig() {}

    public void init() {
        if (mdsDataSqlProperties == null) {
            mdsDataSqlProperties = getDataNucleusProperties();
        }

        if (mdsInternalSqlProperties == null) {
            mdsInternalSqlProperties = getDataNucleusPropertiesForInternalInfrastructure(SCHEMA_CONFIG);
        }

        if(mdsQuartzSqlProperties == null) {
            mdsQuartzSqlProperties = getDataNucleusPropertiesForInternalInfrastructure(QUARTZ_CONFIG);
        }

        //Create database if it doesn't exists
        sqlDBManager.createDatabase(
                mdsDataSqlProperties.getProperty(CONNECTION_URL_KEY)
        );
        sqlDBManager.createDatabase(
                mdsInternalSqlProperties.getProperty(CONNECTION_URL_KEY)
        );
        sqlDBManager.createDatabase(
                mdsQuartzSqlProperties.getProperty(QUARTZ_CONNECTION_URL_KEY)
        );
    }

    public void setConfig(List<Resource> resources) {
        for (Resource configFile : resources) {
            try (InputStream is = configFile.getInputStream()) {
                Properties props = new Properties();
                props.load(is);

                config.put(getResourceFileName(configFile), props);
            } catch (IOException e) {
                throw new MotechException("Cant load config file " + configFile.getFilename(), e);
            }
        }
    }


    public void setCoreConfigurationService(CoreConfigurationService coreConfigurationService) {
        this.coreConfigurationService = coreConfigurationService;
    }

    public void setSqlDBManager(SqlDBManager sqlDBManager) {
        this.sqlDBManager = sqlDBManager;
    }

    public  String getResourceFileName(Resource resource) {
        String name = resource.getFilename();

        if (resource instanceof ClassPathResource) {
            name = ((ClassPathResource) resource).getPath();
        } else {
            int colonIndex = name.indexOf(':');
            if (colonIndex >= 0) {
                name = name.substring(colonIndex + 1);
            }
        }
        return name;
    }

    public Properties asProperties() {
        Properties result = new Properties();
        for (Properties p : config.values()) {
            result.putAll(p);
        }
        result.putAll(getDataNucleusProperties());
        return result;
    }

    public Properties getProperties(String filename) {
        Properties result = config.get(filename);
        return (result == null ? new Properties() : result);
    }

    public Properties getDataNucleusPropertiesForInternalInfrastructure(String configToLoad) {
        // this for the MDS bundle itself, as opposed to the entities bundle being generated
        Properties properties = new Properties();
        if (configToLoad.equals("SCHEMA_CONFIG")) {
            properties.putAll(coreConfigurationService.loadDatanucleusSchemaConfig());
        } else if (configToLoad.equals("QUARTZ_CONFIG")) {
            properties.putAll(coreConfigurationService.loadDatanucleusQuartzConfig());
        }
        addBeanValidationFactoryProperty(properties);

        return properties;
    }

    public Properties getDataNucleusProperties() {
        Properties properties = new Properties();
        properties.putAll(coreConfigurationService.loadDatanucleusDataConfig());
        addBeanValidationFactoryProperty(properties);
        return properties;
    }

    public String getDataDatabaseName() {
        JdbcUrl jdbcUrl = sqlDBManager.prepareConnectionUri(coreConfigurationService.loadDatanucleusDataConfig().getProperty(CONNECTION_URL_KEY));
        return jdbcUrl.getDbName();
    }

    public String getSchemaDatabaseName() {
        JdbcUrl jdbcUrl = sqlDBManager.prepareConnectionUri(coreConfigurationService.loadDatanucleusSchemaConfig().getProperty(CONNECTION_URL_KEY));
        return jdbcUrl.getDbName();
    }

    public String[] getFlywayLocations() {
        String driverName = sqlDBManager.getChosenSQLDriver();
        String sqlMigrationPath = driverName.equals(Constants.Config.MYSQL_DRIVER_CLASSNAME) ? FLYWAY_MYSQL_MIGRATION_PATH : FLYWAY_DEFAULT_MIGRATION_PATH;
        return new String[]{sqlMigrationPath, FLYWAY_JAVA_MIGRATION_PATH};
    }

    public File getFlywayMigrationDirectory() {
        String flywayLocation = getFlywayLocations()[0];
        File migrationDirectory = new File(coreConfigurationService.loadBootstrapConfig().getMotechDir(), Constants.EntitiesMigration.MIGRATION_DIRECTORY);
        migrationDirectory = new File(migrationDirectory, flywayLocation.substring(flywayLocation.lastIndexOf('/') + 1));
        return migrationDirectory;
    }

    public Properties getFlywayDataProperties() {
        return coreConfigurationService.loadFlywayDataConfig();
    }

    public Properties getFlywaySchemaProperties() {
        return coreConfigurationService.loadFlywaySchemaConfig();
    }

    private void addBeanValidationFactoryProperty(Properties properties) {
        // Datanucleus expects the validator factory as the actual object, not just a string property
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        properties.put(PropertyNames.PROPERTY_VALIDATION_FACTORY, validatorFactory);
    }
}
