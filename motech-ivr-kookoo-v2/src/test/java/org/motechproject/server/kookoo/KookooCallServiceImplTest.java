package org.motechproject.server.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class KookooCallServiceImplTest {
    private KookooCallServiceImpl ivrService;
    private final String CALLBACK_URL = "http://localhost/tama/ivr/reply";
    private String phoneNumber;
    @Mock
    private HttpClient httpClient;

    @Before
    public void setUp() {
        initMocks(this);
        phoneNumber = "9876543211";
        Properties properties = new Properties();
        properties.setProperty(KookooCallServiceImpl.OUTBOUND_URL, "http://kookoo/outbound.php");
        properties.setProperty(KookooCallServiceImpl.API_KEY, "api_key_value");

        ivrService = new KookooCallServiceImpl(properties, httpClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitiateCallNullCallData() throws Exception {
        ivrService.initiateCall(null);
    }

    @Test
    public void shouldMakeACallWithMandatoryParameters() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("external_id", "external_id");
        params.put(IVRService.CALL_TYPE, "outbox");

        ivrService.initiateCall(new CallRequest(phoneNumber, params, CALLBACK_URL));

        String apiKey = "api_key=api_key_value";
        String replyUrl = "&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FdataMap%3D%7B%22external_id%22%3A%22external_id%22%2C%22is_outbound_call%22%3A%22true%22%2C%22call_type%22%3A%22outbox%22%7D";
        String phoneNo = "&phone_no=9876543211";
        String callbackUrl = "&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id&call_type=outbox";
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?" + apiKey + replyUrl + phoneNo + callbackUrl)));
    }

    @Test
    public void shouldMakeACallWithMandatoryAndCustomParameters() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("external_id", "external_id");
        params.put("hero", "batman");
        params.put(IVRService.CALL_TYPE, "outbox");

        ivrService.initiateCall(new CallRequest(phoneNumber, params, CALLBACK_URL));

        String apiKey = "api_key=api_key_value";
        String replyUrl = "&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FdataMap%3D%7B%22external_id%22%3A%22external_id%22%2C%22hero%22%3A%22batman%22%2C%22is_outbound_call%22%3A%22true%22%2C%22call_type%22%3A%22outbox%22%7D";
        String phoneNo = "&phone_no=9876543211";
        String callbackUrl = "&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id&call_type=outbox";
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?" + apiKey + replyUrl + phoneNo + callbackUrl)));
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
