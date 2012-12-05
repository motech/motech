package org.motechproject.server.asterisk;

import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.ManagerCommunicationException;
import org.asteriskjava.live.NoSuchChannelException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.asterisk.callback.MotechAsteriskCallBackImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;

/**
 * IVR Service Unit Tests
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class IVRServiceIT {

    private static final String CALLBACK_URL = "http://localhost";

    @Autowired
    private IVRServiceAsteriskImpl ivrService;

    @Test
    public void testInitiateCall() throws Exception {

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        ivrService.setAsteriskServer(asteriskServerMock);

        CallRequest callRequest = new CallRequest("1001", Integer.MAX_VALUE, CALLBACK_URL);

        ivrService.initiateCall(callRequest);

        Mockito.verify(asteriskServerMock, Mockito.times(1))
                .originateToApplicationAsync(Matchers.eq(callRequest.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        anyLong(),
                        Mockito.any(MotechAsteriskCallBackImpl.class));
    }

    @Test(expected = CallInitiationException.class)
    public void testInitiateCallManagerException() throws Exception {

        CallRequest callRequest = new CallRequest("1001", Integer.MAX_VALUE, CALLBACK_URL);

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        Mockito.doThrow(new ManagerCommunicationException("", new Exception())).when(asteriskServerMock)
                .originateToApplicationAsync(
                        Matchers.eq(callRequest.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        anyLong(),
                        Mockito.any(MotechAsteriskCallBackImpl.class));

        ivrService.setAsteriskServer(asteriskServerMock);

        ivrService.initiateCall(callRequest);

        Mockito.verify(asteriskServerMock, Mockito.times(1))
                .originateToApplicationAsync(Matchers.eq(callRequest.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        anyLong(),
                        Mockito.any(MotechAsteriskCallBackImpl.class));
    }

    @Test(expected = CallInitiationException.class)
    public void testInitiateCallChannelException() throws Exception {

        CallRequest callRequest = new CallRequest("0000", Integer.MAX_VALUE, CALLBACK_URL);

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        Mockito.doThrow(new NoSuchChannelException("no channel")).when(asteriskServerMock)
                .originateToApplicationAsync(
                        Matchers.eq(callRequest.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        anyLong(),
                        Mockito.any(MotechAsteriskCallBackImpl.class));

        ivrService.setAsteriskServer(asteriskServerMock);

        ivrService.initiateCall(callRequest);

        Mockito.verify(asteriskServerMock, Mockito.times(1))
                .originateToApplicationAsync(Matchers.eq(callRequest.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        anyLong(),
                        Mockito.any(MotechAsteriskCallBackImpl.class));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testInitiateCallNullCallData() throws Exception {

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        ivrService.setAsteriskServer(asteriskServerMock);

        ivrService.initiateCall(null);
    }

    @Test
    public void testConstructorWithPort() throws Exception {

        String asteriskServerHost = "host";
        int asteriskServerPort = 99;
        String asteriskUserName = "user";
        String asteriskUserPassword = "password";

        IVRServiceAsteriskImpl ivrServiceAsterisk =
                new IVRServiceAsteriskImpl(asteriskServerHost, asteriskServerPort, asteriskUserName, asteriskUserPassword);

        AsteriskServer asteriskServer = ivrService.getAsteriskServer();

        assertNotNull(asteriskServer);
    }
}
