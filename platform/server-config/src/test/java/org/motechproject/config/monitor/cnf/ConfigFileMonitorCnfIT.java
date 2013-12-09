package org.motechproject.config.monitor.cnf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.monitor.ConfigFileMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class ConfigFileMonitorCnfIT {

    @Autowired
    private ConfigFileMonitor configFileMonitor;

    @Test
    public void shouldCreateConfigFileMonitorBeanInFileConfigSource() {
        assertNotNull(configFileMonitor);
    }
}
