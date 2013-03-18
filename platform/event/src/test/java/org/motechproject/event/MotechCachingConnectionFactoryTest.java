package org.motechproject.event;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.jms.JMSException;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotechCachingConnectionFactoryTest {

    @Mock
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    private MotechCachingConnectionFactory motechCachingConnectionFactory = new MotechCachingConnectionFactory();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testUserPassword() throws JMSException {
        motechCachingConnectionFactory.setTargetConnectionFactory(activeMQConnectionFactory);

        motechCachingConnectionFactory.doCreateConnection();
        verify(activeMQConnectionFactory).createConnection();

        motechCachingConnectionFactory.setUsername("user");
        motechCachingConnectionFactory.doCreateConnection();
        verify(activeMQConnectionFactory).createConnection("user", null);

        motechCachingConnectionFactory.setPassword("password");
        motechCachingConnectionFactory.doCreateConnection();
        verify(activeMQConnectionFactory).createConnection("user", "password");
    }
}
