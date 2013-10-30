package org.motechproject.event.config;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class EventConfigManagerIT {

    @Autowired
    private EventConfigManager eventConfigManager;

    private String currentUserPrefix;

    @Before
    public void setUp() throws IOException {
        currentUserPrefix = Tenant.current().getSuffixedId();
    }

    @Test
    public void shouldFallbackToClasspathResource() throws IOException {

        Properties activeMqConfig = eventConfigManager.getActivemqConfig();

        Properties activemqConfigFromClasspath = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("motech-settings.conf")) {
            activemqConfigFromClasspath.load(in);
        }

        // take prefixing with username into consideration
        activemqConfigFromClasspath.setProperty("jms.queue.for.events", currentUserPrefix +
                activemqConfigFromClasspath.getProperty("jms.queue.for.events"));
        activemqConfigFromClasspath.setProperty("jms.queue.for.scheduler", currentUserPrefix +
                activemqConfigFromClasspath.getProperty("jms.queue.for.scheduler"));

        assertEquals(activemqConfigFromClasspath, activeMqConfig);
    }

}
