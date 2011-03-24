package org.motechproject.server.service.ivr.astersik;

import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.ManagerCommunicationException;
import org.asteriskjava.live.NoSuchChannelException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.InitiateCallData;
import org.motechproject.server.service.ivr.CallInitiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;


/**
 * IVR Service Unit Tests
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testIVRAppContext.xml"})
public class TestIVRService {

    @Autowired
    private IVRServiceAsteriskImpl ivrService;

    @Test
    public void testInitiateCall() throws Exception {

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        ivrService.setAsteriskServer(asteriskServerMock);

        InitiateCallData initiateCallData = new InitiateCallData(1L,"1001", Integer.MAX_VALUE, "");

        ivrService.initiateCall(initiateCallData);

        Mockito.verify(asteriskServerMock, Mockito.times(1))
                .originateToApplicationAsync(eq(initiateCallData.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        eq((long)initiateCallData.getTimeOut()),
                        Mockito.any(MotechAsteriskCallBackImpl.class));

    }

    @Test (expected = CallInitiationException.class)
    public void testInitiateCallManagerException() throws Exception {

        InitiateCallData initiateCallData = new InitiateCallData(1L,"1001", Integer.MAX_VALUE,"");

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        Mockito.doThrow(new ManagerCommunicationException("", new Exception())).when(asteriskServerMock)
                .originateToApplicationAsync(
                        eq(initiateCallData.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        eq((long) initiateCallData.getTimeOut()),
                        Mockito.any(MotechAsteriskCallBackImpl.class));

        ivrService.setAsteriskServer(asteriskServerMock);

        ivrService.initiateCall(initiateCallData);

        Mockito.verify(asteriskServerMock, Mockito.times(1))
                .originateToApplicationAsync(eq(initiateCallData.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        eq((long)initiateCallData.getTimeOut()),
                        Mockito.any(MotechAsteriskCallBackImpl.class));

    }

@Test (expected = CallInitiationException.class)
    public void testInitiateCallChannelException() throws Exception {

        InitiateCallData initiateCallData = new InitiateCallData(1L,"0000", Integer.MAX_VALUE, "");

        AsteriskServer asteriskServerMock = mock(AsteriskServer.class);
        Mockito.doThrow(new NoSuchChannelException("no channel")).when(asteriskServerMock)
                .originateToApplicationAsync(
                        eq(initiateCallData.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        eq((long) initiateCallData.getTimeOut()),
                        Mockito.any(MotechAsteriskCallBackImpl.class));

        ivrService.setAsteriskServer(asteriskServerMock);

        ivrService.initiateCall(initiateCallData);

        Mockito.verify(asteriskServerMock, Mockito.times(1))
                .originateToApplicationAsync(eq(initiateCallData.getPhone()),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        eq((long)initiateCallData.getTimeOut()),
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
        int asteriskServerPort=99;
        String asteriskUserName="user";
        String asteriskUserPassword="password";

        IVRServiceAsteriskImpl ivrServiceAsterisk =
                new IVRServiceAsteriskImpl(asteriskServerHost, asteriskServerPort, asteriskUserName, asteriskUserPassword);

        AsteriskServer asteriskServer = ivrService.getAsteriskServer();

        assertNotNull(asteriskServer);

     }


}
