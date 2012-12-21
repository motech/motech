package org.motechproject.ivr.kookoo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.service.KookooHttpRequestBuilder;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.kookoo.service.OutboundResponseParser;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KookooCallServiceImplTest {
    private KookooCallServiceImpl ivrService;
    private final String CALLBACK_URL = "http://localhost/tama/ivr/reply";
    private String phoneNumber;
    @Mock
    private HttpClient httpClient;
    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;
    @Mock
    private OutboundResponseParser responseParser;

    private Properties properties;

    @Before
    public void setUp() {
        initMocks(this);
        phoneNumber = "9876543211";
        properties = new Properties();
        properties.setProperty(KookooCallServiceImpl.OUTBOUND_URL, "http://kookoo/outbound.php");
        properties.setProperty(KookooCallServiceImpl.API_KEY, "api_key_value");

        ivrService = new KookooCallServiceImpl(properties, httpClient, new KookooHttpRequestBuilder(), responseParser, kookooCallDetailRecordsService);
        when(responseParser.isError(Matchers.<String>any())).thenReturn(false);
        when(kookooCallDetailRecordsService.createOutgoing(phoneNumber, CallDetailRecord.Disposition.UNKNOWN)).thenReturn("1234");
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
        String replyUrl = "&url=http://localhost/tama/ivr/reply?dataMap={\"external_id\":\"external_id\",\"is_outbound_call\":\"true\",\"call_type\":\"outbox\",\"call_detail_record_id\":\"1234\"}";
        String phoneNo = "&phone_no=9876543211";
        String callDetailRecordId = "&call_detail_record_id=1234";
        String callbackUrl = "&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id&call_type=outbox" + callDetailRecordId;
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
        String replyUrl = "&url=http://localhost/tama/ivr/reply?dataMap={\"external_id\":\"external_id\",\"hero\":\"batman\",\"is_outbound_call\":\"true\",\"call_type\":\"outbox\",\"call_detail_record_id\":\"1234\"}";
        String phoneNo = "&phone_no=9876543211";
        String callDetailRecordId = "&call_detail_record_id=1234";
        String callbackUrl = "&callback_url=http://localhost/tama/ivr/reply/callback?external_id=external_id&call_type=outbox" + callDetailRecordId;
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?" + apiKey + replyUrl + phoneNo + callbackUrl)));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowException_OnError() throws Exception {
        when(httpClient.executeMethod(Matchers.<GetMethod>any())).thenThrow(new IOException());
        ivrService.initiateCall(new CallRequest(phoneNumber, new HashMap<String, String>(), CALLBACK_URL));
    }

    @Test
    public void shouldMarkKookooCallDetailRecordAsFailed_WhenHTTPRequestFails() throws Exception {
        GetMethod getMethod = mock(GetMethod.class);
        KookooHttpRequestBuilder kookooHttpRequestBuilder = mock(KookooHttpRequestBuilder.class);
        ivrService = new KookooCallServiceImpl(properties, httpClient, kookooHttpRequestBuilder, responseParser, kookooCallDetailRecordsService);

        String responseBody = "somemessage";
        when(kookooHttpRequestBuilder.newGetMethod(Matchers.<String>any(), Matchers.<NameValuePair[]>any())).thenReturn(getMethod);
        when(getMethod.getResponseBodyAsString()).thenReturn(responseBody);
        when(responseParser.isError(responseBody)).thenReturn(true);
        when(responseParser.getMessage(responseBody)).thenReturn("some big error message");

        ivrService.initiateCall(new CallRequest(phoneNumber, new HashMap<String, String>(), CALLBACK_URL));

        verify(kookooCallDetailRecordsService).setCallRecordAsFailed(Matchers.<String>any(), Matchers.eq("some big error message"));
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
