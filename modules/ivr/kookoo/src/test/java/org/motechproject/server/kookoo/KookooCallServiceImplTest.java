package org.motechproject.server.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.ivr.service.contract.CallRequest;
import org.motechproject.server.config.SettingsFacade;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;
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

        CallRequest callRequest = new CallRequest("1234567890", null, "http://localhost/tama/ivr/reply");
        callRequest.setStatusCallbackUrl("http://localhost/motech/kookoo/ivr/callstatus");
        ivrService.initiateCall(callRequest);

        verify(httpClient).executeMethod(argThat(new GetMethodMatcher(format("%s?api_key=%s&phone_no=%s&motech_call_id=%s&callback_url=%s&url=%s",
            KOOKOO_OUTBOUND_URL,
            API_KEY,
            "1234567890",
            callRequest.getCallId(),
            callRequest.getStatusCallbackUrl() + "?motech_call_id=" + callRequest.getCallId(),
            "http://localhost/tama/ivr/reply"
        ))));
    }

    @Test
    public void shouldCreateSessionForOutgoingCall() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("hero", "batman");

        CallRequest callRequest = new CallRequest("1234567890", params, "foobar");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate(callRequest.getCallId(), "1234567890")).thenReturn(flowSession);

        ivrService.initiateCall(callRequest);

        verify(flowSessionService).findOrCreate(callRequest.getCallId(), "1234567890");

        ArgumentCaptor<FlowSession> sessionCaptor = ArgumentCaptor.forClass(FlowSession.class);
        verify(flowSessionService).updateSession(sessionCaptor.capture());
        assertEquals("batman", flowSession.get("hero"));
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
