package org.motechproject.server.verboice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.ivr.service.CallRequest;

import java.io.IOException;
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

        VerboiceIVRService ivrService = new VerboiceIVRService(verboiceProperties, httpClient);

        CallRequest callRequest = new CallRequest("1234567890", 1000, "foobar");
        ivrService.initiateCall(callRequest);

        verify(httpClient).executeMethod(argThat(new PostMethodMatcher("http://verboice:3000/api/call?channel=foobar&address=1234567890")));
    }

    public class PostMethodMatcher extends ArgumentMatcher<PostMethod> {

        private String url;

        public PostMethodMatcher(String url) {
            this.url = url;
        }

        @Override
        public boolean matches(Object o) {
            PostMethod postMethod = (PostMethod) o;
            try {
                String actualURL = postMethod.getURI().getURI();
                return actualURL.equals(url);
            } catch (URIException e) {
                return false;
            }
        }
    }
}
