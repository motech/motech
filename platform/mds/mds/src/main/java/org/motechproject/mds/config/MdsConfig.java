package org.motechproject.mds.config;

import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.mds.util.Constants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Class responsible for handling MDS configuration.
 * Since MDS don't use Server Config everything connected
 * to the MDS configuration needs to be handled by the
 * module itself.
 */
public class MdsConfig {

    private static final String FLYWAY_MYSQL_MIGRATION_PATH = "db/migration/mysql";
    private static final String FLYWAY_DEFAULT_MIGRATION_PATH = "db/migration/default";

    private Map<String, Properties> config = new HashMap<>();

    private SqlDBManager sqlDBManager;

    public MdsConfig() {
    }

    private CoreConfigurationService coreConfigurationService;
    private Properties mdsSqlProperties;

    public void init() {
        if (mdsSqlProperties == null) {
            mdsSqlProperties = getDataNucleusProperties();
        }
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

    public Properties getDataNucleusProperties() {
        return coreConfigurationService.loadDatanucleusConfig();
    }

    public String getFlywayLocations() {
        String driverName = sqlDBManager.getChosenSQLDriver();
        return driverName.equals(Constants.Config.MYSQL_DRIVER_CLASSNAME) ? FLYWAY_MYSQL_MIGRATION_PATH : FLYWAY_DEFAULT_MIGRATION_PATH;
    }

    public File getFlywayMigrationDirectory() {
        String flywayLocation = getFlywayLocations();
        File migrationDirectory = new File(System.getProperty("user.home"), Constants.EntitiesMigration.MIGRATION_DIRECTORY);
        migrationDirectory = new File(migrationDirectory, flywayLocation.substring(flywayLocation.lastIndexOf('/') + 1));

        return migrationDirectory;
    }

}
