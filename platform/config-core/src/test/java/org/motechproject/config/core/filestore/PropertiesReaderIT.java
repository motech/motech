package org.motechproject.config.core.filestore;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PropertiesReaderIT {

    private final String activeMqProperties = "jms.queue.for.events=QueueForEvents;" +
            "jms.queue.for.scheduler=QueueForScheduler;" +
            "jms.topic.for.events=TopicForEvents;" +
            "jms.broker.url=tcp://localhost:61616;" +
            "jms.concurrentConsumers=1;" +
            "jms.maxConcurrentConsumers=10;" +
            "jms.session.cache.size=10;" +
            "jms.cache.producers=false;" +
            "motech.message.max.redelivery.count=3;" +
            "motech.message.redelivery.delay=1;" +
            "jms.username=;" +
            "jms.password=";

    @Test
    public void shouldReturnProperties() throws Exception {
        URL resource = getClass().getClassLoader().getResource("test.properties");
        String file = resource.getFile();
        Properties properties = PropertiesReader.getPropertiesFromFile(new File(file));
        assertNotNull(properties);
        assertThat(properties.getProperty("testkey"), Is.is("testvalue"));
    }

    @Test
    public void shouldParseStringToProperties() {
        Properties expectedProperties = new Properties();
        expectedProperties.setProperty("jms.queue.for.events", "QueueForEvents");
        expectedProperties.setProperty("jms.queue.for.scheduler", "QueueForScheduler");
        expectedProperties.setProperty("jms.topic.for.events", "TopicForEvents");
        expectedProperties.setProperty("jms.broker.url", "tcp://localhost:61616");
        expectedProperties.setProperty("jms.concurrentConsumers", "1");
        expectedProperties.setProperty("jms.maxConcurrentConsumers", "10");
        expectedProperties.setProperty("jms.session.cache.size", "10");
        expectedProperties.setProperty("jms.cache.producers", "false");
        expectedProperties.setProperty("motech.message.max.redelivery.count", "3");
        expectedProperties.setProperty("motech.message.redelivery.delay", "1");
        expectedProperties.setProperty("jms.username", "");
        expectedProperties.setProperty("jms.password", "");

        Properties properties = PropertiesReader.getPropertiesFromString(activeMqProperties);

        assertThat(properties, equalTo(expectedProperties));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionIfUnableToLoadTheFile() throws IOException {
        Properties properties = PropertiesReader.getPropertiesFromFile(new File("non_existing_filename"));
    }
}
