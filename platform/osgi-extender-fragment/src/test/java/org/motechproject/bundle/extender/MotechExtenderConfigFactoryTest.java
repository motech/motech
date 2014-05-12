package org.motechproject.bundle.extender;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class MotechExtenderConfigFactoryTest {

    @Test
    public void shouldReturnPropertiesWithTimeout() {
        try {
            System.setProperty(MotechExtenderConfigFactory.DEP_WAIT_TIME_ENV, "30000");

            Properties extenderCfg = new MotechExtenderConfigFactory().getObject();

            assertEquals(1, extenderCfg.size());
            assertEquals("30000", extenderCfg.getProperty(MotechExtenderConfigFactory.DEP_WAIT_TIME_KEY));
        } finally {
            System.clearProperty(MotechExtenderConfigFactory.DEP_WAIT_TIME_ENV);
        }
    }
}
