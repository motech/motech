package org.motechproject.server.bootstrap;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.server.util.MessageBrokerPingUtil;


/**
 * Created by atish on 18/7/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageBrokerUtilTest {

    private static final String BROKER_URL = "vm://localhost?broker.persistent=false";

    private static final String INCORRECT_BROKER_URL = "tcp://failServer.com?broker.persistent=false";

    @Test
    public void verifyBrokerPingSuccess(){
        final boolean expected = true;
        boolean actual = MessageBrokerPingUtil.getInstance().test(BROKER_URL);
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void verifyBrokerPingFailure(){
        final boolean expected = false;
        boolean actual = MessageBrokerPingUtil.getInstance().test(INCORRECT_BROKER_URL);
        Assert.assertEquals(expected,actual);
    }
}
