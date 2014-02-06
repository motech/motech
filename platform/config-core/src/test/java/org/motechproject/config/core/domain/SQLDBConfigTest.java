package org.motechproject.config.core.domain;

import org.junit.Test;
import org.motechproject.config.core.MotechConfigurationException;

public class SQLDBConfigTest {

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsBlank() {
        new SQLDBConfig("  ", null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsInvalid() {
        new SQLDBConfig("sdfsdb", null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsNotStartWithJdbc() {
        new SQLDBConfig("sql://localhost:3306/", null, null);
    }

    @Test
    public void shouldAcceptValidUrl() {
        new SQLDBConfig("jdbc:mysql://www.mydb.com:3306/", null, null);
    }

    @Test
    public void shouldAcceptLocalhostUrl() {
        new SQLDBConfig("jdbc:mysql://localhost:3306/", null, null);
    }
}
