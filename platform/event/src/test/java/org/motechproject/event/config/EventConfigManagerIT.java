package org.motechproject.event.config;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private File configFile;

    private String currentUserPrefix;

    @Before
    public void setUp() throws IOException {
        currentUserPrefix = System.getProperty("user.name") + "_";

        configFile = File.createTempFile("activemq", "config");
        FileUtils.writeStringToFile(configFile,
                "queue.for.events=QueueForEvents\n" + "queue.for.scheduler=QueueForScheduler");
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(configFile);
    }

    @Test
    public void shouldLoadActivemqPropertiesFromFs() {
        eventConfigManager.setActivemqConfigLocation(configFile.getAbsolutePath());

        Properties activeMqConfig = eventConfigManager.getActivemqConfig();

        assertEquals(2, activeMqConfig.size());
        assertEquals(currentUserPrefix + "QueueForEvents", activeMqConfig.getProperty("queue.for.events"));
        assertEquals(currentUserPrefix + "QueueForScheduler", activeMqConfig.getProperty("queue.for.scheduler"));
    }

    @Test
    public void shouldFallbackToClasspathResource() throws IOException {
        eventConfigManager.setActivemqConfigLocation(configFile.getAbsolutePath());

        FileUtils.deleteQuietly(configFile);

        Properties activeMqConfig = eventConfigManager.getActivemqConfig();

        Properties activemqConfigFromClasspath = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("activemq.properties")) {
            activemqConfigFromClasspath.load(in);
        }

        // take prefixing with username into consideration
        activemqConfigFromClasspath.setProperty("queue.for.events", currentUserPrefix +
                activemqConfigFromClasspath.getProperty("queue.for.events"));
        activemqConfigFromClasspath.setProperty("queue.for.scheduler", currentUserPrefix +
                activemqConfigFromClasspath.getProperty("queue.for.scheduler"));

        assertEquals(activemqConfigFromClasspath, activeMqConfig);
    }
}
