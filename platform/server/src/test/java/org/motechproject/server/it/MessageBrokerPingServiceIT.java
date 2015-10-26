package org.motechproject.server.it;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.bootstrap.MessageBrokerPingService;
import org.motechproject.server.bootstrap.MessageBrokerPingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by atish on 18/7/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MessageBrokerPingServiceIT {



    private static final String BROKER_URL = "vm://localhost?broker.persistent=false";

    private static final String INCORRECT_BROKER_URL = "tcp://failServer.com?broker.persistent=false";

    @Configuration
    static class MessageBrokerTestServiceConfiguration {
        @Bean
        public MessageBrokerPingService messageBrokerPingService() {
            return new MessageBrokerPingServiceImpl();
        }
    }

    @Autowired
    private MessageBrokerPingService messageBrokerPingService;

    @Test
    public void verifyBrokerPingSuccess(){
        final boolean expected = true;
        boolean actual = messageBrokerPingService.pingBroker(BROKER_URL);
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void verifyBrokerPingFailure(){
        final boolean expected = false;
        boolean actual = messageBrokerPingService.pingBroker(INCORRECT_BROKER_URL);
        Assert.assertEquals(expected, actual);
    }

}
