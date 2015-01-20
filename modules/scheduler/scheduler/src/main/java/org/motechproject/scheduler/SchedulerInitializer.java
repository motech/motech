package org.motechproject.scheduler;


import org.motechproject.commons.sql.service.SqlDBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Class responsible for preparing Database for Scheduler.
 */
@Component
public class SchedulerInitializer {

    private static final String MYSQL_DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";
    private static final String FLYWAY_MYSQL_MIGRATION_PATH = "db/migration/scheduler/mysql";
    private static final String FLYWAY_DEFAULT_MIGRATION_PATH = "db/migration/scheduler/default";
    private static final String DATABASE_URL = "org.quartz.dataSource.motechDS.URL";

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
        return driverName.equals(MYSQL_DRIVER_CLASSNAME) ? FLYWAY_MYSQL_MIGRATION_PATH : FLYWAY_DEFAULT_MIGRATION_PATH;
    }

}
