package org.motechproject.commons.sql.service;

import java.io.IOException;
import java.util.Properties;

/**
 * Classes inheriting this interface are responsible for getting sql properties from the bootstrap configuration.
 */
public interface SqlDBManager {
    Properties getSqlProperties(Properties propertiesToUpdate) throws IOException;

    void updateSqlProperties();
}
