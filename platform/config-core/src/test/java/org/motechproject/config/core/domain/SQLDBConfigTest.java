package org.motechproject.config.core.domain;

import org.junit.Test;
import org.motechproject.config.core.exception.MotechConfigurationException;

public class SQLDBConfigTest {

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsBlank() {
        new SQLDBConfig("  ", "none", null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsInvalid() {
        new SQLDBConfig("sdfsdb", "none",null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsNotStartWithJdbc() {
        new SQLDBConfig("sql://localhost:3306/", "none", null, null);
    }

    @Test
    public void shouldAcceptValidUrl() {
        new SQLDBConfig("jdbc:mysql://www.mydb.com:3306/", "none", null, null);
    }

    /**
     * Adding test for AWS and other hosted db urls which usually contain a dash '-'
     */
    @Test
    public void shouldAcceptValidUrlWithDash() {
        new SQLDBConfig("jdbc:mysql://www.my-db.com:3306/", "none", null, null);
    }

    @Test
    public void shouldAcceptLocalhostUrl() {
        new SQLDBConfig("jdbc:mysql://localhost:3306/", "none", null, null);
    }
}
