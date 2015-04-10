package org.motechproject.scheduler;


import org.motechproject.commons.sql.service.SqlDBManager;
import org.motechproject.commons.sql.util.Drivers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Class responsible for preparing Database for Scheduler.
 */
@Component
public class SchedulerInitializer {

    public static final String DATABASE_URL = "org.quartz.dataSource.motechDS.URL";
    private static final String FLYWAY_MYSQL_MIGRATION_PATH = "db/migration/scheduler/mysql";
    private static final String FLYWAY_DEFAULT_MIGRATION_PATH = "db/migration/scheduler/default";

    @Autowired
    private SqlDBManager sqlDBManager;

    @Autowired
    private Properties sqlProperties;

    @PostConstruct
    public void init() {
        sqlDBManager.createDatabase(sqlProperties.getProperty(DATABASE_URL));
    }

    public String getFlywayLocations() {
        String driverName = sqlDBManager.getChosenSQLDriver();
        return driverName.equals(Drivers.MYSQL_DRIVER) ? FLYWAY_MYSQL_MIGRATION_PATH : FLYWAY_DEFAULT_MIGRATION_PATH;
    }

}
