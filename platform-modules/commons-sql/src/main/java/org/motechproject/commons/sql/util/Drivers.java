package org.motechproject.commons.sql.util;

/**
 * Utility for storing supported SQL and Quartz driver class names.
 */
public final class Drivers {

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    public static final String QUARTZ_STD_JDBC_DELEGATE = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
    public static final String QUARTZ_POSTGRESQL_DELEGATE = "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";

    /**
     * This is a utility class and should not be instantiated
     */
    private Drivers() {
    }
}
