package org.motechproject.server.bootstrap;

import org.apache.commons.lang.StringUtils;
import org.motechproject.config.core.domain.SQLDBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionUtil {

    private ConnectionUtil() {
    }

    public static Connection getConnection(SQLDBConfig config) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Connection sqlConnection = null;

        Class.forName(config.getDriver()).newInstance();
        if (StringUtils.isNotBlank(config.getPassword()) || StringUtils.isNotBlank(config.getUsername())) {
            sqlConnection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } else {
            sqlConnection = DriverManager.getConnection(config.getUrl());
        }

        return sqlConnection;
    }
}