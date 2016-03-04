package org.motechproject.mds.flyway;


import com.googlecode.flyway.core.Flyway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.config.MdsConfig;

import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FlywayInitializerTest {

    @InjectMocks
    private FlywayInitializer flywayInitializer = new FlywayInitializer();

    @Mock
    private Properties flywayProperties;

    @Mock
    private Flyway flyway;

    @Mock
    private MdsConfig mdsConfig;

    @Test
    public void shouldInitializeDb() {
        when(mdsConfig.getFlywaySchemaProperties()).thenReturn(flywayProperties);

        flywayInitializer.migrate();

        verify(flyway).configure(flywayProperties);
        verify(flyway).migrate();
    }
}
