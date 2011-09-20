/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package org.motechproject.ivr.kookoo;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.server.service.ivr.CallRequest;

/**
 * IVR Service Unit Tests
 * 
 */

public class KookooCallServiceImplTest {

	private KookooCallServiceImpl ivrService = new KookooCallServiceImpl();
	private final String CALLBACK_URL = "http://localhost/tama/ivr/reply";

	@Test
	public void testInitiateCall() throws Exception {

		CallRequest callRequest = new CallRequest("1001", null, CALLBACK_URL);
		ivrService = Mockito.spy(ivrService);
		Mockito.doNothing().when(ivrService)
				.dial(Mockito.anyString(), anyMap(), Mockito.anyString());

		ivrService.initiateCall(callRequest);
		Mockito.verify(ivrService, Mockito.times(1)).dial(
				Matchers.eq(callRequest.getPhone()), Mockito.anyMap(), Mockito.eq(CALLBACK_URL));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitiateCallNullCallData() throws Exception {

		ivrService.initiateCall(null);

	}

    private Properties properties;
    private String phoneNumber;
    @Mock
    private HttpClient httpClient;

    @Before
    public void setUp() {
        initMocks(this);
        phoneNumber = "9876543211";
        properties = new Properties();
        properties.setProperty(KookooCallServiceImpl.KOOKOO_OUTBOUND_URL, "http://kookoo/outbound.php");
        properties.setProperty(KookooCallServiceImpl.KOOKOO_API_KEY, "KKbedce53758c2e0b0e9eed7191ec2a466");
        ivrService = new KookooCallServiceImpl(properties, httpClient);
    }

    @Test
    public void shouldMakeACallWithThePhoneNumberAndEmptyTamaDataParamsProvided() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        ivrService.initiateCall(new CallRequest(phoneNumber, params, CALLBACK_URL));
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?api_key=KKbedce53758c2e0b0e9eed7191ec2a466&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FtamaData%3D%7B%7D&phone_no=9876543211")));
    }

    @Test
    public void shouldMakeACallWithPhoneNumberAndSomeTamaDataParams() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("hero", "batman");
        ivrService.initiateCall(new CallRequest(phoneNumber, params, CALLBACK_URL));
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?api_key=KKbedce53758c2e0b0e9eed7191ec2a466&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FtamaData%3D%7B%22hero%22%3A%22batman%22%7D&phone_no=9876543211")));
    }


    public class GetMethodMatcher extends ArgumentMatcher<GetMethod> {
        private String url;

        public GetMethodMatcher(String url) {
            this.url = url;
        }

        @Override
        public boolean matches(Object o) {
            GetMethod getMethod = (GetMethod) o;
            try {
                return getMethod.getURI().getURI().equals(url);
            } catch (URIException e) {
                return false;
            }
        }
    }

}
