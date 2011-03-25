/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
