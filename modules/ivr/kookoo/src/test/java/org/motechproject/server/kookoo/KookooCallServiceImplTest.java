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
import org.motechproject.server.config.SettingsFacade;

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

        Properties ivrProperties = new Properties() {{
            setProperty(KookooCallServiceImpl.OUTBOUND_URL, "http://kookoo/outbound.php");
            setProperty(KookooCallServiceImpl.API_KEY, "api_key_value");
        }};

        SettingsFacade settings = new SettingsFacade();
        settings.saveConfigProperties("ivr.properties", ivrProperties);

        ivrService = new KookooCallServiceImpl(settings, httpClient);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitiateCallNullCallData() throws Exception {
        ivrService.initiateCall(null);
    }

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

    public void shouldMakeACallWithMandatoryAndCustomParameters() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("external_id", "external_id");
        params.put("hero", "batman");
        params.put(IVRService.CALL_TYPE, "outbox");

        ivrService.initiateCall(new CallRequest(phoneNumber, params, CALLBACK_URL));

        String apiKey = "api_key=api_key_value";
        String replyUrl = "&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FdataMap%3D%7B%22external_id%22%3A%22external_id%22%2C%22is_outbound_call%22%3A%22true%22%2C%22hero%22%3A%22batman%22%2C%22call_type%22%3A%22outbox%22%7D";
        String phoneNo = "&phone_no=9876543211";
        String callbackUrl = "&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id&call_type=outbox";
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?" + apiKey + replyUrl + phoneNo + callbackUrl)));
    }

    public class GetMethodMatcher extends ArgumentMatcher<GetMethod> {
        private String url;
        private Map<String, String> params = new HashMap<>();

        public GetMethodMatcher(String url) {
            this.url = url;
            params = getParamMap(url.split("[?]")[1]);
        }

        private HashMap<String, String> getParamMap(String s) {
            HashMap<String, String> params = new HashMap<>();
            for (String key : s.split("&")) {
                String [] values = key.split("=");
                params.put(values[0], values[1]);
            }
            return params;
        }

        @Override
        public boolean matches(Object o) {
            GetMethod getMethod = (GetMethod) o;
            try {
                String actualURL = getMethod.getURI().getURI();
                Map<String, String> actualParams = getParamMap(actualURL);

                System.out.println(params);
                System.out.println(actualParams);
                System.out.println(actualURL);
                System.out.println(url);
                return params.equals(actualParams) &&  actualURL.startsWith(url.split("[?]")[0]);
            } catch (URIException e) {
                return false;
            }
        }
    }

}
//http://kookoo/outbound.php?api_key=api_key_value&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FdataMap%3D%7B%22is_outbound_call%22%3A%22true%22%2C%22external_id%22%3A%22external_id%22%2C%22hero%22%3A%22batman%22%2C%22call_type%22%3A%22outbox%22%7D&phone_no=9876543211&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id&call_type=outbox
//http://kookoo/outbound.php?api_key=api_key_value&url=http%3A%2F%2Flocalhost%2Ftama%2Fivr%2Freply%3FdataMap%3D%7B%22external_id%22%3A%22external_id%22%2C%22hero%22%3A%22batman%22%2C%22is_outbound_call%22%3A%22true%22%2C%22call_type%22%3A%22outbox%22%7D&phone_no=9876543211&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id