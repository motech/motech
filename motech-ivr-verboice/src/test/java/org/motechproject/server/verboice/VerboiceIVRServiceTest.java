package org.motechproject.server.verboice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.ivr.service.CallRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VerboiceIVRServiceTest {

    @Mock
    private HttpClient httpClient;
    @Mock
    private Properties verboiceProperties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldInitiateCallOverVerboice() throws IOException {
        when(verboiceProperties.getProperty("host")).thenReturn("verboice");
        when(verboiceProperties.getProperty("port")).thenReturn("3000");
        when(verboiceProperties.getProperty("username")).thenReturn("test@test.com");
        when(verboiceProperties.getProperty("password")).thenReturn("password");
        when(httpClient.getParams()).thenReturn(new HttpClientParams());
        when(httpClient.getState()).thenReturn(new HttpState());
        VerboiceIVRService ivrService = new VerboiceIVRService(verboiceProperties, httpClient);

        CallRequest callRequest = new CallRequest("1234567890", 1000, "foobar");
        callRequest.setPayload(new HashMap<String, String>() {{
            put("callback_url", "key");
        }});
        ivrService.initiateCall(callRequest);

        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://verboice:3000/api/call?channel=foobar&address=1234567890&callback_url=key")));
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
                String actualURL = getMethod.getURI().getURI();
                return actualURL.equals(url);
            } catch (URIException e) {
                return false;
            }
        }
    }
}
