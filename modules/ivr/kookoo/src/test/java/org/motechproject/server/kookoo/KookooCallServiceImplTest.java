package org.motechproject.server.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.config.SettingsFacade;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KookooCallServiceImplTest {

    public static final String API_KEY = "api_key_value";
    public static final String KOOKOO_OUTBOUND_URL = "http://kookoo/outbound.php";

    @Mock
    private HttpClient httpClient;
    @Mock
    private Properties kookooProperties;
    @Mock
    private FlowSessionService flowSessionService;

    private KookooCallServiceImpl ivrService;

    @Before
    public void setUp() {
        initMocks(this);

        Properties ivrProperties = new Properties();
        ivrProperties.setProperty(KookooCallServiceImpl.OUTBOUND_URL, "http://kookoo/outbound.php");
        ivrProperties.setProperty(KookooCallServiceImpl.API_KEY, "api_key_value");

        SettingsFacade settings = new SettingsFacade();
        settings.saveConfigProperties("ivr.properties", ivrProperties);

        ivrService = new KookooCallServiceImpl(settings, httpClient, flowSessionService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitiateCallNullCallData() throws Exception {
        ivrService.initiateCall(null);
    }

    @Test
    public void shouldMakeACallWithParameters() throws IOException {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate(anyString(), anyString())).thenReturn(flowSession);

        Map<String, String> params = new HashMap<>();
        params.put("external_id", "external_id");
        params.put("hero", "batman");
        params.put(IVRService.CALL_TYPE, "outbox");

        ivrService.initiateCall(new CallRequest("1234567890", params, "http://localhost/tama/ivr/reply"));

        verify(httpClient).executeMethod(argThat(new GetMethodMatcher(format("%s?api_key=%s&phone_no=%s&url=%s",
            KOOKOO_OUTBOUND_URL,
            API_KEY,
            "1234567890",
            "http://localhost/tama/ivr/reply?dataMap={\"external_id\":\"external_id\",\"hero\":\"batman\",\"is_outbound_call\":\"true\",\"call_type\":\"outbox\"}"
        ))));
    }

    @Test
    public void shouldCreateSessionForOutgoingCall() throws IOException {
        CallRequest callRequest = new CallRequest("1234567890", 1000, "foobar");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate(callRequest.getCallId(), "1234567890")).thenReturn(flowSession);

        ivrService.initiateCall(callRequest);

        verify(flowSessionService).findOrCreate(callRequest.getCallId(), "1234567890");
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
