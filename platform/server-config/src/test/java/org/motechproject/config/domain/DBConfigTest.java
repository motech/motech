package org.motechproject.config.domain;

import org.junit.Test;
import org.motechproject.config.MotechConfigurationException;

public class DBConfigTest {

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsBlank() {
        new DBConfig("  ", null, null);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfUrlIsInvalid() {
        new DBConfig("sdfsdb", null, null);
    }

    @Test
    public void shouldAcceptValidUrl() {
        new DBConfig("http://www.mydb.com:5984", null, null);
    }

    @Test
    public void shouldAcceptLocalhostUrl() {
        new DBConfig("http://localhost:5984", null, null);
    }
}
